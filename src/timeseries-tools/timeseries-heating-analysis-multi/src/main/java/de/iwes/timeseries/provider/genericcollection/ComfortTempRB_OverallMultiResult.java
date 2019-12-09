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

import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;

/** Here we do not need a special SuperEvalResult class as we just perform additional processing here,
 * but do not define any additional values that would have to be read from JSON later on.
 * 
 */
public class ComfortTempRB_OverallMultiResult extends GaRoMultiResultExtended {
	/*FloatTimeSeries generalOutsideTemperature;
	public EfficientTimeSeriesArray getGeneralOutsideTemperatureValues() {
		return EfficientTimeSeriesArray.getInstance(generalOutsideTemperature);
	}
	public void setGeneralOutsideTemperatureValues(EfficientTimeSeriesArray value) {
		generalOutsideTemperature = value.toFloatTimeSeries(); //EfficientTimeSeriesArray.setValue(value);
	}
	public FloatTimeSeries nonForJsonGeneralOutsideTemperature() {
		return generalOutsideTemperature;
	}*/
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ComfortTempRB_OverallMultiResult(List<MultiEvaluationInputGeneric> inputData, long start, long end,
			Collection<ConfigurationInstance> configurations) {
		super((List)inputData, start, end, configurations);
	}
	
	
	/**!! To be used only be JSON deserialization !!*/
	public ComfortTempRB_OverallMultiResult() {
		super(null, 0, 0, null);
	}	
	
	@Override
	public void finishRoom(GaRoMultiResultExtended result, String roomId) {}

	/** Demontrates how to aggregate room data for a gateway into an overall room*/
	@Override
	public void finishGateway(GaRoMultiResultExtended result, String gw) {
		float gwDiffSum = 0;
		int countAv = 0;
		for(RoomData evalData: result.roomEvals) {
			if(evalData.gwId.equals(gw)) {
				SingleEvaluationResult b = evalData.evalResultObjects().get(ComfortTempRB_OverallProvider.ROOM_TEMP_DECREASE);
				@SuppressWarnings("unchecked")
				float valf = ((SingleValueResult<Float>)b).getValue();
				if(Float.isNaN(valf)) continue;
				gwDiffSum += valf;
				countAv++;
			}
		}
		RoomData resultRD = new RoomData();
		resultRD.roomType = -2; //specific type
		resultRD.gwId = gw;
		resultRD.id = GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID; //"_Overall";
		if(countAv > 0) resultRD.evalResults.put("Temp_Decrease_Overall", ""+gwDiffSum/countAv);
		result.roomEvals.add(resultRD );
	}

	@Override
	public void finishTimeStep(GaRoMultiResultExtended result) {
	}
	@Override
	public void finishTotal(GaRoSuperEvalResult<?> result) {}

}
