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
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;

/** This class is required when a sub-evaluation needs to be implemented that uses more than one of the input
 * values of an evaluation, but not all input values. In this case the duration of each value within the 
 * inputs relevant to the sub-evaluation are calculated here. Also the last effective value of each input
 * is given.<br>
 * Getting the last effective value of all inputs can also be relevant for usage on an entire evaluation,
 * but usually it is recommended to just keep the values without such a utility class in this case.
 *
 */
public class InputSeriesMultiAggregator {
	private final InputSeriesAggregator[] aggregators;
	private final int size;
	private final float[] lastValues;
	private final long[] lastNextTimeStamps;
	
	/**
	 * 
	 * @param aggregators. The endTime of the first aggregator will be used as endTime for entire evaluation
	 * if the endTimes would differ (which normally should not be the case)
	 */
	public InputSeriesMultiAggregator(InputSeriesAggregator[] aggregators) {
		this.aggregators = aggregators;
		this.size = aggregators.length;
		this.lastValues = new float[size];
		this.lastNextTimeStamps = new long[size];
		for(int i=0; i<size; i++) lastNextTimeStamps[i] = -1;
	}

	public class MultiValueDuration {
		public float[] values;
		public long duration;
	}
	
	/** Call this method every time a new value is available for any of the input series. You should (?) not
	 * call / need (?) to not call {@link InputSeriesAggregator#getCurrentValueDuration(int, SampledValue, SampledValueDataPoint, boolean)}
	 * for the input single InputSeriesAggregators.
	 * 
	 * @param sv
	 * @param dataPoint
	 * @param ignoreMissingPoints
	 * @param idxOfRequestedInput index of input as declared in the constructor input array. So this is
	 * 		usually NOT identical with idxOfRequestedInput in GenericGaRoEvaluationCore#processValue
	 * @param idxOfEvaluationInput usually identical with the same input in processValue
	 * @return
	 */
	public MultiValueDuration getCurrentValueDuration(SampledValue sv, SampledValueDataPoint dataPoint,
			boolean ignoreMissingPoints, int idxOfRequestedInput, int idxOfEvaluationInput) {
		MultiValueDuration result = new MultiValueDuration();
		result.values = new float[size];
		long nextTime = Long.MAX_VALUE;
		for(int i=0; i<size; i++) {
			if(i == idxOfRequestedInput) {
				ValueDuration val = aggregators[i].getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, ignoreMissingPoints);
				result.values[i] = lastValues[i] = val.value;
				lastNextTimeStamps[i] = val.nextTimeStamp;
			} else {
				result.values[i] = lastValues[i];
				if(lastNextTimeStamps[i] <= 0) {
					for(int newIdx=0; newIdx<aggregators[i].nrInput; newIdx++) {
						SampledValue nextSv = dataPoint.getNextElement(aggregators[i].getTotalInputIdx(newIdx));
						if(nextSv != null) {
							lastNextTimeStamps[i] = nextSv.getTimestamp();
							break;
						}
					}
				}
			}
			if((lastNextTimeStamps[i] > 0) && (lastNextTimeStamps[i] < nextTime))
				nextTime = lastNextTimeStamps[i];
		}
		long now = sv.getTimestamp();
		result.duration = aggregators[0].getNextTimeStamp(now, nextTime) - now;
		return result;
	}
}
