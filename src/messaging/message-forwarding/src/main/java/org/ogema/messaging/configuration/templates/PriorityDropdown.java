/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ogema.messaging.configuration.templates;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.ogema.messaging.configuration.MessagePriority;
import org.ogema.messaging.configuration.Util;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.messaging.model.MessagingApp;
import de.iwes.widgets.messaging.model.UserConfig;

public class PriorityDropdown extends Dropdown {
	
	private static final long serialVersionUID = 1L;
	private final String userName;
	private final String serviceId;
	private final MessagingApp app;
	private final static Comparator<DropdownOption> PRIO_COMPARATOR = new Comparator<DropdownOption>() {
		
		@Override
		public int compare(DropdownOption drop0, DropdownOption drop1) {
			if (drop0 == drop1)
				return 0;
			return MessagePriority.valueOf(drop0.id()).getPriority() -  MessagePriority.valueOf(drop1.id()).getPriority();
		}
		
	};
	
	public PriorityDropdown(WidgetPage<?> page, String userName, String serviceId, String snippetId, MessagingApp app) {
		super(page, "UserId_" + ResourceUtils.getValidResourceName(userName) + "_ServiceId_" + 
				ResourceUtils.getValidResourceName(serviceId) + "_AppId_" + snippetId + "_PriorityDropdown", true);
		this.userName = userName;
		this.serviceId = serviceId;
		this.app = app;
		
		List<DropdownOption> options = new ArrayList<DropdownOption>();
		for (MessagePriority pr : MessagePriority.values()) {
			String p = pr.name();
			
			options.add(new DropdownOption(p, p, false));
		}
		options.get(0).select(true);
		this.setDefaultOptions(options);
		this.setComparator(PRIO_COMPARATOR);
	}

	@Override
	public void onGET(OgemaHttpRequest req) {
		UserConfig uc = Util.getUserConfig(userName, serviceId, app);
		MessagePriority mp = MessagePriority.NONE;
		
		if (uc != null) {
			int prio = uc.priority().getValue();
			mp = MessagePriority.forInteger(prio);
			if (mp == null)
				mp = MessagePriority.NONE;
		}
		selectSingleOption(mp.toString(), req);
	}
	
	@Override
	public void onPOSTComplete(String data, OgemaHttpRequest req) {
		MessagePriority prio = MessagePriority.valueOf(this.getSelectedValue(req));
		if (prio == null)
			prio = MessagePriority.NONE;
		UserConfig uc = Util.getUserConfig(userName, serviceId, app);
		if (prio == MessagePriority.NONE && uc != null)
			uc.delete();
		else if (prio != MessagePriority.NONE) {
			if (uc == null) {
				uc = Util.createUserConfig(userName, ResourceUtils.getValidResourceName(serviceId), app);
			}
			uc.priority().setValue(prio.getPriority());
			uc.activate(true);
		}
	}

}
