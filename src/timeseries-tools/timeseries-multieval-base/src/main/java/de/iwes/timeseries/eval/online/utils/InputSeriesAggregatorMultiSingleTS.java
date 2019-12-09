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

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;

/**Provides values for a set of single input time series. These do not have to come from
 * a single input type
 */
public class InputSeriesAggregatorMultiSingleTS extends InputSeriesAggregator {
	//private final int[] totalInputIdx;
	/**indeces from respective {@link SpecificEvalBaseImpl}
	 */
	//number of input time series
	//protected final int nrInput;
    
	//not used
	//protected final int idxSumOfPrevious;
	int[] totalInputIdxAll;
	
    public InputSeriesAggregatorMultiSingleTS(int nrInput, long endTime) {
    	this(nrInput, endTime, null, AggregationMode.AVERAGING);
    }
    public InputSeriesAggregatorMultiSingleTS(int nrInput, long endTime, InterpolationMode interpolationMode,
    		AggregationMode aggregationMode) {
		super(nrInput, -1, endTime, interpolationMode, aggregationMode);
		this.totalInputIdxAll = new int[nrInput];
		for(int i=0; i<nrInput; i++) this.totalInputIdxAll[i] = -1;
	}
 

	/** If two input values have the same time stamp the duration for the first one will be zero. So you could
	 * check for a duration zero and not perform further calculations if this has no effect anyways

	 */
   public class ValueDurationMulti {
    	public float[] value;
    	public long duration;
    	public long nextTimeStamp;
    }
   
   @Override
   public ValueDuration getCurrentValueDuration(int idxOfEvaluationInput, SampledValue sv, SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
	   throw new IllegalStateException("Only use getCurrentValueDurationMulti here!");
   }
    /** Use this usually*/
    public ValueDurationMulti getCurrentValueDurationMulti(int idxOfEvaluationInput, SampledValue sv, SampledValueDataPoint dataPoint, boolean ignoreMissingPoints,
    		int totalInputIdx) {
    	ValueDurationMulti result = new ValueDurationMulti();
    	
    	if(nrInput == 1) {
    		result.value = new float[] {sv.getValue().getFloatValue()};
    		//int totIdx = totalInputIdx[0];
			SampledValue svNext = dataPoint.getNextElement(totalInputIdx);
			long now = sv.getTimestamp();
	    	if(svNext == null)
				result.nextTimeStamp = getNextTimeStamp(now, Long.MAX_VALUE);
	    	else
	    		result.nextTimeStamp = getNextTimeStamp(now, svNext.getTimestamp());
	    	result.duration = result.nextTimeStamp - now;
        	return result;
    	}
    	
    	long now = sv.getTimestamp();
    	result.nextTimeStamp = Long.MAX_VALUE;
    	if(sv.getQuality() == Quality.BAD && (!ignoreMissingPoints)) currentValues[idxOfEvaluationInput] = Float.NaN;
    	else currentValues[idxOfEvaluationInput] = sv.getValue().getFloatValue();
    	for(int i=0; i<nrInput; i++) {
			if(i == idxOfEvaluationInput) {
				this.totalInputIdxAll[i] = totalInputIdx;
				SampledValue svNext = dataPoint.getNextElement(totalInputIdx);
				if(svNext != null) {
					long nextTimeLoc = svNext.getTimestamp();
					lastNextTimeStamps[i] = getNextTimeStamp(now, nextTimeLoc);
				} else lastNextTimeStamps[i] = getNextTimeStamp(now, Long.MAX_VALUE);
			} else {
				if((lastNextTimeStamps[i] <= 0) && (totalInputIdxAll[i] >= 0)) {
					//int totIdx = totalInputIdx[i];
					SampledValue svNext = dataPoint.getNextElement(totalInputIdxAll[i]);
					if(svNext != null) {
						long nextTimeLoc = svNext.getTimestamp();
						lastNextTimeStamps[i] = getNextTimeStamp(now, nextTimeLoc);
					}
				} else if((lastNextTimeStamps[i] > 0) && (lastNextTimeStamps[i] < now))
					System.out.println("Timestep for index "+i+" behind:"+lastNextTimeStamps[i]+" now:"+now+" diff:"+(now-lastNextTimeStamps[i]));
			}
			// we do not need this anymore and it would cause Nullpointexception
			/*if (Float.isNaN(currentValues[i])) {
				if (!ignoreMissingPoints) {
					result.value[i] = Float.NaN;
					break;
				}
				continue;
			}*/
			if((lastNextTimeStamps[i] > 0) && (lastNextTimeStamps[i] < result.nextTimeStamp))
				result.nextTimeStamp = lastNextTimeStamps[i];
     	}
    	result.value = currentValues;
    	result.duration = result.nextTimeStamp - now;
    	return result;
    }
 }
