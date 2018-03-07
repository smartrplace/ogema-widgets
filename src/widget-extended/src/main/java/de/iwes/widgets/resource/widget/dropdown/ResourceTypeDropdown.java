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

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.Dropdown;

/**
 * A {@link Dropdown} that allows the user to select a resource type.
 * It is possible to pass explicitly a list of admissible types, or
 * to let the widget determine the available types. In the latter case,
 * the types of all available resources are parsed, which is inefficient.
 *
 */
public class ResourceTypeDropdown extends Dropdown {

	private static final long serialVersionUID = 1L;
	private final List<Class<? extends Resource>> defaultAllowedTypes;
	private final ResourceAccess ra;
	private final boolean filterAbstractTypes;

	/**
	 * Use this constructor to have the widget determine the available resource types
	 * @param page
	 * @param id
	 * @param ra
	 */
	public ResourceTypeDropdown(WidgetPage<?> page, String id, boolean filterAbstractTypes, ResourceAccess ra) {
		super(page, id);
		this.ra= ra;
		this.defaultAllowedTypes = null;
		this.filterAbstractTypes = filterAbstractTypes;
	}
	
	/**
	 * Use this constructor to specify explicitly the admissible resource types 
	 * @param page
	 * @param id
	 * @param defaultAllowedTypes
	 */
	public ResourceTypeDropdown(WidgetPage<?> page, String id, List<Class<? extends Resource>> defaultAllowedTypes) {
		super(page, id);
		this.ra = null;
		this.defaultAllowedTypes = new ArrayList<Class<? extends Resource>>(defaultAllowedTypes);
		this.filterAbstractTypes  =false;
	}

	/**
	 * @see #ResourceTypeDropdown(WidgetPage, String, ResourceAccess)
	 * @param parent
	 * @param id
	 * @param ra
	 * @param req
	 */
    public ResourceTypeDropdown(OgemaWidget parent, String id, boolean filterAbstractTypes, ResourceAccess ra, OgemaHttpRequest req) {
        super(parent, id, req);
		this.ra= ra;
		this.defaultAllowedTypes = null;
		this.filterAbstractTypes  =filterAbstractTypes;
    }
    
    /**
     * @see #ResourceTypeDropdown(WidgetPage, String, List)
     * @param page
     * @param id
     * @param defaultAllowedTypes
     * @param req
     */
	public ResourceTypeDropdown(WidgetPage<?> page, String id, List<Class<? extends Resource>> defaultAllowedTypes, OgemaHttpRequest req) {
		super(page, id);
		this.ra = null;
		this.defaultAllowedTypes = new ArrayList<Class<? extends Resource>>(defaultAllowedTypes);
		this.filterAbstractTypes  =false;
	}
	
	@Override
	public ResourceTypeDropdownData createNewSession() {
		return new ResourceTypeDropdownData(this, filterAbstractTypes, ra, defaultAllowedTypes);
	}
	
	@Override
	public ResourceTypeDropdownData getData(OgemaHttpRequest req) {
		return (ResourceTypeDropdownData) super.getData(req);
	}
	
	/*
	 ********* Public methods **********
	 */
	
	public Class<? extends Resource> getSelectedType(OgemaHttpRequest req) {
		return getData(req).getSelectedType();
	}
	
	/**
	 * @param req
	 * @return
	 */
	public List<Class<? extends Resource>> getAllowedTypes(OgemaHttpRequest req)  {
		return getData(req).getAllowedTypes();
	}
	
	/**
	 * @param allowedTypes
	 * @param req
	 * @throws IllegalStateException
	 * 		if the admissible types are managed by the widget (constructor with ResourceAccess has been used)
	 */
	public void setAllowedTypes(List<Class<? extends Resource>> allowedTypes, OgemaHttpRequest req)throws IllegalStateException  {
		getData(req).setAllowedTypes(allowedTypes);
	}
	
}
