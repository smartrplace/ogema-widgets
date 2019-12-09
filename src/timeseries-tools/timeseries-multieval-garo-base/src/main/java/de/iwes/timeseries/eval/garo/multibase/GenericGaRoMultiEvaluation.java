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
package de.iwes.timeseries.eval.garo.multibase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.Status;
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
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInstance;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;

public class GenericGaRoMultiEvaluation<P extends GaRoSingleEvalProvider> extends GaRoMultiEvaluationInstance<GaRoMultiResult> {
	public final boolean doBasicEval;

	private final BasicEvaluationProvider basicEval = new BasicEvaluationProvider();
	//private final GapEvaluationProvider gapEval = new GapEvaluationProvider();
	protected final P roomEval;
	private final Class<? extends GaRoMultiResultExtended> resultTypeExtended;
	private final List<ResultType> resultsRequested;
	private GaRoMultiResultExtended resultExtended = null;

	public GenericGaRoMultiEvaluation(List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		super(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw);
		this.roomEval = singleProvider;
		this.doBasicEval = doBasicEval;
		if(resultsRequested == null)
			this.resultsRequested = roomEval.resultTypes();
		else
			this.resultsRequested = resultsRequested;
		resultTypeExtended = singleProvider.extendedResultDefinition();
	}

	@Override
	public GaRoMultiResult initNewResultGaRo(long start, long end, Collection<ConfigurationInstance> configurations) {
		if(resultTypeExtended != null) {
			try {
				Constructor<? extends GaRoMultiResultExtended> cons =
						resultTypeExtended.getConstructor(List.class, long.class, long.class, Collection.class);
				resultExtended = cons.newInstance(input, start, end, configurations);
				return resultExtended;
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}			
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		GaRoMultiResult result = new GaRoMultiResult((List)input, start, end, configurations);
		
		//If you have overallResults, initialize in overwritten method here
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<GaRoMultiResultUntyped> getResultType() {
		return GaRoMultiResultUntyped.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<GaRoSuperEvalResult> getSuperResultType() {
		return GaRoSuperEvalResult.class;
	}

	//@Override
	//protected abstract List<GaRoSelectionItem> startRoomLevel(List<GaRoSelectionItem> levelItems, GaRoMultiResult result, String gw);

	GaRoSelectionItem currentRoom;
	@Override
	protected void startTSLevel(List<GaRoSelectionItem> levelItems, GaRoMultiResult result, GaRoSelectionItem roomItem) {
		currentRoom = roomItem;
	}

	@Override
	protected void processInputType(int inputIdx, List<TimeSeriesData> tsList,
			 GaRoDataTypeI dt, GaRoMultiResult result) {
		try {
		for(TimeSeriesData data: tsList) {
			if(doBasicEval) {
				final List<EvaluationInput> inputs = Arrays.<EvaluationInput> asList(new EvaluationInput[]{new EvaluationInputImpl(Arrays.<TimeSeriesData>asList(new TimeSeriesData[]{data}))});
				final EvaluationInstance instance = EvaluationUtils.performEvaluationBlocking(basicEval, inputs, basicEval.resultTypes(), result.configurations);
					
				long inputTime = result.endTime - result.startTime;
				Long nonGapTime = EvalHelperExtended.getSingleResultLong(BasicEvaluationProvider.NON_GAPTIME, instance);
				if((nonGapTime == null) || (inputTime <= 0)) {
					//write to your result type if you want to store this information
					//result.overallResults().any.timeSeriesNum++;
					continue;					
				}
				double quality = ((double)(nonGapTime))/(double)(inputTime);
				if(quality < MINIMUM_QUALITY_REQUIRED) {
					//write to your result type if you want to store this information
					//result.overallResults().any.timeSeriesNum++;
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
	protected void performRoomEvaluation(List<TimeSeriesData>[] inputTimeSeries, GaRoMultiResult result) {
		final List<EvaluationInput> inputs = new ArrayList<>();
		
		if(inputTimeSeries != null) for(List<TimeSeriesData> tsdList: inputTimeSeries) {
			inputs.add(new EvaluationInputImpl(tsdList));
		}
		
		long startTime = EvaluationUtils.getStartAndEndTime(result.configurations, inputs, false)[0];
		String roomId;
		if(currentRoom == null) {
			roomId = GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID;
		} else roomId = getPath(currentRoom);
		roomEval.provideCurrentValues(result.gwId(), roomId, startTime, superResult);
		if(roomEval instanceof GaRoSingleEvalProviderPreEvalRequesting) {
			GaRoSingleEvalProviderPreEvalRequesting preEvalReq = (GaRoSingleEvalProviderPreEvalRequesting) roomEval;
			List<EvaluationInputImpl> toInject = preEvalReq.timeSeriesToInject();
			GaRoDataTypeI[] types = roomEval.getGaRoInputTypes();
			int idx = 0;
			if(toInject != null) for(EvaluationInputImpl inp: toInject) {
				if(idx >= types.length) throw new IllegalStateException("More pre-eval input then placeholders in getGaRoInputTypes!(1)");
				while((types[idx] != GaRoDataType.PreEvaluated) && (types[idx].primaryEvalProvider() == null)) {
					idx++;
					if(idx >= types.length) throw new IllegalStateException("More pre-eval input then placeholders in getGaRoInputTypes!(2)");
				}
				inputs.set(idx, inp);
				idx++;
			}
		}
		
		final EvaluationInstance instance = EvaluationUtils.performEvaluationBlocking(roomEval, inputs, resultsRequested, result.configurations); //myConfigurations );
		final Map<ResultType, EvaluationResult> results = instance.getResults();
		
		GaRoEvalHelper.printAllResults(result.roomData().id, results, EvaluationUtils.getStartAndEndTime(result.configurations, inputs, false));
		if(resultExtended != null) {
			result.roomData().initEvalResultObjects();
			for(Entry<ResultType, EvaluationResult> re: instance.getResults().entrySet()) {
				List<SingleEvaluationResult> rlist = re.getValue().getResults();
				if(!rlist.isEmpty()) result.roomData().evalResultObjects().put(re.getKey(), rlist.get(0));
			}
			//String roomId;
			//if(currentRoom == null) {
			//	roomId = GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID;
			//} else roomId = getPath(currentRoom);
			resultExtended.finishRoom(resultExtended, roomId);
		}
		//In finishRoom we have the chance to remove results so that they are not added to the evalResults map
		result.roomData().evalResults = EvalHelperExtended.getResults(instance);
		result.roomData().timeSeriesResults = EvalHelperExtended.getResultsTS(instance);
	}
	
	@Override
	protected void finishRoomLevel(GaRoMultiResult result) {
		if(resultExtended != null) {
			resultExtended.finishGateway(resultExtended, result.gwId());
		}
	}

	@Override
	protected void finishGwLevel(GaRoMultiResult result) {
		if(resultExtended != null) {
			resultExtended.finishTimeStep(resultExtended);
		}
	}
	
	@SuppressWarnings({ "rawtypes"})
	@Override
	public Status finish() {
		if(resultTypeExtended != null) {
			resultExtended.finishTotal((GaRoSuperEvalResult) superResult);	
		}
		return super.finish();
	}
	
	@Override
	protected List<GaRoSelectionItem> startRoomLevel(List<GaRoSelectionItem> levelItems, GaRoMultiResult result, String gw) {
		int[] roomTypeList = roomEval.getRoomTypes();
		if(roomTypeList == null) {
			/*GaRoSelectionItem toRemove = null;
			for(GaRoSelectionItem lvlIt: levelItems) {
				if(((GaRoSelectionItemJAXB)lvlIt).resource == null) {
					toRemove = lvlIt;
					break;
				}
				if(toRemove != null) levelItems.remove(toRemove);
			}*/
			return levelItems;
		}
		List<GaRoSelectionItem> retVal = new ArrayList<>();
		for(GaRoSelectionItem lvlIt: levelItems) {
			//Resource room = ((GaRoSelectionItemJAXB)lvlIt).getResource();
			if(lvlIt.id().equals(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID)) { //(type == null) {
				if(ArrayUtils.contains(roomTypeList, -1))
					retVal.add(lvlIt);
				continue;
			}
			//Resource typeRes = room.get("type");
			//if(typeRes instanceof IntegerResource) {
			else {
				Integer type = ((GaRoSelectionItem)lvlIt).getRoomType();
				//int roomType = type;
				//int roomType = ((IntegerResource)typeRes).getValue();
				boolean found = false;
				for(int rt: roomTypeList) {
					if(type == null || type < 0) {
						if(rt < -1) { //-1 means overall-room
							found = true;
							break;
						}
					}
					else if(rt == type) {
						found = true;
						break;
					}
				}
				if(found) retVal.add(lvlIt);
			}		
		}
		return retVal;
		//result.dpNumGw = 0;
		//result.resultsGw = new HashMap<>();
	}
}
