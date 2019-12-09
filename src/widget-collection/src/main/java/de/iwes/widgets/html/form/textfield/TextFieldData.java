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
package de.iwes.widgets.html.form.textfield;

import java.util.Collections;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TextFieldData extends WidgetData {
	
	public static final WidgetStyle<TextField> FORM_CONTROL = new WidgetStyle<TextField>("textField", Collections.singletonList("form-control"),0);
	public static final String TYPE_PASSWORD = "password";
	
	private String value = "";
//	private String valueEscaped = "";
	private String placeholder = "";
	private String type = null;
	private String inputmode = null;
	
	
	/*********** Constructor **********/
	
	public TextFieldData(TextField textField) {
		super(textField);
	}
	
	/******* Inherited methods ******/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject result = new JSONObject();
        if (type != null) {
        	result.put("type", type);
        }
        if (inputmode != null) {
        	result.put("inputmode", inputmode);
        }
        if (value !=null && !value.isEmpty()) {
        	result.put("value", value);
//        if (valueEscaped !=null && !valueEscaped.isEmpty()) {
//        	result.put("value", valueEscaped);
        } else {
        	result.put("placeholder", placeholder);
        }
        return result;
    }
	
	@Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject request = new JSONObject(data);
		String result = request.getString("data");
		if (((TextField) widget).valueAdmissible(result, req))
			setValue(result);
		return request;
    }	
	
	@Override
	protected String getWidthSelector() {
		return ">input";
	}
	
	/********** Public methods **********/
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		writeLock();
		try {
			this.value = value;
//			this.valueEscaped = value != null ? StringEscapeUtils.escapeHtml4(value) : null;
		} finally {
			writeUnlock();
		}
	}
	
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInputmode() {
		return inputmode;
	}
	public void setInputmode(String mode) {
		this.inputmode = mode;
	}


}
