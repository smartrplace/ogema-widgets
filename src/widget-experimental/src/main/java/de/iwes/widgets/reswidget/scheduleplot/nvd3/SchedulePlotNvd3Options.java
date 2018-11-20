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
package de.iwes.widgets.reswidget.scheduleplot.nvd3;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotnvd3.Nvd3DataSet;
import de.iwes.widgets.html.plotnvd3.PlotNvd3Options;

public class SchedulePlotNvd3Options extends PlotNvd3Options {
	
	private final ScheduleDataNvd3 scheduleData;
	
	/***** Constructor ****/
	
	public SchedulePlotNvd3Options(SchedulePlotNvd3 plot) {
		super(plot);
		scheduleData = new ScheduleDataNvd3(plot.maxValuesSupplier, plot.bufferWindow);
	}

	/*** Inherited methods ***/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		Map<String,Nvd3DataSet> backup = new LinkedHashMap<>(dataSets);
		// TODO implement reduction? Will be done automatically by super function, but may be inefficient
		dataSets.putAll(scheduleData.getAllDataSets(configuration.isScale(), configuration.getYminFilter(), configuration.getYmaxFilter(),req)); 
		if (!configuration.isPlotTypeSetExplicitly())
			configuration.setPlotType(scheduleData.getCurrentType(), true);
		JSONObject result = super.retrieveGETData(req);
		dataSets.clear();
		dataSets.putAll(backup);
		return result;
	}
	
	/**** Public methods ***/
	
	public ScheduleDataNvd3 getScheduleData() {
		return scheduleData;
	}
	
}
