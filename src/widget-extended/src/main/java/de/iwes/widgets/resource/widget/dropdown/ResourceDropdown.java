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
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.resource.DefaultResourceTemplate;
import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.extended.resource.ResourceWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;

/**
 * A dropdown that allows to select a resource of a specified type. The list of resources to be displayed can be either
 * determined by the dropdown itself (then it shows all resources of a given type), or can be set explicitly.
 * See {@link UpdateMode}.
 *
 * @param <R>
 * 		the resource type
 */
public class ResourceDropdown<R extends Resource> extends TemplateDropdown<R> implements ResourceSelector<R>, ResourceWidget<R> {

	private static final long serialVersionUID = 1L;
	protected Class<? extends R> defaultType;
	protected final UpdateMode updateMode; 
	protected final ResourceAccess ra;
	R defaultSelected = null;
	
	/**
	 * Manual update mode
	 * @param page
	 * @param id
	 */
	public ResourceDropdown(WidgetPage<?> page, String id) {
		this(page, id, false, null, UpdateMode.MANUAL, null);
	}
	
	/**
	 * Manual update mode
	 * @param page
	 * @param id
	 * @param globalWidget
	 */
	public ResourceDropdown(WidgetPage<?> page, String id, boolean globalWidget) {
		this(page, id, globalWidget, null, UpdateMode.MANUAL, null);
	}
	public ResourceDropdown(OgemaWidget parent, String id, OgemaHttpRequest req) {
		this(parent, id, null, UpdateMode.MANUAL, null, req);
	}
	
	public ResourceDropdown(WidgetPage<?> page, String id, boolean globalWidget, Class<? extends R> defaultType, UpdateMode updateMode, ResourceAccess ra) {
		super(page, id, globalWidget);
		this.defaultType = defaultType;
		this.updateMode = updateMode;
		this.ra= ra;
		setTemplate(new DefaultResourceTemplate<R>());
	}
	
	public ResourceDropdown(OgemaWidget parent, String id, Class<? extends R> defaultType, UpdateMode updateMode, ResourceAccess ra, OgemaHttpRequest req) {
		super(parent, id,req);
		this.defaultType = defaultType;
		this.updateMode = updateMode;
		this.ra= ra;
		setTemplate(new DefaultResourceTemplate<R>());
	}
	
	@Override
	public ResourceDropdownData<R> createNewSession() {
		return new ResourceDropdownData<R>(this, updateMode, ra);
	}
	
	@Override
	public ResourceDropdownData<R> getData(OgemaHttpRequest req) {
		return (ResourceDropdownData<R>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ResourceDropdownData<R> opt2 = (ResourceDropdownData<R>) opt;
		opt2.setType(defaultType);
		opt2.selectItem(defaultSelected); // works only if resources are set manually... then the default options have been set already
	}

	@Override
	public void selectDefaultItem(R resource) {
		this.defaultSelected = resource; 
	}

	@Override
	public UpdateMode getUpdateMode() {
		return updateMode;
	}

	@Override
	public void setType(Class<? extends R> type, OgemaHttpRequest req) {
		getData(req).setType(type);
	}

	@Override
	public Class<? extends R> getType(OgemaHttpRequest req) {
		return getData(req).getType();
	}
	
	public void selectItemLocalized(R item, OgemaHttpRequest req) {
		for(R option: getItems(req)) {
			if(option.equalsLocation(item)) {
				selectItem(option, req);
				break;
			}
		}
	}
}
