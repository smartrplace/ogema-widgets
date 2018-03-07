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
