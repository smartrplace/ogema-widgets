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
