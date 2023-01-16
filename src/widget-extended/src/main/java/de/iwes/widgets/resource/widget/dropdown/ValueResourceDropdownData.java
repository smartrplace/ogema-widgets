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

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.ogema.core.model.simple.SingleValueResource;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
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
		updateOnGET(req, null);
		return super.retrieveGETData(req);
	}
	
	@Deprecated
	protected void updateOnGET(Locale locale) {
		this.updateOnGET(null, locale);
	}
	
	@SuppressWarnings("unchecked")
	protected void updateOnGET(OgemaHttpRequest req, Locale locale) {
		update(displayedValues, null, req);
		String newValue;
		if (selectedResource == null) {
			update(ValueResourceDropdown.naList, null, req);
			newValue = "n.a."; // TODO configurable
		} else
			newValue = ((ValueResourceDropdown<V>) widget).getSelection(selectedResource, locale != null ? locale : req.getLocale().getLocale(), displayedValues);
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
