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

import java.util.Locale;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ValueInputFieldData<N extends Number> extends TextFieldData {

	private N value;
	private String unit;
	private short nrDecimals = 2;
	private double lowerBound = -Double.MAX_VALUE;
	private double upperBound = Double.MAX_VALUE;
	
	public ValueInputFieldData(ValueInputField<N> textField) {
		super(textField);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject obj = super.onPOST(data, req);
		String value = getValue().trim();
		if (value.isEmpty()) 
			setNumericalValue(null);
		else  {
			value = cutOffUnit(getValue().trim());
			N numVal = (N) convert(value, ((ValueInputField<N>) widget).numberType);
			if (numVal != null) {
				double val  =numVal.doubleValue();
				if (val < lowerBound || val > upperBound) 
					numVal = null;
			}
			setNumericalValue(numVal);
		}
		return obj;
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		if (value == null) 
			setValue("");
		else
			setValue(format(value));
		return super.retrieveGETData(req);
	}

	public N getNumericalValue() {
		return value;
	}

	public void setNumericalValue(N value) {
		this.value = value;
		if (value == null)
			setValue("");
		else
			setValue(value.toString());
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public short getNrDecimals() {
		return nrDecimals;
	}

	public void setNrDecimals(short nrDecimals) {
		this.nrDecimals = nrDecimals;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
	
	@SuppressWarnings("unchecked")
	protected String format(N value) {
		final String format;
		final String unit = this.unit;
		final short nrDecimals = this.nrDecimals;
		final boolean unitSet = (unit != null && !unit.isEmpty());
		if (((ValueInputField<N>) widget).numberType > 1) {
			if (unitSet)
				format = "%d " + unit;
			else
				format = "%d";
		} else if (nrDecimals < 0) {
			if (unitSet)
				format = "%f " + unit;
			else
				format = "%f";
		} else {
			if (unitSet)
				format = "%." + nrDecimals +"f " + unit;
			else
				format = "%." + nrDecimals +"f";
		}
		return String.format(Locale.ENGLISH, format, value);
	}
	
	protected static String cutOffUnit(String in) {
		final StringBuilder sb=  new StringBuilder();
		boolean firstDotFound = false;
		for (int i=0;i<in.length();i++) {
			char c = in.charAt(i);
			if (!Character.isDigit(c)) {
				if (i == 0 && c == '-') {
					sb.append(c);
					continue;
				}
				if (c == '.' && !firstDotFound) {
					firstDotFound = true;
					sb.append('.');
					continue;
				}
				return sb.toString();
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	protected static Object convert(String text, short key) {
		if (text == null || text.isEmpty())
			return null;
		switch (key) {
		case 0:
			return Float.parseFloat(text);
		case 2:
			return Integer.parseInt(text);
		case 3:
			return Long.parseLong(text);
		case 1: 
			return Double.parseDouble(text);
		case 4:
			return Short.parseShort(text);
		default:
			throw new IllegalArgumentException("Key not in range 0..4: " + key);
		}
	}
	
}
