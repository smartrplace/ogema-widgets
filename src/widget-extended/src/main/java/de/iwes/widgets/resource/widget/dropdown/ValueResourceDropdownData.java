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

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.ogema.core.model.simple.SingleValueResource;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdownData;

public class ValueResourceDropdownData<V extends SingleValueResource> extends TemplateDropdownData<String> {

	protected V selectedResource = null;
	protected List<String> displayedValues;

	public ValueResourceDropdownData(ValueResourceDropdown<V> dropDown,
			V selectedResource, List<String> displayedValues) {
		super(dropDown);
		this.selectedResource = selectedResource;
		this.displayedValues = displayedValues;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject result =  super.onPOST(data, req);
		String value = getSelectedValue();
		if (value == null ) {
			return result;
		}
		try {
			V resource = getSelectedResource();
			((ValueResourceDropdown<V>) widget).setResourceValue(resource, value, displayedValues);
//			setValue(value);
		} catch (Exception e) {
			// ignore -> we do not want to print user data to the log
		}
		return result;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		updateOnGET(null);
		return super.retrieveGETData(req);
	}
	
	@SuppressWarnings("unchecked")
	protected void updateOnGET(Locale locale) {
		update(displayedValues);
		String newValue;
		if (selectedResource == null) {
			update(ValueResourceDropdown.naList);
			newValue = "n.a."; // TODO configurable
		} else
			newValue = ((ValueResourceDropdown<V>) widget).getSelection(selectedResource, locale, displayedValues);
		selectSingleOption(newValue);
	}

	public V getSelectedResource() {
		return selectedResource;
	}

	public void setSelectedResource(V selectedResource) {
		this.selectedResource = selectedResource;
	}

	public void setDisplayedValues(List<String> displayedValues2) {
		this.displayedValues = displayedValues2;
	}
}
