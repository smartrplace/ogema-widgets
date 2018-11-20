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
package de.iwes.widgets.resource.widget.dropdown;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.resource.ResourceWidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdownData;

public class ResourceDropdownData<R extends Resource> extends TemplateDropdownData<R> implements ResourceWidgetData<R> {

	protected Class<? extends R> type;
	protected final UpdateMode updateMode;
	protected final ResourceAccess ra;
	
	public ResourceDropdownData(ResourceDropdown<R> dropdown, UpdateMode updateMode, ResourceAccess ra) {
		super(dropdown);
		this.updateMode = updateMode;
		this.ra = ra;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (updateMode == UpdateMode.AUTO_ON_GET) {
			writeLock();
			try {
				updateOnGet();
			} finally {
				writeUnlock();
			}
		}
		return super.retrieveGETData(req);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void updateOnGet() {
		List<R> resources;
		if (type == null)
			resources = Collections.emptyList();
		else
			resources = (List) ra.getResources(type);
		update(resources);
	}

	@Override
	public void setType(Class<? extends R> type) {
		writeLock();
		try {
			this.type = type;
			if (updateMode != UpdateMode.MANUAL) {
				updateOnGet();
			}
		} finally {
			writeUnlock();
		}
	}

	@Override
	public Class<? extends R> getType() {
		return type;
	}
	
	
}
