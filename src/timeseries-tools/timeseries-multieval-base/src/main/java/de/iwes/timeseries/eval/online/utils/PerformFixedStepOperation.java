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

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;

import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;

/**Online estimator to build fixed-time step time series without alignment*/
public abstract class PerformFixedStepOperation {
	//overwrite if necessary
	protected abstract void performOperation(float value, long timeStamp);
	
	private final long fixedTimeStep;
	private final AggregationMode aggregationMode;
	//next step after last step written
	private long currentTimeStep;
	//private long nextTimeStep;
	private Float currentValue = null;
	private int count = 0;
	
	public PerformFixedStepOperation(long fixedTimeStep, long startTime) {
		this(fixedTimeStep, startTime, AggregationMode.AVERAGING);
	}
	/** Construct Base Estimator object that can be fed with values via {@link #addValue(float)}
	 * 
	 * @param fixedTimeStep time step with which {@link #performOperation(float, long)} shall be called.
	 * @param startTime start time of first interval. performOperation will be called from this time on
	 * @param aggregationMode default is AVERAGING
	 */
	public PerformFixedStepOperation(long fixedTimeStep, long startTime, AggregationMode aggregationMode) {
		this.fixedTimeStep = fixedTimeStep;
		this.aggregationMode = aggregationMode;
		currentTimeStep = startTime;
		//nextTimeStep = startTime + fixedTimeStep;
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
		float val = value.getValue().getFloatValue();
		if(currentValue == null) {
			while((ts-fixedTimeStep) >= currentTimeStep) {
				currentTimeStep += fixedTimeStep;
			}
			currentValue = val;
			count = 1;
		}
		currentValue = InputSeriesAggregator.processSingleInputValue(currentValue, val, aggregationMode);
		count++;
		ts += duration;
		if(ts > currentTimeStep) {
			//update
			float valToUse = InputSeriesAggregator.getFinalValue(currentValue, count, aggregationMode);
			while(ts > currentTimeStep) {
				performOperation(valToUse, currentTimeStep);
				currentTimeStep += fixedTimeStep;
			}
			currentValue = InputSeriesAggregator.initValue(aggregationMode);
			count = 0;
		}
		float newVal = value.getValue().getFloatValue();
		if(currentValue == null) currentValue = newVal;
		else currentValue = InputSeriesAggregator.processSingleInputValue(currentValue, newVal, aggregationMode);
	}
	
}
