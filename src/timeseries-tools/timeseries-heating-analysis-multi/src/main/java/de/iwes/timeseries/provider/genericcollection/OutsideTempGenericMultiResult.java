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
import java.util.HashMap;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.helper.EfficientTimeSeriesArray;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;

//TODO: Documentation how to setup data for JSON-export and other
public class OutsideTempGenericMultiResult extends GaRoMultiResultExtended {

	public int gwWithOutsideSensorCount = 0;
	
	public float generalOutsideTemperatureAv;
	FloatTimeSeries generalOutsideTemperature;
	public EfficientTimeSeriesArray getGeneralOutsideTemperatureValues() {
		return EfficientTimeSeriesArray.getInstance(generalOutsideTemperature);
	}
	public void setGeneralOutsideTemperatureValues(EfficientTimeSeriesArray value) {
		generalOutsideTemperature = value.toFloatTimeSeries(); //EfficientTimeSeriesArray.setValue(value);
	}
	public FloatTimeSeries nonForJsonGeneralOutsideTemperature() {
		return generalOutsideTemperature;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OutsideTempGenericMultiResult(List<MultiEvaluationInputGeneric> inputData, long start, long end,
			Collection<ConfigurationInstance> configurations) {
		super((List)inputData, start, end, configurations);
	}
	
	
	/**!! To be used only be JSON deserialization !!*/
	public OutsideTempGenericMultiResult() {
		super(null, 0, 0, null);
	}	
	
	@Override
	public void finishRoom(GaRoMultiResultExtended result, String roomId) {}

	@Override
	public void finishGateway(GaRoMultiResultExtended result, String gw) {
		gwWithOutsideSensorCount++;
	}

	@Override
	public void finishTimeStep(GaRoMultiResultExtended result) {
		OutsideTempGenericMultiResult oresult = (OutsideTempGenericMultiResult) result;
		RoomData lowSensor = null;
		float lowAv = Float.MAX_VALUE;
		float sumAv = 0;
		int countAv = 0;
		
		long minimumGapTime = Long.MAX_VALUE;
		//first look for minimum gap time
		
		for(RoomData evalData: result.roomEvals) {
			SingleEvaluationResult b = evalData.evalResultObjects().get(OutsideTempGenericEvalProvider.OUTSIDE_TEMP_AVERAGE90);
			@SuppressWarnings("unchecked")
			float valf = ((SingleValueResult<Float>)b).getValue();
			if(Float.isNaN(valf)) continue;
			sumAv += valf;
			countAv++;

			SingleEvaluationResult a = evalData.evalResultObjects().get(OutsideTempGenericEvalProvider.GAP_TIME);
			@SuppressWarnings("unchecked")
			long val = ((SingleValueResult<Long>)a).getValue();
			if((val < minimumGapTime)||
					((val == minimumGapTime)&&(val < lowAv))) {
				lowSensor = evalData;
				minimumGapTime = val;
				lowAv = valf;
			}
		}

		if(lowSensor == null) return;
		float totalAv = sumAv / countAv;
		float correction = totalAv - lowAv;
		SingleEvaluationResult b = lowSensor.evalResultObjects().get(OutsideTempGenericEvalProvider.OUTSIDE_TEMP_TIMESERIES);
		ReadOnlyTimeSeries sourceTs = ((TimeSeriesResult)b).getValue();
		oresult.generalOutsideTemperature = new FloatTreeTimeSeries();
		for(SampledValue v: sourceTs.getValues(Long.MIN_VALUE)) {
			oresult.generalOutsideTemperature.addValue(new SampledValue(
					new FloatValue(v.getValue().getFloatValue()-correction), v.getTimestamp(), Quality.GOOD));
		}
		oresult.generalOutsideTemperatureAv = ValueResourceUtils.getAverage(oresult.generalOutsideTemperature,
				result.startTime, result.endTime);
		//MultiTimeSeriesBuilder.newBuilder(timeSeries, Float.class);
	}
	@Override
	public void finishTotal(GaRoSuperEvalResult<?> result) {
		result.evalResults = new HashMap<String, String>();
		result.timeSeriesResults = new HashMap<>();
		float avSum = 0;
		float avCount = 0;
		
		FloatTimeSeries outTS = new FloatTreeTimeSeries();
		
		for(GaRoMultiResult singleTimeStepResult: result.intervalResults) {
			OutsideTempGenericMultiResult oresult = (OutsideTempGenericMultiResult)singleTimeStepResult;
			if(!Float.isNaN(oresult.generalOutsideTemperatureAv)) {
				avSum += oresult.generalOutsideTemperatureAv;
				avCount++;
			}
			if(oresult.generalOutsideTemperature != null) {
				outTS.addValues(oresult.generalOutsideTemperature.getValues(0));
			}
		}
		float av = avSum / avCount;
		result.evalResults.put(OutsideTempGenericEvalProvider.OUTSIDE_TEMP_AVERAGE_OVERALL.id(), String.format("%f", av));
		result.timeSeriesResults.put(OutsideTempGenericEvalProvider.OUTSIDE_TEMP_TIMESERIES_OVERALL.id(),
				EfficientTimeSeriesArray.getInstance(outTS));
	}

}
