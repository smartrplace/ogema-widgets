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
