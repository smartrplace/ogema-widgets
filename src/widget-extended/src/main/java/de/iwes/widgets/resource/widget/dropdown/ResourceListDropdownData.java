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

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ResourceListDropdownData<R extends Resource> extends ResourceDropdownData<R> {
	
	protected volatile ResourceList<R> list = null;
	
	public ResourceListDropdownData(ResourceListDropdown<R> dropdown, UpdateMode updateMode, ResourceAccess ra) {
		super(dropdown,updateMode,ra);
	}
	
	@Deprecated
	protected void updateOnGet() {
		this.updateOnGet(null);
	}
	
	protected void updateOnGet(OgemaHttpRequest req) {
		List<R> resources;
		if (list == null)
			resources = Collections.emptyList();
		else  
			resources = list.getAllElements();
		update(resources, null, req);
	}

	public ResourceList<R> getList() {
		return list;
	}

	@Deprecated
	public void setList(ResourceList<R> list) {
		this.setList(list, null);
	}

	
	public void setList(ResourceList<R> list, OgemaHttpRequest req) {
		this.list = list;
		updateOnGet(req);
	}

}
