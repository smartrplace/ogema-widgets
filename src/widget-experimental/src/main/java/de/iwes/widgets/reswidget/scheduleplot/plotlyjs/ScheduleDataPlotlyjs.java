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
package de.iwes.widgets.reswidget.scheduleplot.plotlyjs;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ogema.core.channelmanager.measurements.IllegalConversionException;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIterator;
import org.ogema.tools.timeseries.iterator.api.MultiTimeSeriesIteratorBuilder;
import com.google.common.cache.Cache;

import de.iwes.widgets.html.plotlyjs.PlotlyjsDataSet;
import de.iwes.widgets.reswidget.scheduleplot.api.MaxValBuffer;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;

public class ScheduleDataPlotlyjs extends ScheduleData<PlotlyjsDataSet> {
	
	ScheduleDataPlotlyjs() {
		this(null, null);
	}
	
	ScheduleDataPlotlyjs(Supplier<Cache<String, MaxValBuffer>> maxValues, Long bufferWindow) {
		super(maxValues, bufferWindow);
	}

	@Override
	protected PlotlyjsDataSet getData(String id,ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints, 
			float scale, float offset, long downsamplingIntv) {
		return getData(id, schedule, startTime, endTime, maxNrPoints, scale, offset, Float.NaN, Float.NaN, downsamplingIntv);
	}

	@Override
	protected PlotlyjsDataSet getData(String id, ReadOnlyTimeSeries schedule, long startTime, long endTime, int maxNrPoints,
			float scale, float offset, float ymin, float ymax, long downsamplingIntv) {
		PlotlyjsDataSet flotdata = new PlotlyjsDataSet(id);
		Float yminFilter = ((Float.isNaN(ymin) || Float.isInfinite(ymin)) ? null : ymin);
		Float ymaxFilter = ((Float.isNaN(ymax) || Float.isInfinite(ymax)) ? null : ymax);
		Map<String, Object> array = getJSONData(schedule, startTime, endTime, maxNrPoints, offset, scale, yminFilter, ymaxFilter, downsamplingIntv);	
		flotdata.setData(array);
		return flotdata;
	}
	
	private static Map<String,Object> getJSONData(ReadOnlyTimeSeries schedule, long startTime, long endTime, 
				int maxNrPoints, float offset, float scale, Float yminFilter, Float ymaxFilter,
				long downsamplingInterval) { // TODO implement reduction to maxNrPoints; preferably in ScheduleData, 
																						// so other implementations can use it as well
		final JSONArray time = new JSONArray();
		final JSONArray values = new JSONArray();
		if (schedule.isEmpty(startTime, endTime))
			return Stream.<String> builder()
				.add("x").add("y").build()
				.collect(Collectors.toMap(Function.identity(), e -> "x".equals(e) ? time : values));
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
				time.put(t-1);
				values.put(value);
				time.put(t);
				values.put(JSONObject.NULL);
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
			time.put(t);
			values.put(value);
		}
		return Stream.<String> builder()
				.add("x").add("y").build()
				.collect(Collectors.toMap(Function.identity(), e -> "x".equals(e) ? time : values));

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
