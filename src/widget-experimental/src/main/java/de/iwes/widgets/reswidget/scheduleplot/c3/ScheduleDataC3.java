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

import java.util.List;

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
		List<SampledValue> vals = schedule.getValues(startTime, endTime);
		for (SampledValue sv: vals) {
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
