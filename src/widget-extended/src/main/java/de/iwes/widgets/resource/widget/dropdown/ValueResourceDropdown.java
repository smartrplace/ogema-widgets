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
import java.util.Locale;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;

/**
 * A {@link Dropdown} that is used to display the value of a {@link SingleValueResource}, such as a 
 * {@link StringResource} or a {@link IntegerResource}, and allows to edit it.<br>
 * 
 * Note: Only predefined values (the so-called displayValues) are offered by the dropdown
 * and only these values can be selected. IntegerResources in the standard case are
 * expected to provide the index within the displayValues list (starting at index zero).
 * 
 * @param <V>
 */
public class ValueResourceDropdown<V extends SingleValueResource> extends TemplateDropdown<String> {

	private static final long serialVersionUID = 1L;
	private V defaultResource = null;
	protected List<String> defaultDisplayedValues = null;
	public static List<String> naList = new ArrayList<>();
	static {
		naList.add("n/a");
	}

	public ValueResourceDropdown(WidgetPage<?> page, String id) {
		this(page, id, null, null);
	}
	
	public ValueResourceDropdown(WidgetPage<?> page, String id, V valueResource,
			List<String> displayedValues) {
		super(page, id);
		this.defaultResource = valueResource;
		this.defaultDisplayedValues = displayedValues;
	}
	
	public ValueResourceDropdown(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	public ValueResourceDropdown(OgemaWidget parent, String id, V valueResource,
			List<String> displayedValues, OgemaHttpRequest req) {
		super(parent, id, req);
		selectDefaultItem(valueResource);
		setDefaultDisplayedValues(displayedValues);
	}
	
	@Override
	public ValueResourceDropdownData<V> createNewSession() {
		return new ValueResourceDropdownData<>(this, defaultResource, defaultDisplayedValues);
	}
	
	protected void setDefaultValues(DropdownData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ValueResourceDropdownData<V> opt2 = (ValueResourceDropdownData<V>) opt;
		opt2.setSelectedResource(defaultResource);
	}

	public void selectDefaultItem(V resource) {
		this.defaultResource = resource;
	}
	@SuppressWarnings("unchecked")
	public void selectItem(V resource, OgemaHttpRequest req) {
		((ValueResourceDropdownData<V>)getData(req)).setSelectedResource(resource);
	}
	@SuppressWarnings("unchecked")
	public V getSelectedResource(OgemaHttpRequest req) {
		return ((ValueResourceDropdownData<V>)getData(req)).getSelectedResource();
	}
	public void setDefaultDisplayedValues(List<String> displayedValues) {
		this.defaultDisplayedValues = displayedValues;
	}
	@SuppressWarnings("unchecked")
	public void setDisplayedValues(List<String> displayedValues, OgemaHttpRequest req) {
		((ValueResourceDropdownData<V>)getData(req)).setDisplayedValues(displayedValues);
	}

	/** override this if required*/
	public String getSelection(V resource, Locale locale, List<String> displayedValues) {
		if (resource instanceof IntegerResource)  {
			int val = ((IntegerResource)resource).getValue();
			if(val <= 0) {
				return displayedValues.get(0);
			} else if(val >= displayedValues.size()) {
				return displayedValues.get(displayedValues.size()-1);
			} else {
				return displayedValues.get(val);
			}
		}
		else
			return ValueResourceUtils.getValue(resource);	
	}

	/** override this if required*/
	protected void setResourceValue(V resource, String value, List<String> displayedValues) {
		//if(!(resource instanceof StringResource)) {
		//	value = value.replaceAll("[^\\d.,-]", "");
		//}
		// may throw different kinds of exceptions
		int i = 0;
		if (resource instanceof IntegerResource)  {
			for(String s: displayedValues) {
				if(s.equals(value)) {
					((IntegerResource) resource).setValue(i);					
				}
				i++;
			}
		}
		else
			ValueResourceUtils.setValue(resource, value);		
	}
}
