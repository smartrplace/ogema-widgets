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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class MeterData extends WidgetData {

	private float value = 0;
	private float min = 0;
	private float max = 1;
	private float low = 0;;
	private float high = max;
	private float optimum = 1;
	
	public MeterData(Meter meter) {
		super(meter);
	}

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject obj =new JSONObject();
		obj.put("value", value);
		obj.put("min", min);
		obj.put("max", max);
		obj.put("high", high);
		obj.put("low", low);
		obj.put("optimum", optimum);
		return obj;
	}
	
	@Override
	protected String getWidthSelector() {
		return ">meter";
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public float getOptimum() {
		return optimum;
	}

	public void setOptimum(float optimum) {
		this.optimum = optimum;
	}
	
}
