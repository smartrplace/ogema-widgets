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
