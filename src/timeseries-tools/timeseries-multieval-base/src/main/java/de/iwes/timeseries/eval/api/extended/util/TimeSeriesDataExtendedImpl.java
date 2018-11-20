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
package de.iwes.timeseries.eval.api.extended.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;

public class TimeSeriesDataExtendedImpl extends TimeSeriesDataImpl {
	//Ids of the different SelectionItem-levels
	private List<String> ids = null;
	private final Map<String, String> properties = new HashMap<String, String>();
	public Object type = null;
	
	public TimeSeriesDataExtendedImpl(ReadOnlyTimeSeries timeSeries, String label, String description, InterpolationMode mode) {
		this(timeSeries, label, description, mode, 0, 0, 0);
	}
	
	public TimeSeriesDataExtendedImpl(ReadOnlyTimeSeries timeSeries, String label, String description, InterpolationMode mode,
			float offset, float factor, long timeOffset) {
		super(timeSeries, label, description, mode, offset, factor, timeOffset);
	}
	public TimeSeriesDataExtendedImpl(TimeSeriesDataImpl tsIn, List<String> ids2, Object inputDef) {
		super(tsIn.getTimeSeries(), tsIn.label(null), tsIn.description(null), tsIn.interpolationMode(),
				tsIn.offset(), tsIn.factor(), tsIn.timeOffset());
		this.ids = ids2;
		this.type = inputDef;
	}

	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
	
	public Set<String> getProporties() {
		return properties.keySet();
	}
	
	public List<String> getIds() {
		return ids;
	}
	
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
}
