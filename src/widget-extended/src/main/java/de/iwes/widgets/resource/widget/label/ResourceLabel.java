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
package de.iwes.widgets.resource.widget.label;

import java.util.Locale;

import org.ogema.core.model.Resource;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.label.LabelData;

/**
 * A label that is modeled on resource. By default, it prints the resource path,
 * which can be changed by overriding the {@link #format(Resource, Locale)} method.
 * Note that there is also a {@link ValueResourceLabel}, which by default prints the
 * value of a SingleValueResource.  <br>
 * The selected resource is session-specific, in general, but there is the option to 
 * use a global resource (set parameter <code>globalWidget</code> in constructor), or to
 * define a default selection (see {@link #selectDefaultItem(Resource)}).
 *
 *@see ValueResourceLabel
 *
 * @param <T>
 * 		The resource type.
 */
public class ResourceLabel<T extends Resource> extends Label implements ResourceSelector<T> {

	private static final long serialVersionUID = 1L;
	private T defaultResource = null;

	public ResourceLabel(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public ResourceLabel(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	@Override
	public ResourceLabelData<T> createNewSession() {
		return new ResourceLabelData<T>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResourceLabelData<T> getData(OgemaHttpRequest req) {
		return (ResourceLabelData<T>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(LabelData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ResourceLabelData<T> opt2 = (ResourceLabelData<T>) opt;
		opt2.setSelectedResource(defaultResource);
	}
	
	/*
	 ******** to be overridden in derived class 
	 */
	
	/**
	 * Returns the value to be displayed on the user page. 
	 * Override this in derived class, if necessary.
	 * 
	 * Note: several specific derived ResourceTextFields already come
	 * with an adapted version of this, printing the resource value instead of 
	 * a name / the path of the resource.
	 */
	protected String format(T resource, Locale locale) {
		return ResourceUtils.getHumanReadableName(resource);
	}
	
	/*
	 ******** public methods *************
	 */

	@Override
	public void selectDefaultItem(T resource) throws UnsupportedOperationException {
		this.defaultResource = resource;
	}
	
	@Override
	public T getSelectedItem(OgemaHttpRequest req) {
		return getData(req).getSelectedResource();
	}
	
	@Override
	public void selectItem(T resource, OgemaHttpRequest req) throws UnsupportedOperationException {
		getData(req).setSelectedResource(resource);
	}
	
	
	// TODO misuse setDefaultValue to display some message if no resource is selected
	
	/**
	 * Not supported by ResourceLabel, use {@link #selectItem(Resource, OgemaHttpRequest)} instead
	 */
	@Override
	public void setText(String value, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by ResourceTextField");
	}

	protected String getEmptyLabel(Locale locale) {
		return "n.a.";
	}
	
}