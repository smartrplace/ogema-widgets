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
