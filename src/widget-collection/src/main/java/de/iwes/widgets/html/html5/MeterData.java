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
