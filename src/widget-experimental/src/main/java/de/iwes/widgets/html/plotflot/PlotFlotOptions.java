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
package de.iwes.widgets.html.plotflot;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DOptions;

public class PlotFlotOptions extends Plot2DOptions<FlotConfiguration,FlotDataSet> {

	public static final TriggeredAction DOWNLOAD = new TriggeredAction("download");
	
	public PlotFlotOptions(PlotFlot plot) {
		super(plot);
		// default values
		setWidth("100%");
		setHeight("300px");
	}

	@Override
	public JSONObject getPlotData(Map<String, FlotDataSet> data, OgemaHttpRequest req) { 
		JSONArray array = new JSONArray();
		
		for (FlotDataSet row : data.values()) {
			JSONObject seriesObj = new JSONObject();
			JSONArray dt = row.getValues();
			seriesObj.put("label", row.getId());
			seriesObj.put("data", dt);
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
		return ">div>#chart";
	}
	
}
