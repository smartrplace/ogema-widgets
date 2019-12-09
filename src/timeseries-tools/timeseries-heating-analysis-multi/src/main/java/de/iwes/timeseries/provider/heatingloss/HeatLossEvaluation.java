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
package de.iwes.timeseries.provider.heatingloss;

import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;
import de.iwes.timeseries.provider.comfortTemp.ComfortTemperatureEvalProvider;
import de.iwes.timeseries.provider.outsideTemperature.OutsideTemperatureMultiResult;

public class HeatLossEvaluation extends SpecificEvalBaseImpl<HeatLossEvalValueContainer> {
	public static final long MAX_DATA_INTERVAL = 1800*1000;

	private final HeatLossEvalProvider evalProvider;
	private final FloatTimeSeries ts;
	private final InputSeriesAggregator tempSetpoint;
	//private final InputSeriesAggregator tempSensor;
	
	@Override
	protected HeatLossEvalValueContainer initValueContainer(List<EvaluationInput> input) {
		return new HeatLossEvalValueContainer(size, requestedResults, input);
	}

	public HeatLossEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			HeatLossEvalProvider heatLossEvalProvider) {
		super(input, requestedResults, configurations, listener, time,
				HeatLossEvalProvider.ID, HeatLossEvalProvider.INPUT_NUM, null); //"RoomBaseEvaluation", 3);
		this.evalProvider = heatLossEvalProvider;
		
		tempSetpoint = new InputSeriesAggregator(nrInput, getIdxSumOfPrevious(),
				HeatLossEvalProvider.TEMPSETP_IDX, startEnd[1]);
		//tempSensor = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
		//		HeatLossEvalProvider.TEMP_M_IDX, startEnd[1]);
		
		RoomData dailyComfortTemp1 = evalProvider.comfortTempProvider.getRoomData(startEnd[0],
				evalProvider.currentGwId, evalProvider.currentRoomId);
		values.dailyComfortTemp = Float.parseFloat(dailyComfortTemp1.evalResults.get(ComfortTemperatureEvalProvider.COMFORT_TEMP2.id()));
		
		OutsideTemperatureMultiResult outRes = evalProvider.outsideTempProvider.getIntervalData(startEnd[0]);
		ts = outRes.nonForJsonGeneralOutsideTemperature();
		ts.setInterpolationMode(InterpolationMode.LINEAR);
		values.dailyAv = ValueResourceUtils.getAverage(ts, startEnd[0], startEnd[1]);
	}

	@Override
	protected long maximumGapTimeAccepted(int idxOfRequestedInput) {
		switch(idxOfRequestedInput) {
		case HeatLossEvalProvider.TEMPSETP_IDX:// temperature sensor value
			return 5*MAX_DATA_INTERVAL;
		case HeatLossEvalProvider.TEMP_M_IDX:// temperature sensor value
			return MAX_DATA_INTERVAL;
		default:
			throw new IllegalStateException();
		}
	}
	
	@Override
	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
			int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long durationDeprecated) {
		switch(idxOfRequestedInput) {
		case HeatLossEvalProvider.TEMPSETP_IDX:
			ValueDuration vd = tempSetpoint.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
			final float valSP = vd.value;
			final long duration = (vd.duration>0)?vd.duration:0;
			float diff = values.dailyComfortTemp-valSP;
			if(requestedResults.contains(HeatLossEvalProvider.SETPOINT_RELATIVE_AV)) {
				values.setPointRelativeIntegrator.addValue(diff, duration);
			}
			if(requestedResults.contains(HeatLossEvalProvider.SETPOINT_REDUCTION_AV)) {
				if(diff > 0)
					values.setPointReductionIntegrator.addValue(diff, duration);
			}
			if(requestedResults.contains(HeatLossEvalProvider.HEATING_DEGREE_DAYS_LOWERED) && (ts != null)) {
				SampledValue outSV = ts.getValue(timeStamp);
				if(outSV != null) {
					diff = valSP - outSV.getValue().getFloatValue() -
							HeatLossEvalValueContainer.DELTA_T_HEATING_LIMIT_FIXED;
					if(diff >= 0)
						values.heatingDegreeDaysIntegrator.addValue(diff, duration);
				}
			}
			break;
		case HeatLossEvalProvider.TEMP_M_IDX:
			//final float val = tempSensor.getCurrentValue(sv, dataPoint, true);
		}
	}
}
