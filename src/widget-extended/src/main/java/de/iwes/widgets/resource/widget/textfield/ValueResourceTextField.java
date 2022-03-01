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
package de.iwes.widgets.resource.widget.textfield;

import java.util.Locale;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A {@link ResourceTextField} that displays the value of a {@link SingleValueResource}, such as a 
 * {@link StringResource} or a {@link TemperatureResource}, and allows to edit it.<br>
 * 
 * Note: there is a separate {@link TimeResourceTextField} for displaying {@link TimeResource}s.
 * This widget simply print the long value of a time resource, i.e. the number of milliseconds since
 * 1 Jan 1970.
 * 
 * @param <V>
 */
public class ValueResourceTextField<V extends SingleValueResource> extends ResourceTextField<V> {

	private static final long serialVersionUID = 1L;
	private volatile int nrDecimals = 2;
	protected final Float defaultValue;

	public ValueResourceTextField(WidgetPage<?> page, String id) {
		this(page, id, null);
	}
	
	public ValueResourceTextField(WidgetPage<?> page, String id, V valueResource) {
		this(page, id, valueResource, (Float)null);
	}
	public ValueResourceTextField(WidgetPage<?> page, String id, V valueResource, Float defaultValue) {
		super(page, id, valueResource);
		this.defaultValue = defaultValue;
	}
	
	public ValueResourceTextField(OgemaWidget parent, String id, V valueResource, OgemaHttpRequest req) {
		this(parent, id, valueResource, null, req);
	}
	public ValueResourceTextField(OgemaWidget parent, String id, V valueResource, Float defaultValue, OgemaHttpRequest req) {
		super(parent, id, req);
		selectDefaultItem(valueResource);
		this.defaultValue = defaultValue;
	}
	
	@Deprecated
	public ValueResourceTextField(WidgetPage<?> page, String id, V valueResource, OgemaHttpRequest req) {
		super(page, id, req);
		selectDefaultItem(valueResource);
		this.defaultValue = null;
	}

	@Override
	public ValueResourceTextFieldData<V> createNewSession() {
		return new ValueResourceTextFieldData<>(this);
	}
	
	@Override
	protected String format(V resource, Locale locale) {
		final String output;
		if((!resource.exists()) && (defaultValue != null)) {
			final int nrDecimals = this.nrDecimals;
			if (nrDecimals < 0)
				output = String.format(Locale.ENGLISH, "%f", defaultValue);
			else
				output = ValueResourceUtils.getValue(defaultValue, nrDecimals); // default; override in derived class, if necessary // FIXME or use parameter?
			
		} else if (resource instanceof FloatResource) {
			final int nrDecimals = this.nrDecimals;
			if (nrDecimals < 0)
				output = String.format(Locale.ENGLISH, "%f", ((FloatResource) resource).getValue());
			else
				output = ValueResourceUtils.getValue((FloatResource) resource, nrDecimals); // default; override in derived class, if necessary // FIXME or use parameter?
		//} else if(resource instanceof TimeResource) {
		//	output = ValueResourceUtils.getValue(((TimeResource)resource).getValue()/(60*60000f), nrDecimals);
		} else
			output = ValueResourceUtils.getValue(resource);
		return output;
	}
	
	protected void setResourceValue(V resource, String value, OgemaHttpRequest req) {
		if(!(resource instanceof StringResource || resource instanceof BooleanResource)) {
			value = value.replaceAll("[^\\d.,-]", "");
		}
		// may throw different kinds of exceptions
		boolean exists = resource.exists();
		if(!exists) resource.create();
		if (resource instanceof TemperatureResource)  {
			((TemperatureResource) resource).setValue(Float.parseFloat(value) + 273.15F);
		//} else if(resource instanceof TimeResource) {
		//	((TimeResource) resource).setValue((long) (Double.parseDouble(value)*(60*60000l)));
		} else
			ValueResourceUtils.setValue(resource, value);
		if(!exists) resource.activate(false);
	}
	
	/**
	 * Relevant for FloatResources. 
	 * @param nr
	 * 		a negative value to show all digits, or a non-negative value to specify the
	 * 		number of digits to be shown.
	 */
	public void setNrDecimals(int nr) {
		this.nrDecimals = nr;
	}
	
}
