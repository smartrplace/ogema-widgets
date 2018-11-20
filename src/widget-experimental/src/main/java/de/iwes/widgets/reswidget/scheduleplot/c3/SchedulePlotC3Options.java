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
package de.iwes.widgets.reswidget.scheduleplot.c3;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotc3.C3DataSet;
import de.iwes.widgets.html.plotc3.PlotC3Options;

public class SchedulePlotC3Options extends PlotC3Options {

	private final ScheduleDataC3 scheduleData = new ScheduleDataC3();

	/***** Constructor ****/

	public SchedulePlotC3Options(SchedulePlotC3 plot) {
		super(plot);
	}

	/*** Inherited methods ***/

	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		Map<String,C3DataSet> backup = new LinkedHashMap<String, C3DataSet>(dataSets);
		dataSets.putAll(scheduleData.getAllDataSets(Long.MIN_VALUE,Long.MAX_VALUE,Integer.MAX_VALUE,req)); // TODO implement reduction? Will be done automatically by super function, but may be inefficient
		if (!configuration.isPlotTypeSetExplicitly())
			configuration.setPlotType(scheduleData.getCurrentType());
		JSONObject result = super.retrieveGETData(req);
		dataSets.clear();
		dataSets.putAll(backup);
		return result;
	}


	/**** Public methods ***/

	public ScheduleDataC3 getScheduleData() {
		return scheduleData;
	}

}
