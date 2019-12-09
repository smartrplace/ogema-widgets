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
package de.iwes.timeseries.eval.garo.api.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.tools.resource.util.TimeUtils;
import org.slf4j.LoggerFactory;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils.TimeSeriesInputForSingleRequiredInputIdx;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.GaRoStdOverallResults;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;
import de.iwes.util.resource.ResourceHelper.DeviceInfo;
import de.iwes.widgets.html.selectiontree.LinkingOption;

/**
 * 
 * @author dnestle
 *
 * @param <R> org.ogema...resource or JAXB-resource
 * @param <T>
 */
public abstract class GaRoMultiEvaluationInstance<T extends GaRoMultiResult> extends AbstractMultiEvaluationInstance<T, GaRoSelectionItem> {
	public static Path evalOutputPath = null;
	
	private final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources
	public static final float MINIMUM_QUALITY_REQUIRED = 0.5f;
	public static final long GAP_THRESHOLD = 30*60000;

	private final String id;
	protected final GaRoDataTypeI[] inputTypesFromRoom;
	protected final GaRoDataTypeI[] inputTypesFromGw;

	//protected final List<String> gwIds;
	
	protected void startGwLevel(List<GaRoSelectionItem> levelItems, T result) {};
	protected abstract List<GaRoSelectionItem> startRoomLevel(List<GaRoSelectionItem> levelItems, T result, String gw);
	//protected abstract void startTSLevel(List<GaRoSelectionItem> levelItems, T result, R room);
	protected abstract void startTSLevel(List<GaRoSelectionItem> levelItems, T result, GaRoSelectionItem roomItem);
	protected void finishGwLevel(T result) {};
	protected void finishRoomLevel(T result) {};
	protected void finishTSLevel(T result) {};
	protected abstract void processInputType(int inputIdx, List<TimeSeriesData> tsList, GaRoDataTypeI dt, T result);
	protected abstract void performRoomEvaluation(List<TimeSeriesData>[] inputTimeSeries, T result);
	protected abstract GaRoMultiResult initNewResultGaRo(long start, long end, Collection<ConfigurationInstance> configurations);
	
	
	/*protected abstract Integer getRoomType(GaRoSelectionItem roomItem);
	protected abstract String getName(GaRoSelectionItem roomItem);
	protected abstract String getPath(GaRoSelectionItem roomItem);*/
	
	protected Integer getRoomType(GaRoSelectionItem roomItem) {
		return roomItem.getRoomType();
	}

	protected String getName(GaRoSelectionItem roomItem) {
		return roomItem.getRoomName();
	}

	protected String getPath(GaRoSelectionItem roomItem) {
		return roomItem.getPath();
	}
	
