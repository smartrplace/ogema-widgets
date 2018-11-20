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

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

public abstract class InputBooleanValueCleaner extends InputSeriesAggregator {
	protected final long minOnTime;
	protected abstract void processValue(ValueDuration val, int idxOfEvaluationInput, SampledValue sv, SampledValueDataPoint dataPoint);
	
    public InputBooleanValueCleaner(int nrInput, int idxSumOfPrevious, long endTime, long minOnTime) {
    	this(nrInput, idxSumOfPrevious, endTime, null, AggregationMode.AVERAGING, minOnTime);
    }
    public InputBooleanValueCleaner(int nrInput, int idxSumOfPrevious, long endTime, InterpolationMode interpolationMode,
    		AggregationMode aggregationMode, long minOnTime) {
    	super(nrInput, idxSumOfPrevious, endTime, interpolationMode, AggregationMode.AVERAGING);
       	this.minOnTime = minOnTime;
	}
    public InputBooleanValueCleaner(int[] nrInput, int[] idxSumOfPrevious, int inputIdx, long endTime, long minOnTime) {
    	this(nrInput, idxSumOfPrevious, inputIdx, endTime, null, AggregationMode.AVERAGING, minOnTime);
     }
    public InputBooleanValueCleaner(int[] nrInput, int[] idxSumOfPrevious, int inputIdx, long endTime,
    		InterpolationMode interpolationMode, AggregationMode aggregationMode, long minOnTime) {
		this(nrInput[inputIdx], idxSumOfPrevious[inputIdx], endTime, interpolationMode, aggregationMode, minOnTime);
	}
	
	boolean isPresent = false;
	long presenceConfirmedTime = 0;
	class InputCallData {
		public InputCallData(ValueDuration val, int idxOfEvaluationInput, SampledValue sv,
				SampledValueDataPoint dataPoint) {
			this.val = val;
			this.idxOfEvaluationInput = idxOfEvaluationInput;
			this.sv = sv;
			this.dataPoint = dataPoint;
		}
		ValueDuration val;
		int idxOfEvaluationInput;
		SampledValue sv;
		SampledValueDataPoint dataPoint;
	}
	InputCallData firstHiddenOff = null;

	@Override
    public ValueDuration getCurrentValueDuration(int idxOfEvaluationInput, SampledValue sv,
    		SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
		throw new IllegalStateException("Use only newValue and implement processValue here!");
	}
    public void newValue(int idxOfEvaluationInput, SampledValue sv,
	    		SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
    	ValueDuration currentVal = super.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, ignoreMissingPoints);

		boolean bval = currentVal.value > 0.5;
		if(isPresent && (firstHiddenOff != null) && ((presenceConfirmedTime + minOnTime) <= sv.getTimestamp())) {
			processValue(firstHiddenOff.val, firstHiddenOff.idxOfEvaluationInput, firstHiddenOff.sv,
					firstHiddenOff.dataPoint);
			firstHiddenOff = null;
			isPresent = false;
		}
		if(bval) {
			presenceConfirmedTime = sv.getTimestamp();
		}
		if(bval == isPresent) return;
		if(isPresent &&
				((presenceConfirmedTime + minOnTime) > sv.getTimestamp())) {
			if(firstHiddenOff == null) firstHiddenOff = new InputCallData(currentVal, idxOfEvaluationInput, sv, dataPoint);
			return;
		}
		isPresent = bval;
		processValue(currentVal, idxOfEvaluationInput, sv, dataPoint);
    
    }
}
