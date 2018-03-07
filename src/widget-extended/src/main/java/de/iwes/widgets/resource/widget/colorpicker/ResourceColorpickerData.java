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

package de.iwes.widgets.resource.widget.colorpicker;

import org.json.JSONObject;
import org.ogema.core.model.units.ColourResource;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextFieldData;

public class ResourceColorpickerData extends TextFieldData {

	private ColourResource resource = null;
	
	public ResourceColorpickerData(ResourceColorpicker colorpicker) {
		super(colorpicker);
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (resource == null || !resource.isActive())
			disable();
		else {
			enable();
			String newVal = "#" + resource.getHexadecimal();
			setValue(newVal);
		}
		return super.retrieveGETData(req);
	}
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject result = super.onPOST(data, req);
		if (resource != null) {
			resource.setHexadecimal(getValue());
		}
		return result;
	}

	public ColourResource getResource() {
		return resource;
	}

	public void setResource(ColourResource resource) {
		this.resource = resource;
	}
	
	public String getColorString() {
		ColourResource res = getResource();
		if (res== null || !res.isActive()) 
			return null;
		return res.getHexadecimal();
	}
}
