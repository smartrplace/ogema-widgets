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
package de.iwes.timeseries.provider.comfortTemp;

import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;

@Deprecated
public class ComfortTemperatureEvaluation extends SpecificEvalBaseImpl<ComfortTemperatureEvalValueContainer> {
	public static final int TEMPSETP_IDX = 0; 
	public static final int INPUT_NUM = 1;

	private final boolean upperQuantileRequired; 

	@Override
	protected ComfortTemperatureEvalValueContainer initValueContainer(List<EvaluationInput> input) {
		return new ComfortTemperatureEvalValueContainer(size, requestedResults, input);
	}

	public ComfortTemperatureEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
		super(input, requestedResults, configurations, listener, time,
				ComfortTemperatureEvalProvider.ID, INPUT_NUM, null); //"RoomBaseEvaluation", 3);
		this.upperQuantileRequired = requestedResults.contains(ComfortTemperatureEvalProvider.COMFORT_TEMP2) || requestedResults.contains(ComfortTemperatureEvalProvider.SETPOINTS_USED_NUM);
		lastValues = new float[size];
		for(int i=0; i<size; i++) lastValues[i] = Float.NaN;
	}

	private long lastEqualTime = -1;
	private boolean lastEqual = true;
	private float[] lastValues;
	
	@Override
	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
			int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		switch(idxOfRequestedInput) {
		case TEMPSETP_IDX:// temperature sensor value
			final float val = sv.getValue().getFloatValue();
			if(requestedResults.contains(ComfortTemperatureEvalProvider.COMFORT_TEMP1))
				values.lowerEstimator.addValue(val, duration);
			if(upperQuantileRequired)
				values.upperEstimator.addValue(val, duration);
			if(requestedResults.contains(ComfortTemperatureEvalProvider.COMFORT_TEMP3) ||
					requestedResults.contains(ComfortTemperatureEvalProvider.SETP_TEMP_AV))
				values.maxEstimator.addValue(val, duration);
			if(requestedResults.contains(ComfortTemperatureEvalProvider.MULTI_THERMOSTAT_DEVIATIONS_FOUND_NUM)) {
				if(!lastEqual && ((timeStamp - lastEqualTime) > ComfortTemperatureEvalValueContainer.MAX_DEVIATION_TIME_ACCEPTED)) {
					values.countMultiValveDeviations++;
				}
				lastValues[idxOfEvaluationInput] = val;
				boolean eq = true;
				for(int i=0; i<size; i++) {
					if(Float.isNaN(lastValues[i])) continue;
					if(lastValues[i] != val) {
						eq = false;
						break;
					}
				}
				if(eq) {
					lastEqualTime = timeStamp;
					lastEqual = true;
				} else {
					lastEqual = false;					
				}
			}
		}
	}

}
