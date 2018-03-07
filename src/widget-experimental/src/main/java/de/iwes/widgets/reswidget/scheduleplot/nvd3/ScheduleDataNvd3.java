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

import java.util.Iterator;

import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ogema.core.channelmanager.measurements.IllegalConversionException;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;

import de.iwes.widgets.html.plotnvd3.Nvd3DataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.MaxValBuffer;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;

public class ScheduleDataNvd3 extends ScheduleData<Nvd3DataSet> {
	
	ScheduleDataNvd3() {
		this(null, null);
	}
	
	ScheduleDataNvd3(Cache<String, MaxValBuffer> maxValues, Long bufferWindow) {
		super(maxValues, bufferWindow);
	}

	@Override
	protected Nvd3DataSet getData(String id,ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints, 
			float scale, float offset, long downsamplingIntv) {
		return getData(id, schedule, startTime, endTime, maxNrPoints, scale, offset, Float.NaN, Float.NaN, downsamplingIntv);
	}

	@Override
	protected Nvd3DataSet getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints,
			float scale, float offset, float ymin, float ymax, long downsamplingIntv) {
		Nvd3DataSet flotdata = new Nvd3DataSet(id);
		Float yminFilter = ((Float.isNaN(ymin) || Float.isInfinite(ymin)) ? null : ymin);
		Float ymaxFilter = ((Float.isNaN(ymax) || Float.isInfinite(ymax)) ? null : ymax);
		JSONArray array = getJSONData(schedule, startTime, endTime, maxNrPoints, offset, scale, yminFilter, ymaxFilter);	
		flotdata.setData(array);
		return flotdata;
	}
	
	private static JSONArray getJSONData(ReadOnlyTimeSeries schedule, long startTime, long endTime, 
				int maxNrPoints, float offset, float scale, Float yminFilter, Float ymaxFilter) { // TODO implement reduction to maxNrPoints; preferably in ScheduleData, 
																						// so other implementations can use it as well
		JSONArray array = new JSONArray();
		if (schedule.isEmpty(startTime, endTime))
			return array;
//		List<SampledValue> vals = schedule.getValues(startTime, endTime);
		Iterator<SampledValue> it = schedule.iterator(startTime, endTime);
		SampledValue last = null;
		long localTZOffset = 0;
		Value container;
		float value;
		JSONObject point;
		boolean first = true;
//		for (SampledValue sv: vals) {
		SampledValue sv;
		while (it.hasNext()) {
			sv = it.next();
			if (first) {
				localTZOffset = DateTimeZone.getDefault().getOffset(sv.getTimestamp());
				first = false;
			}
			long t = (sv.getTimestamp()) + localTZOffset;
			if (sv.getQuality()== Quality.BAD) {
				if (last == null || t - last.getTimestamp() < 1)
					continue;
				try {
					value = last.getValue().getFloatValue();
				} catch (Exception e) {  // all relevant Values can be converted to float
					continue;
				}
				JSONObject pointAux = new JSONObject();
				pointAux.put("x", t-1);
				pointAux.put("y", value);
				JSONObject pointBad = new JSONObject();
				pointAux.put("x", t);
				pointAux.put("y", Float.NaN);
				array.put(pointAux); array.put(pointBad);
				continue;
			}
			last = sv;
			container = sv.getValue();
			try {
				value = container.getFloatValue();
				if (yminFilter != null && value < yminFilter)
					continue;
				if (ymaxFilter != null && value > ymaxFilter)
					continue;
				value = value * scale + offset;
			} catch (IllegalConversionException e) {  // all relevant Values can be converted to float
				continue;
			}
			point = new JSONObject();
			try {
				point.put("x", t);
				point.put("y", value);
			} catch (JSONException e) {
				LoggerFactory.getLogger(ScheduleDataNvd3.class).warn("Value not allowed in JSON; skipping this {}; {}", value, e.toString());
				continue;
			}
			array.put(point);
		}
		return array;
	}

}
