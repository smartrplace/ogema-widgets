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
package de.iwes.widgets.reswidget.scheduleplot.morris2;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ogema.core.channelmanager.measurements.IllegalConversionException;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.html.plotmorris.MorrisDataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;

public class ScheduleDataMorris extends ScheduleData<MorrisDataSet> {

	@Override
	protected MorrisDataSet getData(String id,ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints, 
			float scale, float offset, long downsamplingIntv) {
		MorrisDataSet morrisdata = new MorrisDataSet(id);
		JSONArray array = getJSONData(id, schedule, startTime, endTime, maxNrPoints, scale, offset);	
		morrisdata.setData(array);
		return morrisdata;
	}
	
	@Override
	protected MorrisDataSet getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime,
			int maxNrPoints, float scale, float offset, float ymin, float ymax, long downsamplingIntv) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Value filtering not implemented yet");
	}

	private static JSONArray getJSONData(String id,ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints, float scale, float offset) { // TODO implement reduction to maxNrPoints; preferably in ScheduleData, 
																						// so other implementations can use it as well
		JSONArray array = new JSONArray();
		List<SampledValue> vals = schedule.getValues(startTime, endTime);
		for (SampledValue sv: vals) {
			if (sv.getQuality()== Quality.BAD) 
				continue;
			long t = sv.getTimestamp();
			Value container = sv.getValue();
			float value;
			try {
				value = container.getFloatValue()* scale + offset;
				JSONObject point = new JSONObject();
				point.put("t",t); point.put(id,value);
				array.put(point);
			} catch (IllegalConversionException | JSONException e) {  // all relevant Values can be converted to float
				continue;
			}
		}
		return array;
		
	}
	
	

}
