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

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;

public class ResourceListDropdownData<R extends Resource> extends ResourceDropdownData<R> {
	
	protected volatile ResourceList<R> list = null;
	
	public ResourceListDropdownData(ResourceListDropdown<R> dropdown, UpdateMode updateMode, ResourceAccess ra) {
		super(dropdown,updateMode,ra);
	}
	
	protected void updateOnGet() {
		List<R> resources;
		if (list == null)
			resources = Collections.emptyList();
		else  
			resources = list.getAllElements();
		update(resources);
	}

	public ResourceList<R> getList() {
		return list;
	}

	public void setList(ResourceList<R> list) {
		this.list = list;
		updateOnGet();
	}

}
