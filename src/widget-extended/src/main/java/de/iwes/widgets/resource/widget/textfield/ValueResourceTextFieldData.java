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

import org.json.JSONObject;
import org.ogema.core.model.simple.SingleValueResource;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ValueResourceTextFieldData<V extends SingleValueResource> extends ResourceTextFieldData<V> {

	public ValueResourceTextFieldData(ValueResourceTextField<V> textField) {
		super(textField);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject request = new JSONObject(data);
		String value;
		try {
			value = request.getString("data");
		} catch (Exception e) {
			value = "SyntaxError; POST request in wrong format";
			return request;
		}
		try {
			V resource = getSelectedResource();
			((ValueResourceTextField<V>) widget).setResourceValue(resource, value, req);
//			setValue(value);
		} catch (Exception e) {
			// ignore -> we do not want to print user data to the log
			return request;
		}
		return request;
	}
	
}
