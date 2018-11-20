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
package de.iwes.timeseries.eval.online.utils;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;

/**Online estimator to build fixed-time step time series without alignment*/
public class TimeSeriesFixedStepOnlineBuilder {
	//overwrite if necessary
	protected float provideReplacementValue(long timeStepStart) {
		return 0;
	}
	
	private final List<SampledValue> collected = new ArrayList<>();
	private final long resultTimeStep;
	private final AggregationMode aggregationMode;
	//next step after last step written
	private long currentTimeStep;
	private long nextTimeStep;
	private Float currentValue = null;
	
	public TimeSeriesFixedStepOnlineBuilder(long resultTimeStep, long startTime) {
		this(resultTimeStep, startTime, AggregationMode.AVERAGING);
	}
	/** Construct Base Estimator object that can be fed with values via {@link #addValue(float)}
	 * 
	 * @param resultTimeStep time step of resulting fixed-step time series
	 * @param startTime start time of first interval. Starting intervals without data
	 * 		will be filled with NaN values.
	 * @param aggregationMode default is AVERAGING
	 */
	public TimeSeriesFixedStepOnlineBuilder(long resultTimeStep, long startTime, AggregationMode aggregationMode) {
		this.resultTimeStep = resultTimeStep;
		this.aggregationMode = aggregationMode;
		currentTimeStep = startTime;
		nextTimeStep = startTime + resultTimeStep;
	}

	public ReadOnlyTimeSeries getTimeSeries() {
		FloatTreeTimeSeries result = new FloatTreeTimeSeries();
		//for an empty FloatTreeTimeSeries addValues should be pretty efficient
		result.addValues(collected);
		return result;
	}
	
	/**Report new value.
	 * @param value
	 * @param duration If the evaluation shall weight all values equally (duration of the value
	 * not considered, just hand over 1 every time, otherwise the duration of the value
	 * @param timeStep of the value
	 */
	public void addValue(float value, long duration, long timeStep) {
		addValue(new SampledValue(new FloatValue(value), timeStep , Quality.GOOD), duration);
	}
	public void addValue(SampledValue value, long duration) {
		long ts = value.getTimestamp();
		if(ts >= nextTimeStep) {
			//update
			if(currentValue != null) {
				collected.add(new SampledValue(new FloatValue(currentValue), currentTimeStep, Quality.GOOD));
				currentTimeStep += resultTimeStep;
				nextTimeStep += resultTimeStep;				
			}
			while(ts >= nextTimeStep) {
				float replace = provideReplacementValue(currentTimeStep);
				collected.add(new SampledValue(new FloatValue(replace), currentTimeStep, Quality.GOOD));
				currentTimeStep += resultTimeStep;
				nextTimeStep += resultTimeStep;				
			}
			currentValue = null;
		}
		float newVal = value.getValue().getFloatValue();
		if(currentValue == null) currentValue = newVal;
		else currentValue = InputSeriesAggregator.processSingleInputValue(currentValue, newVal, aggregationMode);
	}
	
}
