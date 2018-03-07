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

package de.iwes.widgets.html.plotnvd3;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DOptions;

public class PlotNvd3Options extends Plot2DOptions<Nvd3Configuration,Nvd3DataSet> {

	public PlotNvd3Options(PlotNvd3 plot) {
		super(plot);
	}

	@Override
	public JSONObject getPlotData(Map<String, Nvd3DataSet> data, OgemaHttpRequest req) { 
		// FIXME instead of JSON array, use format specfiied at http://nvd3.org/examples/line.html
		JSONArray array = new JSONArray();
		
		for (Nvd3DataSet row : data.values()) {
			JSONObject seriesObj = new JSONObject();
			JSONArray dt = row.getValues();
			seriesObj.put("key", row.getId());
			seriesObj.put("values", dt);
			// TODO options such as color: 
//			seriesObj.put("color", "#ff7f0e");
			array.put(seriesObj);
		}
		JSONObject result = new JSONObject();
		result.put("data", array);
		boolean showOverview = configuration.isEnableOverviewPlot();
		result.put("enableOverviewPlot", showOverview);
		if (showOverview)
			result.put("overviewHeight", configuration.getOverviewHeight());	
		result.put("options", configuration.toJSON());
		return result;
	}
	
	@Override
	protected String getWidthSelector() {
		return ">svg#chart";
	}
	
}
