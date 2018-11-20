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
package de.iwes.widgets.reswidget.scheduleplot.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;

import de.iwes.widgets.html.plot.api.Plot2DDataSet;

public abstract class ResourceData<D extends Plot2DDataSet> {

	private static final int MAX_NR_DATA_POINTS = 10000;
	protected final Map<String,Resource> resources = new ConcurrentHashMap<String, Resource>();
	protected final Map<String,Map<Long,Float>> values = new ConcurrentHashMap<String, Map<Long,Float>>();
	private volatile int counter = 0; 
	
	/**** Methods to be implemented in derived class ****/
	
	/**
	 * convert time series data (in variable {@link #values}) to the respective widget's data format
	 */
	protected abstract D getData(String id, Map<Long,Float> valueMap);
	
	/**** Public methods  ****/
	
	public final void setResources(Collection<Resource> resources) {
		this.resources.clear();
		for (Resource res: resources) {
			addResource(res);
		}
		
	}

	public final void addResource(Resource resource) {
		resources.put(resource.getPath(), resource);
	}

	public final boolean removeResource(Resource resource) {
		return resources.remove(resource.getPath()) != null;
	}
	
	/****** Internal methods ****/
	
	/**
	 * for use in respective Widget's options' retrieveGET method;
	 * each call leads to an update of the data
	 */
	public final Map<String,D> getAllDataSets() {
		counter++;
		if (counter > MAX_NR_DATA_POINTS)
			values.clear();
		Map<String,D> map = new LinkedHashMap<String, D>();
		synchronized (resources) { // required?
			Iterator<Map.Entry<String, Resource>> it = resources.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Resource> entry = it.next();
				String id = Utils.getValidJSName(entry.getKey());
				if (!values.containsKey(id)) 
					values.put(id, new LinkedHashMap<Long, Float>()); 
				Resource res = entry.getValue();
				Float val = getValue(res);
				if (val == null) continue;
				long t = System.currentTimeMillis(); // FIXME use OGEMA time instead
				values.get(id).put(t,val);
				map.put(id, getData(id, values.get(id)));
			}
		}
		return map;
	}
	
	private Float getValue(Resource res) {
		if (!res.exists() || !(res instanceof SingleValueResource) || res instanceof StringResource)
			return null;
		if (res instanceof FloatResource)
			return ((FloatResource) res).getValue();
		else if (res instanceof BooleanResource) 
			return (((BooleanResource) res).getValue() ? (float) 1 : (float) 0);
		else if (res instanceof IntegerResource) 
			return (float) ((IntegerResource) res).getValue();
		else if (res instanceof TimeResource) 
			return (float) ((TimeResource) res).getValue();
		return null;
	}
	
	
}
