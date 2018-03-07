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

package de.iwes.widgets.reswidget.scheduleplot.morris2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotmorris.MorrisDataSet;
import de.iwes.widgets.html.plotmorris.PlotMorrisOptions;

public class SchedulePlotMorrisOptions extends PlotMorrisOptions {
	
	private final ScheduleDataMorris scheduleData = new ScheduleDataMorris();

	/***** Constructor ****/
	
	public SchedulePlotMorrisOptions(SchedulePlotMorris plot) {
		super(plot);
	}

	/*** Inherited methods ***/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		Map<String,MorrisDataSet> backup = new LinkedHashMap<String, MorrisDataSet>(dataSets);
		dataSets.putAll(scheduleData.getAllDataSets(Long.MIN_VALUE,Long.MAX_VALUE,Integer.MAX_VALUE,req)); // TODO implement reduction? Will be done automatically by super function, but may be inefficient
		configuration.setPlotType(scheduleData.getCurrentType());
		JSONObject result = super.retrieveGETData(req);
		dataSets.clear();
		dataSets.putAll(backup);
		return result;
	}
	
	
	/**** Public methods ***/
	
	public ScheduleDataMorris getScheduleData() {
		return scheduleData;
	}
	
	
	
}
