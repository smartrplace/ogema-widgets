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
package de.iwes.widgets.html.plotlyjs;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DOptions;

public class PlotlyjsOptions extends Plot2DOptions<PlotlyjsConfiguration,PlotlyjsDataSet> {

//	public static final TriggeredAction DOWNLOAD = new TriggeredAction("download");
	
	public PlotlyjsOptions(Plotlyjs plot) {
		super(plot);
	}

	@Override
	public JSONObject getPlotData(Map<String, PlotlyjsDataSet> data, OgemaHttpRequest req) { 
		JSONArray array = new JSONArray();
		for (PlotlyjsDataSet row : data.values()) {
			JSONObject seriesObj = new JSONObject();
			Map<String, Object> dt = row.getValues();
//			seriesObj.put("name", row.getId()); // label?
			seriesObj.put("data", dt);
			array.put(seriesObj);
		}
		JSONObject result = new JSONObject();
		result.put("data", array);
		result.put("options", configuration.toJSON());
		return result;
	}
	
	@Override
	protected String getWidthSelector() {
		return ">div";
	}
	
}
