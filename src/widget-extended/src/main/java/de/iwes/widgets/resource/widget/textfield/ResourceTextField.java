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

package de.iwes.widgets.resource.widget.textfield;

import java.util.Locale;

import org.ogema.core.model.Resource;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.extended.resource.ResourceSelector;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.form.textfield.TextFieldData;

/**
 * A special {@link TextField} that displays data associated to a resource. It uses a default method to 
 * guess a sensible name for a resource to be displayed, this can be overridden in a derived class, however,
 * for instance to always display the path of the resource. See {@link #format(Resource, Locale)}.
 * <br>
 * In its basic form, the ResourceTextField does not support POST requests, so widgets that need POST should
 * override the {@link ResourceTextFieldData#onPOST(String, OgemaHttpRequest)} method. 
 * There are predefined derived classes which support POST, though, in particular the
 * {@link ValueResourceTextField} and {@link TimeResourceTextField}, which display the value of a 
 * {@see org.ogema.core.model.simple.SingleValueResource} and {@see org.ogema.core.model.simple.SingleValueResource}, respectively.
 *
 * @param <T>
 */
public abstract class ResourceTextField<T extends Resource> extends TextField implements ResourceSelector<T> {

	private static final long serialVersionUID = 1L;
	private T defaultResource = null;

	public ResourceTextField(WidgetPage<?> page, String id) {
		this(page, id, (T) null);
	}
	
	public ResourceTextField(WidgetPage<?> page, String id, T defaultResource) {
		super(page, id);
		this.defaultResource = defaultResource;
	}
	
	public ResourceTextField(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	@Deprecated
	public ResourceTextField(WidgetPage<?> page, String id, OgemaHttpRequest req) {
		super(page, id, req);
	}
	
	@Override
	public ResourceTextFieldData<T> createNewSession() {
		return new ResourceTextFieldData<T>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResourceTextFieldData<T> getData(OgemaHttpRequest req) {
		return (ResourceTextFieldData<T>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(TextFieldData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ResourceTextFieldData<T> opt2 = (ResourceTextFieldData<T>) opt;
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
	
	/**
	 * Provide a value for display when no resource is selected.
	 * @param locale
	 * @return
	 */
	protected String getEmptyLabel(Locale locale) {
		return "n.a.";
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
	 * Not supported by ResourceTextField
	 */
	@Override
	public void setValue(String value, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by ResourceTextField");
	}
	
}
