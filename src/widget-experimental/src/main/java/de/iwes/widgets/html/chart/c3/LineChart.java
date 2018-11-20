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
package de.iwes.widgets.html.chart.c3;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.html.chart.C3DataRow;
import de.iwes.widgets.html.chart.C3SimpleChart;
import de.iwes.widgets.html.chart.C3Chart.ChartType;

public class LineChart extends C3SimpleChart {

	private boolean subChart = false;
	private boolean zoom = false;

	public LineChart(WidgetPageBase<?> page, String id) {
		super(page, id, ChartType.LINE);
	}

	@Override
	public JSONArray getConfig() {

		JSONArray array = null;

		if (subChart == true) {
			array = (array == null) ? new JSONArray() : array;
			array.put(new JSONObject().put("subchart",
					new JSONObject().put("show", subChart)));
		}

		if (zoom == true) {
			array = (array == null) ? new JSONArray() : array;
			array.put(new JSONObject().put("zoom",
					new JSONObject().put("enabled", zoom)));
		}
		
		return array;
	}

	public LineChart withSubChart(boolean subChart) {
		this.subChart = true;
		return this;
	}

	public LineChart withZoom(boolean zoom) {
		this.zoom = true;
		return this;
	}

}
