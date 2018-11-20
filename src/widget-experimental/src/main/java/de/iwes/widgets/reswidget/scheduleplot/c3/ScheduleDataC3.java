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

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.ogema.core.channelmanager.measurements.IllegalConversionException;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.html.plotc3.C3DataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;

public class ScheduleDataC3 extends ScheduleData<C3DataSet> {

	@Override
	protected C3DataSet getData(String id,ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints,
			float scale, float offset, long downsamplingIntv) {
		JSONArray[] array = getJSONData(id, schedule, startTime, endTime, maxNrPoints, scale, offset);
		C3DataSet c3data = new C3DataSet(array[0], array[1]);
		return c3data;
	}

	@Override
	protected C3DataSet getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints,
			float scale, float offset, float ymin, float ymax, long downsamplingIntv) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Value filters not implemented yet");
	}

	private static JSONArray[] getJSONData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints, float scale, float offset) { // TODO implement reduction to maxNrPoints; preferably in ScheduleData,
																						// so other implementations can use it as well
		JSONArray array_x = new JSONArray();
		JSONArray array_y = new JSONArray();
		array_x.put(id.substring(0, id.length()-2) + "_t");
		array_y.put(id);
		final Iterator<SampledValue> vals = schedule.iterator(startTime, endTime);
		while (vals.hasNext()) {
			final SampledValue sv = vals.next();
			if (sv.getQuality()== Quality.BAD)
				continue;
			long t = sv.getTimestamp();
			Value container = sv.getValue();
			float value;
			try {
				value = container.getFloatValue()* scale + offset;
				array_x.put(t);
				array_y.put(value);
			} catch (IllegalConversionException | JSONException e) {  // all relevant Values can be converted to float
				continue;
			}
		}
		return new JSONArray[]{array_x,array_y};

	}

}
