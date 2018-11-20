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
package de.iwes.widgets.html.plotlyjs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import de.iwes.widgets.html.plot.api.Plot2DDataSet;

public class PlotlyjsDataSet implements Plot2DDataSet {
	
	private static final Map<String, Object> EMPTY = Stream.<String> builder()
			.add("x").add("y").build()
			.collect(Collectors.toMap(Function.identity(), e -> new JSONArray()));
	
	private final String id;
	// two arrays, x-axis, y-axis
	/* content (see https://plot.ly/javascript/plotlyjs-function-reference/):
	  x: [1999, 2000, 2001, 2002],
	  y: [10, 15, 13, 17],
	*/
	private Map<String, Object> data;
	
	

	public PlotlyjsDataSet(String id) {
		this(id, new HashMap<>(EMPTY));
	}
	
	public PlotlyjsDataSet(String id, Map<String, Object> data) {
		this.id = id;
		setData(data);
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data; 
		if (!data.containsKey("name"))
			data.put("name", id);
	}
	
	/*
	public void addData(JSONArray point) {
		data.put(point);
	}
	*/

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax) throws UnsupportedOperationException {
		// XXX presumably not very efficient; keep data in NavigableMAp instead?
		final JSONArray newTime = new JSONArray();
		final JSONArray newValue = new JSONArray();
		final Map<String, Object> json = new HashMap<>(8);
		json.put("x", newTime);
		json.put("y", newValue);
		json.put("name", id);
//		final Map<String, Object> json = Stream.<String> builder()
//				.add("x").add("y").build()
//				.collect(Collectors.toMap(Function.identity(), e -> "x".equals(e) ? newTime : newValue));
		Iterator<Object> itTime = ((JSONArray) data.get("x")).iterator();
		Iterator<Object> itValue = ((JSONArray) data.get("y")).iterator();
		while (itTime.hasNext()) {
			final long x = (long) itTime.next();
			final float y = (float) itValue.next();
			// TODO more efficient way to find first relevant timestamp
			if (x >= xmin && x <= xmax) {
				newTime.put(x);
				newValue.put(y);
			}
			else if (x > xmax)
				break;
		}
		return new PlotlyjsDataSet(id, json);
	}
	
	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, int maxNrPoints)	throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Plot2DDataSet getValues(float xmin, float xmax, float ymin, float ymax) {
		// XXX presumably not very efficient; keep data in NavigableMAp instead?
		final JSONArray newTime = new JSONArray();
		final JSONArray newValue = new JSONArray();
		final Map<String, Object> json = new HashMap<>(8);
		json.put("x", newTime);
		json.put("y", newValue);
		json.put("name", id);
//		final Map<String, JSONArray> json = Stream.<String> builder()
//				.add("x").add("y").build()
//				.collect(Collectors.toMap(Function.identity(), e -> "x".equals(e) ? newTime : newValue));
		Iterator<Object> itTime = ((JSONArray) data.get("x")).iterator();
		Iterator<Object> itValue =((JSONArray) data.get("y")).iterator();
		while (itTime.hasNext()) {
			final long x = (long) itTime.next();
			final float y = (float) itValue.next();
			// TODO more efficient way to find first relevant timestamp
			if (x >= xmin && x <= xmax && y >= ymin && y<= ymax) {
				newTime.put(x);
				newValue.put(y);
			}
			else if (x > xmax)
				break;
		}
		return new PlotlyjsDataSet(id, json);
	}
	
	Map<String, Object> getValues() {
		return data;
	}

	
}
