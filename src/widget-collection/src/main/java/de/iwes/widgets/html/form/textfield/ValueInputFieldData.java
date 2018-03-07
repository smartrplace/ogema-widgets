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
		final boolean unitSet = (unit != null && !unit.isEmpty());
		if (((ValueInputField<N>) widget).numberType > 1) {
			if (unitSet)
				format = "%d " + unit;
			else
				format = "%d";
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
