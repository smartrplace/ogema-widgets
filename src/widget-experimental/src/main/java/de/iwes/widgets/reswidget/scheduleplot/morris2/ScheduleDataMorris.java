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
