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

package de.iwes.widgets.reswidget.scheduleplot.flot;

import java.util.Collections;
import java.util.Iterator;

import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.ogema.core.channelmanager.measurements.IllegalConversionException;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIterator;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIteratorBuilder;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;

import de.iwes.widgets.html.plotflot.FlotDataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.MaxValBuffer;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;

public class ScheduleDataFlot extends ScheduleData<FlotDataSet> {
	
	ScheduleDataFlot() {
		this(null, null);
	}
	
	ScheduleDataFlot(Cache<String, MaxValBuffer> maxValues, Long bufferWindow) {
		super(maxValues, bufferWindow);
	}

	@Override
	protected FlotDataSet getData(String id,ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints, 
			float scale, float offset, long downsamplingIntv) {
		return getData(id, schedule, startTime, endTime, maxNrPoints, scale, offset, Float.NaN, Float.NaN, downsamplingIntv);
	}

	@Override
	protected FlotDataSet getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints,
			float scale, float offset, float ymin, float ymax, long downsamplingIntv) {
		FlotDataSet flotdata = new FlotDataSet(id);
		Float yminFilter = ((Float.isNaN(ymin) || Float.isInfinite(ymin)) ? null : ymin);
		Float ymaxFilter = ((Float.isNaN(ymax) || Float.isInfinite(ymax)) ? null : ymax);
		JSONArray array = getJSONData(schedule, startTime, endTime, maxNrPoints, offset, scale, yminFilter, ymaxFilter, downsamplingIntv);	
		flotdata.setData(array);
		return flotdata;
	}
	
	private static JSONArray getJSONData(ReadOnlyTimeSeries schedule, long startTime, long endTime, 
				int maxNrPoints, float offset, float scale, Float yminFilter, Float ymaxFilter,
				long downsamplingInterval) { // TODO implement reduction to maxNrPoints; preferably in ScheduleData, 
																						// so other implementations can use it as well
		JSONArray array = new JSONArray();
		if (schedule.isEmpty(startTime, endTime))
			return array;
//		List<SampledValue> vals = schedule.getValues(startTime, endTime);
		final Iterator<SampledValue> it; 
		if (downsamplingInterval <= 0)
			it = schedule.iterator(startTime, endTime);
		else  {
			final MultiTimeSeriesIterator multiIt = MultiTimeSeriesIteratorBuilder.newBuilder(Collections.singletonList(schedule.iterator(startTime,endTime)))
					.setStepSize(startTime, downsamplingInterval)
					.setGlobalInterpolationMode(getMode(schedule))
					.build();
			it = new WrappedIterator(multiIt);
		}
		SampledValue last = null;
		long localTZOffset = 0;
		Value container;
		float value;
		JSONArray point;
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
				JSONArray pointAux = new JSONArray();
				pointAux.put(t-1);pointAux.put(value);
				JSONArray pointBad = new JSONArray();
				pointBad.put(t);pointBad.put((String) null);
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
			point = new JSONArray();
			try {
				point.put(t); point.put(value);
			} catch (JSONException e) {
				LoggerFactory.getLogger(ScheduleDataFlot.class).warn("Value not allowed in JSON; skipping this {}; {}", value, e.toString());
				continue;
			}
			array.put(point);
		}
		return array;
	}
	
	
	private static final InterpolationMode getMode(final ReadOnlyTimeSeries schedule) {
		final InterpolationMode mode = schedule.getInterpolationMode();
		return mode != null && mode != InterpolationMode.NONE ? mode : InterpolationMode.LINEAR;
	}

	private final static class WrappedIterator implements Iterator<SampledValue> {
		
		private final MultiTimeSeriesIterator base;
		
		WrappedIterator(MultiTimeSeriesIterator base) {
			this.base = base;
		}

		@Override
		public boolean hasNext() {
			return base.hasNext();
		}

		@Override
		public SampledValue next() {
			return base.next().getElement(0);
		}
		
	}
	
}
