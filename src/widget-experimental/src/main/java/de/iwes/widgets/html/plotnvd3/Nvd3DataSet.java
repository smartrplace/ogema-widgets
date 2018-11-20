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
package de.iwes.widgets.html.plotnvd3;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.html.plot.api.Plot2DDataSet;

public class Nvd3DataSet implements Plot2DDataSet {
	
	private final String id;
	private JSONArray data; // must be an array of points, where each point itself is an object of type { x : 3.4, y: 4.2 }

	public Nvd3DataSet(String id) {
		this(id, new JSONArray());
	}
	
	public Nvd3DataSet(String id, JSONArray data) {
		this.data = data;
		this.id = id;
	}
	
	public void setData(JSONArray data) {
		this.data = data; 
	}
	
	public void addData(JSONArray point) {
		data.put(point);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax) throws UnsupportedOperationException {
		// XXX presumably not very efficient; keep data in NavigableMAp instead?
		JSONArray arr = new JSONArray();
		Iterator<Object> it = data.iterator();
		while (it.hasNext()) {
			JSONObject point = (JSONObject) it.next();
			long x = point.getLong("x");
			// TODO more efficient way to find first relevant timestamp
			if (x >= xmin && x <= xmax) 
				arr.put(point);
			else if (x > xmax)
				break;
		}
		return new Nvd3DataSet(id, arr);
	}
	
	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, int maxNrPoints)	throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, float ymin, float ymax) {
		// XXX presumably not very efficient; keep data in NavigableMAp instead?
				JSONArray arr = new JSONArray();
				Iterator<Object> it = data.iterator();
				while (it.hasNext()) {
					JSONObject point = (JSONObject) it.next();
					long x = point.getLong("x");
					double y = point.getDouble("y");
					// TODO more efficient way to find first relevant timestamp
					if (x >= xmin && x <= xmax && y >= ymin && y<= ymax) 
						arr.put(point);
					else if (x > xmax)
						break;
				}
				return new Nvd3DataSet(id, arr);
	}
	
	JSONArray getValues() {
		return data;
	}

	
}
