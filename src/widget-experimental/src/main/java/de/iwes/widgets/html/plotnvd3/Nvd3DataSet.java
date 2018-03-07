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
