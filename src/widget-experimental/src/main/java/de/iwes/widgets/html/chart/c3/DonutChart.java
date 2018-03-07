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
