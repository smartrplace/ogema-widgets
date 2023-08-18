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
		if (defaultSelected != null) {
			opt2.selectItem(defaultSelected);
			opt2.defaultSelected();
		}
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
