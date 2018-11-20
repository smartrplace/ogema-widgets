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
package de.iwes.widgets.html.plotchartjs;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DOptions;
import de.iwes.widgets.html.plot.api.PlotType;

public class PlotChartjsOptions extends Plot2DOptions<ChartjsConfiguration,ChartjsDataSet> {

	public PlotChartjsOptions(PlotChartjs plot) {
		super(plot);
	}

	@Override
	public JSONObject getPlotData(Map<String, ChartjsDataSet> data, OgemaHttpRequest req) {
		JSONArray array = new JSONArray();
		final PlotType type = configuration.getPlotType();
		int colorCount = 0;
		for (ChartjsDataSet row : data.values()) {
			final JSONObject json = row.getValues();
			json.put("steppedLine", type == PlotType.STEPS);
			setColor(colorCount++, json);
			array.put(row.getValues());
		}
		JSONObject result = new JSONObject();
		final JSONObject dataObj = new JSONObject();
		dataObj.put("datasets", array);
		result.put("data", dataObj);
		result.put("options", configuration.toJSON());
		result.put("type", map(configuration.getPlotType()));
		return result;
	}

	private static void setColor(final int colorCnt, final JSONObject json) {
		final int rem = colorCnt % 3;
		final int reductionFactor = colorCnt / 6 + 1;
		final int r = 255 - (rem == 0 ? 0 : 255 / reductionFactor);
		final int g = 166 - (rem == 1 ? 0 : 166 / reductionFactor);
		final int b = 200 - (rem == 2 ? 0 : 200 / reductionFactor);
		final String rgba = "rgba(" + r + "," + g + "," + b;
		json.put("borderColor", rgba + ", 0.4)");
		json.put("backgroundColor", rgba + ", 0.1)");
	}

	@Override
	protected String getWidthSelector() {
		return ">canvas";
	}

	private static String map(PlotType type) {
		if (type == PlotType.LINE_WITH_POINTS || type == PlotType.STEPS || type == PlotType.LINE_STACKED)
			type = PlotType.LINE;
		if (type == PlotType.BAR_STACKED)
			type = PlotType.BAR;
		if (type == PlotType.LINE || type == PlotType.BAR)
			return type.getId();
		if (type == PlotType.POINTS)
			return "scatter";
		return "unknown";
	}

}
