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
package de.iwes.timeseries.multi.provider.comfortTemp;

import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
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
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.timeseries.eval.garo.api.helper.base.SpecialGaRoEvalResult;
import de.iwes.timeseries.provider.comfortTemp.ComfortTemperatureEvalProvider;
import de.iwes.timeseries.provider.comfortTemp.ComfortTemperatureEvaluation;

@Deprecated
public class CTGaRoEvaluation extends GaRoMultiEvaluationInstance<CTGaRoMultiResult> {
	public static final boolean doBasicEval = false;

	private final BasicEvaluationProvider basicEval = new BasicEvaluationProvider();
	//private final GapEvaluationProvider gapEval = new GapEvaluationProvider();
	private final ComfortTemperatureEvalProvider roomEval = new ComfortTemperatureEvalProvider();
	
	public CTGaRoEvaluation(List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<CTGaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw) {
		super(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw);
	}

	@Override
	public CTGaRoMultiResult initNewResultGaRo(long start, long end, Collection<ConfigurationInstance> configurations) {
		CTGaRoMultiResult result = new CTGaRoMultiResult(input, start, end, configurations);
		
		result.overallResults().any = new SpecialGaRoEvalResult(GaRoDataType.Any);
		result.overallResults().results.put(GaRoDataType.Any, result.overallResults().any);
		result.overallResults().resultsPerRoomType = new HashMap<>();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<CTGaRoMultiResult> getResultType() {
		return CTGaRoMultiResult.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<GaRoSuperEvalResult> getSuperResultType() {
		return GaRoSuperEvalResult.class;
	}

	@Override
	protected List<GaRoSelectionItem> startRoomLevel(List<GaRoSelectionItem> levelItems, CTGaRoMultiResult result, String gw) {
		result.dpNumGw = 0;
		result.resultsGw = new HashMap<>();
		return levelItems;
	}

	@Override
	protected void startTSLevel(List<GaRoSelectionItem> levelItems, CTGaRoMultiResult result, GaRoSelectionItem roomItem) {
	}

	@Override
	protected void processInputType(int inputIdx, List<TimeSeriesData> tsList,
			 GaRoDataTypeI dt, CTGaRoMultiResult result) {
		try {
		for(TimeSeriesData data: tsList) {
			if(doBasicEval) {
				final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(new EvaluationInput[]{new EvaluationInputImpl(Arrays.<TimeSeriesData>asList(new TimeSeriesData[]{data}))});
				final EvaluationInstance instance = EvaluationUtils.performEvaluationBlocking(basicEval, inputs, basicEval.resultTypes(), result.configurations);
					
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
			}
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
	protected void performRoomEvaluation(List<TimeSeriesData>[] inputTimeSeries, CTGaRoMultiResult result) {
		List<TimeSeriesData> tempSPData = inputTimeSeries[ComfortTemperatureEvaluation.TEMPSETP_IDX];
		
		/*public class ExampleConfigObject {
			//Results from previous evaluation
			public List<RoomData> roomEvals;
		}
	 	ExampleConfigObject configObject = new ExampleConfigObject();
		final List<ConfigurationInstance> localConfigs = new ArrayList<>();
		for(ConfigurationInstance c: result.configurations) {
			localConfigs.add(c);
		}
		localConfigs.add(ConfigurationBuilder.newBuilder(GenericObjectConfiguration.class).withLabel("SpecificConfigObject").
				withDefaultObject(configObject).build().defaultValues());*/
		
		final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(new EvaluationInput[]{new EvaluationInputImpl(tempSPData)});
		final EvaluationInstance instance = EvaluationUtils.performEvaluationBlocking(roomEval, inputs, roomEval.resultTypes(), result.configurations);
		final Map<ResultType, EvaluationResult> results = instance.getResults();
		
		GaRoEvalHelper.printAllResults(result.roomData().id, results, EvaluationUtils.getStartAndEndTime(result.configurations, inputs, false));
		result.roomData().evalResults = EvalHelperExtended.getResults(instance);
	}
	
	@Override
	protected void finishRoomLevel(CTGaRoMultiResult result) {
		if(result.dpNumGw > 10* CTGaRoMultiResult.MINIMUM_DP_REQUIRED) result.overallResults.gwCountWithData++;
		else result.overallResults().missingSources.add(result.gwId());
	}

}
