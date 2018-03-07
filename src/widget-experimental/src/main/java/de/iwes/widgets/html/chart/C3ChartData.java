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

package de.iwes.widgets.html.chart;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.chart.C3Chart.ChartType;

public class C3ChartData extends WidgetData {

	private ChartType type = null;
	private int changeCounter = 0;
	
	/************* constructor **********************/
	
	public C3ChartData(C3Chart chart, ChartType type) {
		super(chart);
		this.type = type;
	}
	
	/******* Inherited methods ******/


	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject result = new JSONObject();
		result.put("cc", changeCounter);

		if (type != null) {
			result.put("type", type);
		}
		
		if (getChartConfig() != null) {
			result.put("conf", getChartConfig());
		}

		JSONArray rows = new JSONArray();
		result.put("rows", rows);

		for (C3DataRow row : dataRows) {
			rows.put(row.toJSON());
		}

		return result;
	}
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) {
		return new JSONObject();
	}

	/******** public methods ***********/
	
	public JSONArray getChartConfig() {
		return null;
	}

	public HashSet<C3DataRow> dataRows = new HashSet<C3DataRow>();
	
	public HashSet<C3DataRow> getDataRows() {
		return dataRows;
	}	
	public void changed() {
		changeCounter++;
	}

	public void add(C3DataRow row) {
		dataRows.add(row);
	}

	public void remove(C3DataRow row) {
		// TODO unload data!
	}
}
