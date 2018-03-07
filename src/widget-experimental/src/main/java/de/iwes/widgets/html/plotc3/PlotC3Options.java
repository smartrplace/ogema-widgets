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

package de.iwes.widgets.html.plotc3;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DOptions;
import de.iwes.widgets.html.plot.api.PlotType;

public class PlotC3Options extends Plot2DOptions<C3ChartConfiguration, C3DataSet> {

	public static final PlotType GAUGE = new PlotType("gauge");
	public static final PlotType PIE = new PlotType("pie");
	public static final PlotType ODNUT = new PlotType("donut");
	
	C3ChartConfiguration config = new C3ChartConfiguration();
	
	/************* constructor **********************/
	
	public PlotC3Options(PlotC3 plot) {
		super(plot);
	}
	
	/******* Inherited methods ******/

	public JSONObject getPlotData(Map<String,C3DataSet> data, OgemaHttpRequest req) {
		JSONObject result = config.toJSON();
		JSONObject axesMap = new JSONObject();
		JSONObject dt = new JSONObject();
		dt.put("type", configuration.getPlotType().toString().replace("linePoints", "line"));
		JSONObject types = new JSONObject();
		JSONArray colArr = new JSONArray();
		for (C3DataSet row : dataSets.values()) {
			String id = row.getId();
			JSONArray[] arr = row.getData();
			colArr.put(arr[0]);	// first entry in xs is a string, the xLabel
			colArr.put(arr[1]);	// first entry in ys is a string, the yLabel
			axesMap.put(id,row.getXLabel());
		}	
		dt.put("columns", colArr);
		dt.put("types", types);
		dt.put("xs", axesMap);
		result.put("data", dt);
		return result;
	}

}
