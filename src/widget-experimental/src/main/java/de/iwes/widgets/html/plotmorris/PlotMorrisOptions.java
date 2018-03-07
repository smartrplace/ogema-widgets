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

package de.iwes.widgets.html.plotmorris;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DOptions;

public class PlotMorrisOptions extends Plot2DOptions<MorrisChartConfiguration, MorrisDataSet> {
	
	public PlotMorrisOptions(PlotMorris plot) {
		super(plot);
	}
	
	
	@Override
	public JSONObject getPlotData(Map<String, MorrisDataSet> data, OgemaHttpRequest req) { 
		JSONArray array = new JSONArray();
		String[] ykeys = new String[data.size()];
		int counter = 0;
		for (MorrisDataSet row : data.values()) {
			ykeys[counter] = row.getId();
			JSONArray rowArray = row.getValues();
			for (int i=0;i<rowArray.length();i++) {  // putAll method?
				array.put(rowArray.getJSONObject(i));
			}
			counter++;
		}
		JSONObject result = configuration.toJSON();
		result.put("data", array);
		result.put("xkey", "t");
		result.put("ykeys", ykeys);
		result.put("labels", ykeys); // FIXME
		return result;
	}
	
}
