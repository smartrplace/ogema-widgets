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

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownData;

/**
 * A dropdown that allows to select a resource from a resource list. Only update mode
 * {@link UpdateMode#AUTO_ON_GET} is supported, for other update modes use a
 * {@link ResourceDropdown} instead.
 *
 * @param <R>
 * 		the resource type
 */
public class ResourceListDropdown<R extends Resource> extends ResourceDropdown<R> {

	private static final long serialVersionUID = 1L;
	protected ResourceList<R> defaultList = null;
	

	public ResourceListDropdown(WidgetPage<?> page, String id, boolean globalWidget) {
		this(page, id, globalWidget, null);
	}
	
	public ResourceListDropdown(WidgetPage<?> page, String id, boolean globalWidget, ResourceList<R> defaultList) {
		super(page, id, globalWidget, null, UpdateMode.AUTO_ON_GET, null);
		this.defaultList = defaultList;
	}
	
	public ResourceListDropdown(OgemaWidget parent, String id, OgemaHttpRequest req, ResourceList<R> defaultList) {
		super(parent, id, null, UpdateMode.AUTO_ON_GET, null, req);
		this.defaultList = defaultList;
	}
	
	@Override
	public ResourceListDropdownData<R> createNewSession() {
		return new ResourceListDropdownData<R>(this, updateMode, ra);
	}
	
	@Override
	public ResourceListDropdownData<R> getData(OgemaHttpRequest req) {
		return (ResourceListDropdownData<R>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ResourceListDropdownData<R> opt2 = (ResourceListDropdownData<R>) opt;
		opt2.setList(defaultList);
	}
	
	public void setDefaultList(ResourceList<R> list) {
		this.defaultList = list;
	}
	
	public ResourceList<R> getList(OgemaHttpRequest req) {
		return getData(req).getList();
	}

	public void setList(ResourceList<R> list, OgemaHttpRequest req) {
		getData(req).setList(list);
	}

	/**
	 * Not supported by ResourceListDropdown
	 */
	@Override
	public boolean addItem(R item, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by ResourceListDropdown");
	}
	
	/**
	 * Not supported by ResourceListDropdown
	 */
	@Override
	public boolean removeItem(R item, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by ResourceListDropdown");
	}
	
	/**
	 * Not supported by ResourceListDropdown
	 */
	@Override
	public void setType(Class<? extends R> type, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by ResourceListDropdown");
	}

	@Override
	public Class<? extends R> getType(OgemaHttpRequest req) {
		if (defaultList != null)
			return defaultList.getElementType();
		else
			return null;
	}
	
}
