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

package de.iwes.widgets.reswidget.scheduleplot.nvd3;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iwes.widgets.html.plotflot.FlotDataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.ResourceData;

public class ResourceDataNvd3 extends ResourceData<FlotDataSet> {

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
			JSONObject point = new JSONObject();
			point.put("x",entry.getKey()); point.put("y", entry.getValue());
			array.put(point);			
		}
		return array;
	}
	
	

}
