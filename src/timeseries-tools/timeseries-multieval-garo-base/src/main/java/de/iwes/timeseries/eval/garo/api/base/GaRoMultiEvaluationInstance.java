package de.iwes.timeseries.eval.garo.api.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.tools.resource.util.TimeUtils;
import org.slf4j.LoggerFactory;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.GaRoStdOverallResults;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;

/**
 * 
 * @author dnestle
 *
 * @param <R> org.ogema...resource or JAXB-resource
 * @param <T>
 */
public abstract class GaRoMultiEvaluationInstance<R, T extends GaRoMultiResult<R>> extends AbstractMultiEvaluationInstance<R, T, GaRoSelectionItem<R>> {
	public static Path evalOutputPath = null;
	
	private final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources
	public static final float MINIMUM_QUALITY_REQUIRED = 0.5f;
	public static final long GAP_THRESHOLD = 30*60000;

	private final String id;
	protected final GaRoDataType[] inputTypesFromRoom;
	protected final GaRoDataType[] inputTypesFromGw;

	//protected final List<String> gwIds;
	
	protected void startGwLevel(List<GaRoSelectionItem<R>> levelItems, T result) {};
	protected abstract List<GaRoSelectionItem<R>> startRoomLevel(List<GaRoSelectionItem<R>> levelItems, T result, String gw);
	protected abstract void startTSLevel(List<GaRoSelectionItem<R>> levelItems, T result, R room);
	protected void finishGwLevel(T result) {};
	protected void finishRoomLevel(T result) {};
	protected void finishTSLevel(T result) {};
	protected abstract void processInputType(int inputIdx, List<TimeSeriesData> tsList, GaRoDataType dt, T result);
	protected abstract void performRoomEvaluation(List<TimeSeriesData>[] inputTimeSeries, T result);
	protected abstract GaRoMultiResult<?> initNewResultGaRo(long start, long end, Collection<ConfigurationInstance> configurations);
	protected abstract Integer getRoomType(R room);
	protected abstract String getName(R room);
	protected abstract String getPath(R room);
	
	public GaRoMultiEvaluationInstance(List<MultiEvaluationInputGeneric<R>> input,
			Collection<ConfigurationInstance> configurations,
			final GaRoEvalProvider<R, T> dataProviderAccess,
			TemporalUnit resultStepSize, GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw) {
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
	
	@Override
	public AbstractSuperMultiResult<R, T> initSuperResult(List<MultiEvaluationInputGeneric<R>> inputData, long startTime, Collection<ConfigurationInstance> configurations) {
		return (AbstractSuperMultiResult<R, T>) new GaRoSuperEvalResult<R, T>(inputData, startTime, configurations);
	}
	
	@Override
	public String id() {
		return id;
	}
	
	@Override
	public List<GaRoSelectionItem<R>> startInputLevel(List<GaRoSelectionItem<R>> levelItems,
			List<GaRoSelectionItem<R>> dependecyTreeSelection, int level, T result) {
		switch(level) {
		case GaRoMultiEvalDataProvider.GW_LEVEL:
			if(result.overallResults != null)
				result.overallResults.gwCount = levelItems.size();
			System.out.println("Starting GaRoWinMultiEval with "+levelItems.size()+" gateways.");
			result.roomEvals = new ArrayList<>(); //new HashMap<>();
			break;
		case GaRoMultiEvalDataProvider.ROOM_LEVEL:
			//we start into a single gateway here
			result.gwId = dependecyTreeSelection.get(0).id();
			levelItems = startRoomLevel(levelItems, result, result.gwId);
			
			System.out.println("Starting Gw:"+result.gwId+" with "+ levelItems.size() +" rooms from "+TimeUtils.getDateAndTimeString(result.startTime)+
					" to "+TimeUtils.getDateAndTimeString(result.endTime));
			System.out.println("Starting Gw:"+result.gwId+" from "+new Date(result.startTime)+
					" to "+new Date(result.endTime));
			break;
		case GaRoMultiEvalDataProvider.TS_LEVEL:
			try {
				GaRoSelectionItem<R> roomFullData = dependecyTreeSelection.get(1);
				R room = getResource(roomFullData);
				result.roomData = new RoomData();
				if(room == null)
					result.roomData.id = roomFullData.id();
				else
					result.roomData.id = getPath(room);
				result.roomData.gwId = result.gwId;
				//Resource typeRes = room.get("type");
				final Integer roomType;
				if(room == null)
					roomType = -1;
				else
					roomType = getRoomType(room);
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

				startTSLevel(levelItems, result, room);
				
				System.out.println("Init room "+result.roomData.id+" type:"+result.roomData.roomType);
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

	@Override
	public void evaluateDataSet(List<GaRoSelectionItem<R>> keys, List<TimeSeriesData>[] timeSeries, T result) {
		int inputIdx = 0;
		@SuppressWarnings("unchecked")
		List<TimeSeriesData>[] inputTimeSeries = new List[timeSeries.length];
		
		for(List<TimeSeriesData> tsList: timeSeries) {
			if(result.overallResults != null && result.overallResults instanceof GaRoStdOverallResults) {
				GaRoStdOverallResults overallResults = (GaRoStdOverallResults)result.overallResults;
				overallResults.countTimeseries +=tsList.size();
			}
			if(inputTypesFromRoom != null) {
				inputTimeSeries[inputIdx] = tsList;
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
			if(inputTimeSeries[i].isEmpty()) {
				LoggerFactory.getLogger(GaRoEvalProvider.class).info(inputTypesFromRoom[i].toString()+" not found in room {}, skipping room base evaluation", result.roomData.id);
				return;
			}
		}
		if(result.overallResults != null && result.overallResults instanceof GaRoStdOverallResults) {
			GaRoStdOverallResults overallResults = (GaRoStdOverallResults)result.overallResults;
			overallResults.countRoomsWithAllDeviceData++;
		}		
		performRoomEvaluation(inputTimeSeries, result);
	}
}
