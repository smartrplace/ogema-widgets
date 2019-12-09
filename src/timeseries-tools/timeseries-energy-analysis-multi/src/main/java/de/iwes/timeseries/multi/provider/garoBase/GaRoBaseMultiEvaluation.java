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
package de.iwes.timeseries.multi.provider.garoBase;

import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.helper.EvalHelperExtended;
import de.iwes.timeseries.eval.base.provider.BasicEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInstance;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.api.helper.base.SpecialGaRoEvalResult;

public class GaRoBaseMultiEvaluation extends GaRoMultiEvaluationInstance<GaRoBaseMultiResult> {

	private final BasicEvaluationProvider basicEval = new BasicEvaluationProvider();
	
	public GaRoBaseMultiEvaluation(List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<GaRoBaseMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw) {
		super(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw);
	}

	@Override
	public GaRoBaseMultiResult initNewResultGaRo(long start, long end, Collection<ConfigurationInstance> configurations) {
		GaRoBaseMultiResult result = new GaRoBaseMultiResult(input, start, end, configurations);
		
		result.overallResults().any = new SpecialGaRoEvalResult(GaRoDataType.Any);
		result.overallResults().results.put(GaRoDataType.Any, result.overallResults().any);
		result.overallResults().resultsPerRoomType = new HashMap<>();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<GaRoBaseMultiResult> getResultType() {
		return GaRoBaseMultiResult.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<GaRoSuperEvalResult> getSuperResultType() {
		return GaRoSuperEvalResult.class;
	}

	@Override
	protected List<GaRoSelectionItem> startRoomLevel(List<GaRoSelectionItem> levelItems, GaRoBaseMultiResult result, String gw) {
		result.dpNumGw = 0;
		result.resultsGw = new HashMap<>();
		return levelItems;
	}

	@Override
	protected void startTSLevel(List<GaRoSelectionItem> levelItems, GaRoBaseMultiResult result, GaRoSelectionItem roomItem) {
	}

	@Override
	protected void processInputType(int inputIdx, List<TimeSeriesData> tsList,
			 GaRoDataTypeI dt, GaRoBaseMultiResult result) {
		try {
		for(TimeSeriesData data: tsList) {
			final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(new EvaluationInput[]{new EvaluationInputImpl(Arrays.<TimeSeriesData>asList(new TimeSeriesData[]{data}))});
			final EvaluationInstance instance = EvaluationUtils.performEvaluationBlocking(basicEval, inputs, basicEval.resultTypes(), result.configurations);
				
			//long[] startEnd = EvaluationUtils.getStartAndEndTime(configurations, inputs, false);
			long inputTime = result.endTime - result.startTime;
			Long nonGapTime = EvalHelperExtended.getSingleResultLong(BasicEvaluationProvider.NON_GAPTIME, instance);
			if((nonGapTime == null) || (inputTime <= 0)) {
				result.overallResults().any.timeSeriesNum++;
				continue;					
			}
			double quality = ((double)(nonGapTime))/(double)(inputTime);
			if(quality < MINIMUM_QUALITY_REQUIRED) {
				result.overallResults().any.timeSeriesNum++;
				continue;
			}
			result.roomData().evalResults = EvalHelperExtended.getResults(instance);
			
			GaRoTimeSeriesId tsEval = new GaRoTimeSeriesId();
			tsEval.gwId = result.gwId();		
			tsEval.timeSeriesId = data.id(); //tsid;
			result.timeSeriesEvaluated.add(tsEval);
		}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void performRoomEvaluation(List<TimeSeriesData>[] inputTimeSeries, GaRoBaseMultiResult result) {
	}
	
	@Override
	protected void finishRoomLevel(GaRoBaseMultiResult result) {
		if(result.dpNumGw > 10* GaRoBaseMultiResult.MINIMUM_DP_REQUIRED) result.overallResults.gwCountWithData++;
		else result.overallResults().missingSources.add(result.gwId());
	}
	
	/*@Override
	protected Integer getRoomType(Resource room) {
		return GenericGaRoMultiEvaluationJAXB.getRoomTypeStatic(room);
	}

	@Override
	protected String getName(Resource room) {
		return room.getName();
	}

	@Override
	protected String getPath(Resource room) {
		return room.getPath();
	}*/
}
