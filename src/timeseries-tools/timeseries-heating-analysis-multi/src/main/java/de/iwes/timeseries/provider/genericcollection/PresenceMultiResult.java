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
package de.iwes.timeseries.provider.genericcollection;

import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.helper.EfficientTimeSeriesArray;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;
import de.iwes.util.timer.AbsoluteTimeHelper;
import de.iwes.util.timer.AbsoluteTiming;

//TODO: Documentation how to setup data for JSON-export and other
public class PresenceMultiResult extends GaRoMultiResultExtended {

	public int gwCount = 0;
	
	//time series over full time evaluated
	FloatTimeSeries overallPresenceOverTime;
	public EfficientTimeSeriesArray getOverallPresenceOverTimeValues() {
		return EfficientTimeSeriesArray.getInstance(overallPresenceOverTime);
	}
	public void setOverallPresenceOverTimeValues(EfficientTimeSeriesArray value) {
		overallPresenceOverTime = value.toFloatTimeSeries(); //EfficientTimeSeriesArray.setValue(value);
	}
	public FloatTimeSeries nonForJsonOverallPresenceOverTime() {
		return overallPresenceOverTime;
	}

	//daily average time series
	public static final long DAY = 24*3600*1000l;
	FloatTimeSeries overallPresenceDaily = new FloatTreeTimeSeries();
	public EfficientTimeSeriesArray getOverallPresenceDailyValues() {
		return EfficientTimeSeriesArray.getInstance(overallPresenceDaily);
	}
	public void setOverallPresenceDailyValues(EfficientTimeSeriesArray value) {
		overallPresenceDaily = value.toFloatTimeSeries(); //EfficientTimeSeriesArray.setValue(value);
	}
	public FloatTimeSeries nonForJsonOverallPresenceDaily() {
		return overallPresenceDaily;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PresenceMultiResult(List<MultiEvaluationInputGeneric> inputData, long start, long end,
			Collection<ConfigurationInstance> configurations) {
		super((List)inputData, start, end, configurations);
	}
	
	
	/**!! To be used only be JSON deserialization !!*/
	public PresenceMultiResult() {
		super(null, 0, 0, null);
	}	
	
	@Override
	public void finishRoom(GaRoMultiResultExtended result, String roomId) {}

	@Override
	public void finishGateway(GaRoMultiResultExtended result, String gw) {
		gwCount++;
	}

	@Override
	public void finishTimeStep(GaRoMultiResultExtended result) {
		PresenceMultiResult oresult = (PresenceMultiResult) result;
		
		final long resultDuration = oresult.endTime - oresult.startTime;
		final int sizePerSingleEval;
		if(resultDuration % PresenceEvalProvider.TIMESERIES_STEP1 != 0) {
			System.out.println("Warning: Timeseries is no divisor of duration!");
			sizePerSingleEval = (int) (resultDuration / PresenceEvalProvider.TIMESERIES_STEP1)+1;
		} else
			sizePerSingleEval = (int) (resultDuration / PresenceEvalProvider.TIMESERIES_STEP1);
		final float[] overallValues = new float[sizePerSingleEval];
		final long[] timeStamps = new long[sizePerSingleEval];
		final int[] countVals = new int[sizePerSingleEval];
		long timeStamp = oresult.startTime;
		for(int i=0; i<sizePerSingleEval; i++) {
			overallValues[i] = 0;
			countVals[i] = 0;
			timeStamps[i] = timeStamp;
			timeStamp += PresenceEvalProvider.TIMESERIES_STEP1;
		}
		
		for(RoomData evalData: result.roomEvals) {
			SingleEvaluationResult b;
			try {
				b = evalData.evalResultObjects().get(PresenceEvalProvider.PRESENCE_FIXED_TIMESERIES);
			} catch(NullPointerException e) {
				System.out.println("Should not occur!");
				continue;
			}
			ReadOnlyTimeSeries sourceTs = ((TimeSeriesResult)b).getValue();
			int i=0;
			for(SampledValue v: sourceTs.getValues(Long.MIN_VALUE)) {
				if(v.getTimestamp() != timeStamps[i]) {
					System.out.println("Wrong time stamp!");
					//throw new IllegalStateException("Wrong time stamp["+i+"]:"+v.getTimestamp()+" / "+timeStamps[i]);
				}
				overallValues[i] += v.getValue().getFloatValue();
				(countVals[i])++;
			}
			//We do not need to store all the single-room time series here
			evalData.evalResultObjects().remove(PresenceEvalProvider.PRESENCE_FIXED_TIMESERIES);
		}
		oresult.overallPresenceOverTime = new FloatTreeTimeSeries();
		for(int i=0; i<sizePerSingleEval; i++) {
			oresult.overallPresenceOverTime.addValue(new SampledValue(
					new FloatValue(overallValues[i]/countVals[i]), timeStamps[i], Quality.GOOD));
		}
	}
	
	@SuppressWarnings("null")
	@Override
	public void finishTotal(GaRoSuperEvalResult<?> result) {
		PresenceMultiResult oresult = null; //(PresenceMultiResult) result;
		final int sizePerSingleEval;
		sizePerSingleEval = (int) (DAY / PresenceEvalProvider.TIMESERIES_STEP1);
		final float[] overallValues = new float[sizePerSingleEval];
		final long[] timeStamps = new long[sizePerSingleEval];
		final int[] countVals = new int[sizePerSingleEval];
		long timeStampDay = 0;
		for(int i=0; i<sizePerSingleEval; i++) {
			overallValues[i] = 0;
			countVals[i] = 0;
			timeStamps[i] = timeStampDay;
			timeStampDay += PresenceEvalProvider.TIMESERIES_STEP1;
		}
		
		long timeStampTot = AbsoluteTimeHelper.getIntervalStart(result.startTime, AbsoluteTiming.DAY);
		for(int i=0; i<sizePerSingleEval; i++) {
			SampledValue val = oresult.overallPresenceOverTime.getValue(timeStampTot);
			overallValues[i] += val.getValue().getFloatValue();
		}
	}

}
