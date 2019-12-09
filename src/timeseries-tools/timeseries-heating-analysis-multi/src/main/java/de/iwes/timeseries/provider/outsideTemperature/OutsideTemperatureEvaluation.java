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
package de.iwes.timeseries.provider.outsideTemperature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;

public class OutsideTemperatureEvaluation extends SpecificEvalBaseImpl<OutsideTemperatureEvalValueContainer> {
	public static final int TEMPSETP_IDX = 0; 
	public static final int INPUT_NUM = 1;
	public static final long MAX_DATA_INTERVAL = 3600*1000;
	
	private List<ResultListener> interMediateListeners = new ArrayList<>();

	@Override
	protected OutsideTemperatureEvalValueContainer initValueContainer(List<EvaluationInput> input) {
		return new OutsideTemperatureEvalValueContainer(size, requestedResults, input);
	}

	public OutsideTemperatureEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
		super(input, requestedResults, configurations, listener, time,
				OutsideTemperatureEvalProvider.ID, INPUT_NUM, null); //"RoomBaseEvaluation", 3);
	}

	@Override
	protected long maximumGapTimeAccepted(int idxOfRequestedInput) {
		switch(idxOfRequestedInput) {
		case TEMPSETP_IDX:// temperature sensor value
			return MAX_DATA_INTERVAL;
		default:
			throw new IllegalStateException();
		}
	}
	
	@Override
	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
			int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		switch(idxOfRequestedInput) {
		case TEMPSETP_IDX:// temperature sensor value
			final float val = sv.getValue().getFloatValue();
			if(requestedResults.contains(OutsideTemperatureEvalProvider.OUTSIDE_TEMP_AVERAGE))
				values.baseEstimator.addValue(val, duration);
			if(requestedResults.contains(OutsideTemperatureEvalProvider.OUTSIDE_TEMP_AVERAGE90))
				values.upperEstimator.addValue(val, duration);
			if(requestedResults.contains(OutsideTemperatureEvalProvider.OUTSIDE_TEMP_TIMESERIES)) {
				values.tsBuilder.addValue(sv);
				callListeners(OutsideTemperatureEvalProvider.OUTSIDE_TEMP_TIMESERIES, timeStamp, val);
			}
		}
	}
	
	@Override
	protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput, int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		values.tsBuilder.addValue(new SampledValue(new FloatValue(Float.NaN), sv.getTimestamp(), Quality.BAD));
	}

	@Override
	public void addIntermediateResultListener(ResultListener listener) {
		interMediateListeners.add(listener);
	}
	
	private void callListeners(ResultType type, long timeStamp, float value) {
		SampledValue sv = new SampledValue(new FloatValue(value), timeStamp, Quality.GOOD);
		for(ResultListener listen: interMediateListeners) {
			listen.resultAvailable(type, sv);
		}
	}
}
