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
package de.iwes.widgets.html.plotmorris;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.html.plot.api.Plot2DDataSet;

public class MorrisDataSet implements Plot2DDataSet {
	
	private final String id;  // yLabel
	private JSONArray data; // Collection<Map<String, Object>> values) 

	public MorrisDataSet(String id) {
		this(id, new JSONArray());
	}
	
	public MorrisDataSet(String id, JSONArray data) {
		this.data = data;
		this.id = id;
	}
	
	public void setData(JSONArray data) {
		this.data = data; 
	}
	
	public void addDataPoint(long time, float yValue) {
		JSONObject map = new JSONObject();
		map.put("t", time);
		map.put(id, yValue);
		data.put(map);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, int maxNrPoints)	throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, float ymin, float ymax) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	JSONArray getValues() {
		return data;
	}

	
}
