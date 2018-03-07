/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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
