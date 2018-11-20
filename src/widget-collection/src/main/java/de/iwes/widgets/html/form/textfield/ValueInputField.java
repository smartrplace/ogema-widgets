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

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ValueInputField<N extends Number> extends TextField {
	
	private static final long serialVersionUID = 1L;
	private N defaultValue;
	private String defaultUnit;
	private Short defaultNrDecimals;
	private Double defaultLowerBound;
	private Double defaultUpperBound;
	
	protected final Class<N> type;
	protected final short numberType;

	/*
	 ********** Constructors ***********
	 */
	
	public ValueInputField(WidgetPage<?> page, String id, Class<N> type) {
		this(page, id, type, null);
	}
	
	public ValueInputField(WidgetPage<?> page, String id, Class<N> type, N defaultValue) {
		super(page, id);
		this.defaultValue = defaultValue;
		this.type = type;
		this.numberType = convertToEnum(type);
	}

	public ValueInputField(WidgetPage<?> page, String id, Class<N> type, boolean globalWidget) {
		this(page, id, globalWidget, type, null);
	}
	
	public ValueInputField(WidgetPage<?> page, String id, boolean globalWidget, Class<N> type, N defaultValue) {
		super(page, id, globalWidget);
		this.defaultValue = defaultValue;
		this.type = type;
		this.numberType = convertToEnum(type);
	}
	
	public ValueInputField(OgemaWidget parent, String id, Class<N> type, OgemaHttpRequest req) {
		super(parent, id, req);
		this.type = type;
		this.numberType = convertToEnum(type);
	}
	
	/*
	 ********** Inherited methods ***********
	 */
	
	@Override
	public ValueInputFieldData<N> createNewSession() {
		return new ValueInputFieldData<>(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ValueInputFieldData<N> getData(OgemaHttpRequest req) {
		return (ValueInputFieldData<N>) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(TextFieldData opt) {
		super.setDefaultValues(opt);
		@SuppressWarnings("unchecked")
		ValueInputFieldData<N> opt2 = (ValueInputFieldData<N>) opt;
		if (defaultValue != null)
			opt2.setNumericalValue(defaultValue);
		if (defaultUnit != null)
			opt2.setUnit(defaultUnit);
		if (defaultLowerBound != null)
			opt2.setLowerBound(defaultLowerBound);
		if (defaultUpperBound != null)
			opt2.setUpperBound(defaultUpperBound);
		if (defaultNrDecimals != null)
			opt2.setNrDecimals(defaultNrDecimals);
	}
	
	/*
	 ************** Public methods ************* 
	 */
	
	public void setDefaultNumericalValue(N value) {
		this.defaultValue = value;
	}
	
	public N getNumericalValue(OgemaHttpRequest req) {
		return getData(req).getNumericalValue();
	}

	public void setNumericalValue(N value,OgemaHttpRequest req) {
		getData(req).setNumericalValue(value);
	}
		
	public void setDefaultUnit(String unit) {
		this.defaultUnit = unit;
	}
	
	public String getUnit(OgemaHttpRequest req) {
		return getData(req).getUnit();
	}

	public void setUnit(String unit, OgemaHttpRequest req) {
		getData(req).setUnit(unit);
	}

	public void setDefaultNrDecimals(short nrDecimals) {
		this.defaultNrDecimals = nrDecimals;
	}
	
	public short getNrDecimals(OgemaHttpRequest req) {
		return getData(req).getNrDecimals();
	}

	/**
	 * Set the number of decimal digits. Pass a negative value to show all digits.
	 * @param nrDecimals
	 * @param req
	 */
	public void setNrDecimals(short nrDecimals, OgemaHttpRequest req) {
		getData(req).setNrDecimals(nrDecimals);
	}

	/**
	 * Inclusive
	 * @param lowerBound
	 */
	public void setDefaultLowerBound(double lowerBound) {
		this.defaultLowerBound = lowerBound;
	}
	
	public double getLowerBound(OgemaHttpRequest req) {
		return getData(req).getLowerBound();
	}

	public void setLowerBound(double lowerBound, OgemaHttpRequest req) {
		getData(req).setLowerBound(lowerBound);
	}

	public void setDefaultUpperBound(double upperBound) {
		this.defaultUpperBound = upperBound;
	}
	
	public double getUpperBound(OgemaHttpRequest req) {
		return getData(req).getUpperBound();
	}

	public void setUpperBound(double upperBound, OgemaHttpRequest req) {
		getData(req).setUpperBound(upperBound);
	}
	
	/**
	 * not supported by ValueInputField, use {@link #setDefaultNumericalValue(Number)} instead
	 */
	@Override
	public void setDefaultValue(String defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("setValue not supported by ValueInputField, use #setNumericalValue instead"); 
	}
	
	/**
	 * not supported by ValueInputField, use {@link #setNumericalValue(Number, OgemaHttpRequest)} instead
	 */
	@Override
	public void setValue(String value, OgemaHttpRequest req) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("setValue not supported by ValueInputField, use #setNumericalValue instead"); 
	}
	
	
	/*
	 *************** Internal methods ************
	 */ 
	private static final short convertToEnum(Class<?> type) {
		if (Float.class.isAssignableFrom(type) || Float.TYPE.isAssignableFrom(type))
			return 0;
		if (Integer.class.isAssignableFrom(type) || Integer.TYPE.isAssignableFrom(type))
			return 2;
		if (Long.class.isAssignableFrom(type) || Long.TYPE.isAssignableFrom(type))
			return 3;
		if (Double.class.isAssignableFrom(type) || Double.TYPE.isAssignableFrom(type))
			return 1;
		if (Short.class.isAssignableFrom(type) || Short.TYPE.isAssignableFrom(type))
			return 4;
		throw new IllegalArgumentException("Only numerical types allowed, got " + type);
	}
	
}
