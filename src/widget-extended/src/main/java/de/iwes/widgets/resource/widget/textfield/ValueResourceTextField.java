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
	private int nrDecimals = 2;

	public ValueResourceTextField(WidgetPage<?> page, String id) {
		this(page, id, null);
	}
	
	public ValueResourceTextField(WidgetPage<?> page, String id, V valueResource) {
		super(page, id, valueResource);
	}
	
	public ValueResourceTextField(OgemaWidget parent, String id, V valueResource, OgemaHttpRequest req) {
		super(parent, id, req);
		selectDefaultItem(valueResource);
	}
	
	@Deprecated
	public ValueResourceTextField(WidgetPage<?> page, String id, V valueResource, OgemaHttpRequest req) {
		super(page, id, req);
		selectDefaultItem(valueResource);
	}

	@Override
	public ValueResourceTextFieldData<V> createNewSession() {
		return new ValueResourceTextFieldData<>(this);
	}
	
	@Override
	protected String format(V resource, Locale locale) {
		String output;
		if (resource instanceof FloatResource)
			output = ValueResourceUtils.getValue((FloatResource) resource, nrDecimals); // default; override in derived class, if necessary // FIXME or use parameter?
		else
			output = ValueResourceUtils.getValue(resource);
		return output;
	}
	
	protected void setResourceValue(V resource, String value, OgemaHttpRequest req) {
		if(!(resource instanceof StringResource || resource instanceof BooleanResource)) {
			value = value.replaceAll("[^\\d.,-]", "");
		}
		// may throw different kinds of exceptions
		if (resource instanceof TemperatureResource)  {
			((TemperatureResource) resource).setValue(Float.parseFloat(value) + 273.15F);
		}
		else
			ValueResourceUtils.setValue(resource, value);		
	}
	
	/**
	 * Relevant for FloatResources
	 * @param nr
	 */
	public void setNrDecimals(int nr) {
		if (nr < 0)
			nrDecimals = 0;
		else
			nrDecimals = nr;
	}
	
}
