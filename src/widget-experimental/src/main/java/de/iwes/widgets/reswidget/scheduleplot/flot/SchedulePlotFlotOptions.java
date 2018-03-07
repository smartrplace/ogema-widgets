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

package de.iwes.widgets.reswidget.scheduleplot.flot;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotflot.FlotDataSet;
import de.iwes.widgets.html.plotflot.PlotFlotOptions;

public class SchedulePlotFlotOptions extends PlotFlotOptions {
	
	private final ScheduleDataFlot scheduleData;
	
	/***** Constructor ****/
	
	public SchedulePlotFlotOptions(SchedulePlotFlot plot) {
		super(plot);
		scheduleData = new ScheduleDataFlot(plot.maxValues, plot.bufferWindow);
	}

	/*** Inherited methods ***/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		Map<String,FlotDataSet> backup = new LinkedHashMap<String, FlotDataSet>(dataSets);
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
	
	public ScheduleDataFlot getScheduleData() {
		return scheduleData;
	}
	
}