	public GaRoMultiEvaluationInstance(List<MultiEvaluationInputGeneric> input,
			Collection<ConfigurationInstance> configurations,
			final GaRoEvalProvider<T> dataProviderAccess,
			TemporalUnit resultStepSize, GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw) {
		super(input, configurations, resultStepSize, dataProviderAccess.id(), dataProviderAccess);
		this.id = "EnergyEvaluation_" + idcounter.getAndIncrement();
		this.inputTypesFromGw = inputTypesFromGw;
		this.inputTypesFromRoom = inputTypesFromRoom;
		
        //DateTime evaluationStartTime = DateTime.now();
        if(evalOutputPath == null) try {
			evalOutputPath = Files.createDirectories(Paths.get("evaluationresults"));
		} catch (IOException e) {
			e.printStackTrace();
		}
                
		//gwIds = dataProviderAccess.getGatewayParser().getGatewayIds();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T initNewResult(long start, long end, Collection<ConfigurationInstance> configurations) {
		T result = (T) initNewResultGaRo(start, end, configurations);
		
		if(result.overallResults != null && result.overallResults instanceof GaRoStdOverallResults) {
			GaRoStdOverallResults overallResults = (GaRoStdOverallResults)result.overallResults;
			overallResults.missingSources = new ArrayList<>();
			overallResults.countRoomsByType = new HashMap<>();
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AbstractSuperMultiResult<T> initSuperResult(List<MultiEvaluationInputGeneric> inputData, long startTime, Collection<ConfigurationInstance> configurations) {
		return (AbstractSuperMultiResult<T>) new GaRoSuperEvalResult<T>((List)inputData, startTime, configurations);		
	}
	
	@Override
	public String id() {
		return id;
	}
	
	@Override
	public List<GaRoSelectionItem> startInputLevel(List<GaRoSelectionItem> levelItems,
			List<GaRoSelectionItem> dependecyTreeSelection, int level, T result) {
		switch(level) {
		case GaRoMultiEvalDataProvider.GW_LEVEL:
			if(result.overallResults != null)
				result.overallResults.gwCount = levelItems.size();
			String evalId;
			if(this.provider instanceof GenericGaRoMultiProvider) {
				//evalId = this.provider.id();
				evalId = ((GenericGaRoMultiProvider<?>)(provider)).getSingleEvalId();
			} else {
				evalId = this.provider.id();
			}
			System.out.println("Starting "+evalId+" with "+levelItems.size()+" gateways.");
			result.roomEvals = new ArrayList<>(); //new HashMap<>();
			break;
		case GaRoMultiEvalDataProvider.ROOM_LEVEL:
			//we start into a single gateway here
			
			result.gwId = dependecyTreeSelection.get(0).id();

			//check if we have a single-evaluation-per-gateway-with-external-input
			if((!result.getInputData().isEmpty()) && (result.getInputData().get(0) instanceof GaRoMultiEvaluationInput)) {
				GaRoMultiEvaluationInput inp = (GaRoMultiEvaluationInput) result.getInputData().get(0);
				if(inp.terminalDataType.label(null).equals(GaRoDataType.OncePerGateway.label(null))) {
					String room = GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID;
					result.roomData = result.getNewRoomEval(); //new RoomData();
					result.roomData.id = room;
					result.roomData.gwId = result.gwId;
					final Integer roomType;
					roomType = -1;
					result.roomData.roomType = roomType; //((IntegerResource)typeRes).getValue();
					result.roomEvals.add(result.roomData); //put(room.getPath(), result.roomData);
					return Arrays.asList(new GaRoSelectionItem[] {null});
					//levelItems = Arrays.asList(new GaRoSelectionItem[] {AbstractMultiEvaluationInstance.EXECUTE_NOW_ITEM});
				}
			}
			
			levelItems = startRoomLevel(levelItems, result, result.gwId);
			
			int ll = Integer.getInteger("org.ogema.multieval.loglevel", 10);
			if(ll >= 6) System.out.println("Starting Gw:"+result.gwId+" with "+ levelItems.size() +" rooms from "+TimeUtils.getDateAndTimeString(result.startTime)+
					" to "+TimeUtils.getDateAndTimeString(result.endTime));
			if(ll >= 6) System.out.println("Starting Gw:"+result.gwId+" from "+new Date(result.startTime)+
					" to "+new Date(result.endTime));
			break;
		case GaRoMultiEvalDataProvider.TS_LEVEL:
			try {
				GaRoSelectionItem roomFullData = dependecyTreeSelection.get(1);
				//R room = getResource(roomFullData);
				String room = roomFullData.getRoomName();
				result.roomData = result.getNewRoomEval(); //new RoomData();
				if(room == null)
					result.roomData.id = roomFullData.id();
				else
					result.roomData.id = roomFullData.getPath(); //getPath(room);
				result.roomData.gwId = result.gwId;
				//Resource typeRes = room.get("type");
				final Integer roomType;
				if(room == null)
					roomType = -1;
				else
					roomType = roomFullData.getRoomType(); //getRoomType(room);
				//if(typeRes instanceof IntegerResource) {
				if(roomType != null) {
					result.roomData.roomType = roomType; //((IntegerResource)typeRes).getValue();
					if(result.overallResults != null && result.overallResults instanceof GaRoStdOverallResults) {
						GaRoStdOverallResults overallResults = (GaRoStdOverallResults)result.overallResults;
						Integer roomCount = overallResults.countRoomsByType.get(result.roomData.roomType);
						if(roomCount == null)
							overallResults.countRoomsByType.put(result.roomData.roomType, 1);
						else
							overallResults.countRoomsByType.put(result.roomData.roomType, roomCount+1);
					}
				}
				result.roomEvals.add(result.roomData); //put(room.getPath(), result.roomData);

				startTSLevel(levelItems, result, roomFullData);
				
				ll = Integer.getInteger("org.ogema.multieval.loglevel", 10);
				if(ll >= 10) System.out.println("Init room "+result.roomData.id+" type:"+result.roomData.roomType);
			} catch(Exception e) {
				e.printStackTrace();
			}
			break;
		}
		return levelItems;
	}

	@Override
	public void finishInputLevel(int level, T result) {
		if(level == GaRoMultiEvalDataProvider.TS_LEVEL) {
			finishTSLevel(result);
		} else if(level == GaRoMultiEvalDataProvider.ROOM_LEVEL) {
			finishRoomLevel(result);
		} else if(level == GaRoMultiEvalDataProvider.GW_LEVEL) {
			finishGwLevel(result);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void evaluateDataSet(List<GaRoSelectionItem> keys, TimeSeriesInputForSingleRequiredInputIdx[] timeSeries, T result) {
		if(timeSeries == null) {
			performRoomEvaluation(null, result);
			return;
		}
		int inputIdx = 0;
		List<TimeSeriesData>[] inputTimeSeries = new List[timeSeries.length];
		List<DeviceInfo>[] deviceInfo = new List[timeSeries.length];
		
		for(TimeSeriesInputForSingleRequiredInputIdx extInp: timeSeries) {
			List<TimeSeriesData> tsList = extInp.tsList;
			if(result.overallResults != null && result.overallResults instanceof GaRoStdOverallResults) {
				GaRoStdOverallResults overallResults = (GaRoStdOverallResults)result.overallResults;
				overallResults.countTimeseries +=tsList.size();
			}
			if(inputTypesFromRoom != null) {
				inputTimeSeries[inputIdx] = tsList;
				//TODO: For now we only add deviceInfo if the provider itself and all previous providers support
				//DeviceInfo. There should be a mechanism to skip providers without info
				List<DeviceInfo> devList = null;
				for(EvaluationInput ei: extInp.eiList) {
					if(ei instanceof EvaluationInputImplGaRo) {
						EvaluationInputImplGaRo eiGaRo = (EvaluationInputImplGaRo)ei;
						if(devList == null) devList = new ArrayList<>();
						devList.addAll(eiGaRo.getDeviceInfo());
					} else {
						break;
					}
				}
				deviceInfo[inputIdx] = devList;
			}
			processInputType(inputIdx, tsList, inputTypesFromRoom[inputIdx], result);
			
			inputIdx++;
		}
		
		if(result.overallResults != null && result.overallResults instanceof GaRoStdOverallResults) {
			GaRoStdOverallResults overallResults = (GaRoStdOverallResults)result.overallResults;
			overallResults.countRooms++;
			for(int i=0; i<timeSeries.length; i++) {
				if(!inputTimeSeries[i].isEmpty()) {
					overallResults.countRoomsWithData++;
					break;
				}
			}
		}
		for(int i=0; i<timeSeries.length; i++) {
			if(inputTimeSeries[i].isEmpty() && (inputTypesFromRoom[i] != GaRoDataType.PreEvaluated)
					&& (inputTypesFromRoom[i].primaryEvalProvider() == null)) {
				if(inputTypesFromRoom[i] instanceof GaRoDataTypeParam) {
					GaRoDataTypeParam inputType = (GaRoDataTypeParam)inputTypesFromRoom[i];
					if(!inputType.isRequired()) continue;
				}
				int ll = Integer.getInteger("org.ogema.multieval.loglevel", 10);
				if(ll >= 8) LoggerFactory.getLogger(GaRoEvalProvider.class).info(inputTypesFromRoom[i].toString()+" not found in room {}, skipping room base evaluation", result.roomData.id);
				return;
			}
			if(inputTypesFromRoom[i] instanceof GaRoDataTypeParam) {
				GaRoDataTypeParam inputType = (GaRoDataTypeParam)inputTypesFromRoom[i];
				if(i==0) inputType.inputInfo = new ArrayList<>();
				inputType.inputInfo = new ArrayList<>();
				List<DeviceInfo> devInfo = deviceInfo[i];
				if(devInfo != null) {
					inputType.deviceInfo = new ArrayList<>();
				}
				int idx = 0;
				for(TimeSeriesData tsh: inputTimeSeries[i]) {
					inputType.inputInfo.add((TimeSeriesDataImpl) tsh);
					if(devInfo != null) {
						DeviceInfo di =devInfo.get(idx);
						inputType.deviceInfo.add(di);
						idx++;
					}
				}
			}
		}
		if(result.overallResults != null && result.overallResults instanceof GaRoStdOverallResults) {
			GaRoStdOverallResults overallResults = (GaRoStdOverallResults)result.overallResults;
			overallResults.countRoomsWithAllDeviceData++;
		}		
		performRoomEvaluation(inputTimeSeries, result);
	}
	
	@Override
	protected LinkingOption[] getLinkingOptions(MultiEvaluationInputGeneric governingInput) {
		return governingInput.dataProvider().get(0).selectionOptions();
	}
}
