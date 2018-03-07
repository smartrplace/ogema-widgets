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


}
