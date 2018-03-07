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
