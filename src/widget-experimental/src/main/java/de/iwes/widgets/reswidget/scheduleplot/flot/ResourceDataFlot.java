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
package de.iwes.widgets.reswidget.scheduleplot.flot;

import java.util.Map;

import org.json.JSONArray;

import de.iwes.widgets.html.plotflot.FlotDataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.ResourceData;

public class ResourceDataFlot extends ResourceData<FlotDataSet> {

	@Override
	protected FlotDataSet getData(String id,Map<Long,Float> valueMap) {
		FlotDataSet flotdata = new FlotDataSet(id);
		JSONArray array = getJSONData(valueMap);	
		flotdata.setData(array);
		return flotdata;
	}

	private static JSONArray getJSONData(Map<Long,Float> valueMap) {
		JSONArray array = new JSONArray();
		for (Map.Entry<Long, Float> entry : valueMap.entrySet()) {
			JSONArray point = new JSONArray();
			point.put(entry.getKey()); point.put(entry.getValue());
			array.put(point);			
		}
		return array;
	}
	
	

}
