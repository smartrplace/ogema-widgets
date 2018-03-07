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

package de.iwes.widgets.html.html5;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Wraps an HMTL5 <code><meter></code>. This is particularly suited to represent the 
 * State of charge of some device.<br>
 * To enable auto-updating of the client (HTML) side, call
 * {link {@link OgemaWidget#setDefaultPollingInterval(long)},
 * and pass a positive number (update interval in milliseconds). 
 *
 */
public class Meter extends OgemaWidgetBase<MeterData> {
		
	private static final long serialVersionUID = 1L;

	private float defaultValue = 0;
	private float defaultMin = 0;
	private float defaultMax = 1;
	private float defaultLow  =0;
	private float defaultHigh = 1;
	private float defaultOptimum =1;
	
	/*
	 ********** Constructors ***********
	 */

	public Meter(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public Meter(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	public Meter(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	/*
	 ********** Inherited methods ***********
	 */
	

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return Meter.class;
	}

	@Override
	public MeterData createNewSession() {
		return new MeterData(this);
	}
	
	@Override
	protected void setDefaultValues(MeterData opt) {
		super.setDefaultValues(opt);
		opt.setValue(defaultValue);
		opt.setMin(defaultMin);
		opt.setMax(defaultMax);
		opt.setLow(defaultLow);
		opt.setHigh(defaultHigh);
		opt.setOptimum(defaultOptimum);
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "Meter", "/ogema/widget/html5/Meter.js");
	}
	
	/*
	 ************** Public methods *************
	 */

	public void setDefaultValue(float value) {
		this.defaultValue = value;
	}

	public void setDefaultMin(float min) {
		this.defaultMin = min;
	}
	
	public void setDefaultMax(float max) {
		this.defaultMax = max;
	}
	
	public void setDefaultLow(float low) {
		this.defaultLow = low;
	}
	
	public void setDefaultHigh(float high) {
		this.defaultHigh = high;
	}
	
	public void setDefaultOptimum(float optimum) {
		this.defaultOptimum = optimum;
	}
	
	public float getValue(OgemaHttpRequest req) {
		return getData(req).getValue();
	}

	public void setValue(float value,OgemaHttpRequest req) {
		getData(req).setValue(value);
	}

	public float getMin(OgemaHttpRequest req) {
		return getData(req).getMin();
	}

	public void setMin(float min,OgemaHttpRequest req) {
		getData(req).setMin(min);
	}

	public float getMax(OgemaHttpRequest req) {
		return getData(req).getMax();
	}

	public void setMax(float max,OgemaHttpRequest req) {
		getData(req).setMax(max);
	}
	
	public float getLow(OgemaHttpRequest req) {
		return getData(req).getLow();
	}
	
	public void setLow(float low, OgemaHttpRequest req) {
		getData(req).setLow(low);
	}

	public float getHigh(OgemaHttpRequest req) {
		return getData(req).getHigh();
	}

	public void setHigh(float high, OgemaHttpRequest req) {
		getData(req).setHigh(high);
	}

	public float getOptimum(OgemaHttpRequest req) {
		return getData(req).getOptimum();
	}

	public void setOptimum(float optimum, OgemaHttpRequest req) {
		getData(req).setOptimum(optimum);
	}
	
}
