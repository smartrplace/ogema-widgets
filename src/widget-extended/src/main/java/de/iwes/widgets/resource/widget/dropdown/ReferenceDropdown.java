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

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.dropdown.Dropdown;

/**
 * Allows to set a resource as a reference. 
 * If the base resource is set to null, this can also be used
 * to simply select a resource (the reference target) of a given
 * type. However, for this task a {@link ResourceDropdown} is recommended.
 */
// this can be used as a ResourceSelector or a pattern selector, but also
// in different ways. -> how to deal with interfaces?
// TODO convert to TemplateDropdown
public class ReferenceDropdown extends Dropdown {

	private static final long serialVersionUID = 1L;
	private final Alert alert;
	private final ApplicationManager am;

	/**
	 * 
	 * @param page
	 * @param id
	 * @param alert
	 * 		may be null, in which case no error messages are displayed.
	 */
	public ReferenceDropdown(WidgetPage<?> page, String id, ApplicationManager am, Alert alert) {
		super(page, id);
		this.alert = alert;
		this.am = am;
	}

	public ReferenceDropdown(OgemaWidget parent, String id, Alert alert, ApplicationManager am, OgemaHttpRequest req) {
		super(parent, id, req);
		this.alert = alert;
		this.am = am;
	}
	
	@Override
	public ReferenceDropdownData createNewSession() {
		return new ReferenceDropdownData(this, am, alert);
	}
	
	@Override
	public ReferenceDropdownData getData(OgemaHttpRequest req) {
		return (ReferenceDropdownData) super.getData(req);
	}
	
	/*
	 ********* Public methods ***********
	 */
	
	/**
	 * Get the base resource, which is to be set as a reference
	 * @param req
	 * @return
	 */
	public Resource getResource(OgemaHttpRequest req) {
		return getData(req).getResource();
	}

	/**
	 * @param resource
	 * 		the base resource, which is to be set as reference. May be null,
	 * 		in which case the widget only serves as a resource selector.
	 * @param targetType
	 * 		Must be a subtype of {@link org.ogema.core.model.Resource}, or
	 * 		{@link org.ogema.core.resourcemanager.pattern.ResourcePattern}
	 * @param req
	 */
	public void setResource(Resource resource, Class<?> targetType, OgemaHttpRequest req) {
		getData(req).setResource(resource, targetType);
	}

	public Class<?> getType(OgemaHttpRequest req) {
		return getData(req).getType();
	}
	
	/**
	 * Get the reference target resource, selected by the user
	 * @param req
	 * @return
	 */
	public Resource getReferenceTarget(OgemaHttpRequest req) {
		return getData(req).getReferenceTarget();
	}
	
}
