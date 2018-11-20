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
import de.iwes.widgets.html.chart.C3SimpleChart;
import de.iwes.widgets.html.chart.C3Chart.ChartType;

public class DonutChart extends C3SimpleChart {

	private String title = null;

	public DonutChart(WidgetPageBase<?> page, String id) {
		super(page, id, ChartType.DONUT);
	}

	@Override
	public JSONArray getConfig() {
		JSONArray array = null;

		if (title != null) {
			array = (array == null) ? new JSONArray() : array;
			array.put(new JSONObject().put("donut",
					new JSONObject().put("title", title)));
		}
		return array;
	}

	public DonutChart withTitle(String title) {
		this.title = title;
		return this;
	}
}
