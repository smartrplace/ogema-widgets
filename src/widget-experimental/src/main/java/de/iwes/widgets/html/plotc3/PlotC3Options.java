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
package de.iwes.widgets.html.plotc3;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration.AxisType;
import de.iwes.widgets.html.plot.api.Plot2DOptions;
import de.iwes.widgets.html.plot.api.PlotType;

public class PlotC3Options extends Plot2DOptions<C3ChartConfiguration, C3DataSet> {

	// ?
	public static final PlotType GAUGE = new PlotType("gauge");
	public static final PlotType PIE = new PlotType("pie");
	public static final PlotType ODNUT = new PlotType("donut");

	C3ChartConfiguration config = new C3ChartConfiguration();

	/************* constructor **********************/

	public PlotC3Options(PlotC3 plot) {
		super(plot);
	}

	/******* Inherited methods ******/

	// http://c3js.org/reference.html
	public JSONObject getPlotData(Map<String,C3DataSet> data, OgemaHttpRequest req) {
		JSONObject result = config.toJSON();
		JSONObject axesMap = new JSONObject();
		JSONObject dt = new JSONObject();
		dt.put("type", map(configuration.getPlotType()));
		JSONObject types = new JSONObject();
		JSONArray colArr = new JSONArray();
		final JSONArray x = new JSONArray();
		for (C3DataSet row : dataSets.values()) {
			String id = row.getId();
			JSONArray[] arr = row.getData();
			x.put(row.getXLabel());
			colArr.put(arr[0]);	// first entry in xs is a string, the xLabel
			colArr.put(arr[1]);	// first entry in ys is a string, the yLabel
			axesMap.put(id,row.getXLabel());
		}
		dt.put("columns", colArr);
		dt.put("types", types);
		dt.put("xs", axesMap);
//		final JSONObject axis = new JSONObject();
//		final JSONObject xAxis = new JSONObject();
//		switch (configuration.getXAxisType0()) {
//		case TIME:
//			xAxis.put("type", "timeseries");
//			xAxis.put("label", "Time");
//			break;
//		case DEFAULT:
//			xAxis.put("type", "x");
//			break;
//		}
//		axis.put("x", xAxis);
//		result.put("axis", axis);

		result.put("data", dt);
		return result;
	}

	private static String map(PlotType type) {
		if (type.equals(PlotType.LINE_STACKED) || type.equals(PlotType.LINE_WITH_POINTS))
			type = PlotType.LINE;
		if (type.equals(PlotType.BAR_STACKED))
			type = PlotType.BAR;
		return type.equals(PlotType.POINTS) ? "scatter" : type.equals(PlotType.STEPS) ? "step" : type.getId();
	}

	@Override
	protected String getWidthSelector() {
		return ">div";
	}
	
}
