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
