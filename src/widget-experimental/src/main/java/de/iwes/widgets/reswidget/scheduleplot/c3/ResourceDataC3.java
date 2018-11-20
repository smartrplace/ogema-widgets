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
package de.iwes.widgets.reswidget.scheduleplot.c3;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;

import de.iwes.widgets.html.plotc3.C3DataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.ResourceData;

public class ResourceDataC3 extends ResourceData<C3DataSet> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected C3DataSet getData(String id,Map<Long,Float> valueMap) {
		Map newMap = new LinkedHashMap<Long, Float>();
		newMap.put(id + "_t", id + "_y");
		newMap.putAll(valueMap);
		JSONArray xs = new JSONArray(newMap.keySet());
		JSONArray ys = new JSONArray(newMap.values());
		C3DataSet c3data = new C3DataSet(xs,ys);
		return c3data;
	}

}
