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