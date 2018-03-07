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

package de.iwes.widgets.object.widget.dropdown.deprecated;

import java.util.List;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.template.DefaultDisplayTemplate;

/**
 * A dropdown that allows to select a resource of a specified type. The list of resources to be displayed can be either
 * determined by the dropdown itself (then it shows all resources of a given type), or can be set explicitly.
 * See {@link UpdateMode}.
 *
 * @param <R>
 * 		the resource type
 * @deprecated use TemplateDropdown instead
 */
@Deprecated
public class ObjectDropdown<R> extends TemplateDropdown<R> {

	private static final long serialVersionUID = 1L;
	R defaultSelected = null;
	protected List<R> objectList;
	
	/**
	 * Manual update mode
	 * @param page
	 * @param id
	 */
	public ObjectDropdown(WidgetPage<?> page, String id) {
		this(page, id, false, null);
	}
	
	/**
	 * Manual update mode
	 * @param page
	 * @param id
	 * @param globalWidget
	 */
	public ObjectDropdown(WidgetPage<?> page, String id, boolean globalWidget) {
		this(page, id, globalWidget, null);
	}
	
	public ObjectDropdown(WidgetPage<?> page, String id, boolean globalWidget, List<R> objectList) {
		super(page, id, globalWidget);
		this.objectList = objectList;
		setTemplate(new DefaultDisplayTemplate<R>());
	}
	
	public ObjectDropdown(OgemaWidget parent, String id, List<R> objectList, OgemaHttpRequest req) {
		super(parent, id,req);
		this.objectList = objectList;
		setTemplate(new DefaultDisplayTemplate<R>());
	}
	
	@Override
	public ObjectDropdownData<R> createNewSession() {
		return new ObjectDropdownData<R>(this);
	}
	
	@Override
	public ObjectDropdownData<R> getData(OgemaHttpRequest req) {
		return (ObjectDropdownData<R>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ObjectDropdownData<R> opt2 = (ObjectDropdownData<R>) opt;
		opt2.setList(objectList);
		opt2.selectItem(defaultSelected); // works only if resources are set manually... then the default options have been set already
	}

	@Override
	public void selectDefaultItem(R resource) {
		this.defaultSelected = resource; 
	}

	public void setList(List<? extends R> objectList, OgemaHttpRequest req) {
		getData(req).setList(objectList);
	}

	public List<? extends R> getList(OgemaHttpRequest req) {
		return getData(req).getList();
	}
	
}
