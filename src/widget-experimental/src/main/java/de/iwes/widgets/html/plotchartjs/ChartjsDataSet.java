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

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.html.plot.api.Plot2DDataSet;

public class ChartjsDataSet implements Plot2DDataSet {

	private final String id;
	// http://www.chartjs.org/docs/latest/charts/mixed.html
	private JSONObject data; // must be an array of points, where each point itself is an array of two numerical entries

	public ChartjsDataSet(String id) {
		this(id, new JSONObject());
	}

	public ChartjsDataSet(String id, JSONObject data) {
		this.data = data;
		this.id = id;
		data.put("label", id);
		if (!data.has("data"))
			data.put("data", new JSONArray());
	}

	public void setData(JSONArray data) {
		this.data.put("data", data);
	}

	/*
	 * http://www.chartjs.org/docs/latest/charts/line.html
	 * Point format:
	 * {
     *   x: 10,
     *   y: 20
     * }
	 */
	public void addData(JSONObject point) {
		data.getJSONArray("data").put(point);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax) throws UnsupportedOperationException {
		// XXX presumably not very efficient; keep data in NavigableMAp instead?
		final JSONArray arr = new JSONArray();
		final Iterator<Object> it = data.getJSONArray("data").iterator();
		while (it.hasNext()) {
			final JSONObject point = (JSONObject) it.next();
			final long x = point.getLong("x");
			// TODO more efficient way to find first relevant timestamp
			if (x >= xmin && x <= xmax)
				arr.put(point);
			else if (x > xmax)
				break;
		}
		final JSONObject json = new JSONObject();
		json.put("label", id);
		json.put("data", arr);
		return new ChartjsDataSet(id, json);
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, int maxNrPoints)	throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, float ymin, float ymax) {
		final JSONArray arr = new JSONArray();
		final Iterator<Object> it = data.getJSONArray("data").iterator();
		while (it.hasNext()) {
			final JSONObject point = (JSONObject) it.next();
			final long x = point.getLong("x");
			double y = point.getDouble("y");
			// TODO more efficient way to find first relevant timestamp
			if (x >= xmin && x <= xmax && y >= ymin && y<= ymax)
				arr.put(point);
			else if (x > xmax)
				break;
		}
		final JSONObject json = new JSONObject();
		json.put("label", id);
		json.put("data", arr);
		return new ChartjsDataSet(id, json);
	}

	JSONObject getValues() {
		return data;
	}


}
