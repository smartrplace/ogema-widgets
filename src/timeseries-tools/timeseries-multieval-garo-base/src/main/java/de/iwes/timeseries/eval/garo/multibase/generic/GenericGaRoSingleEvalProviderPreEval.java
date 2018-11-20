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
package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.extended.MultiResult;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.api.helper.EfficientTimeSeriesArray;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;
import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting;

/**
 * Generic GaRoSingleEvalProvider that allows to defined eval providers without
 * separate EvaluationInstance classes.
 */
public abstract class GenericGaRoSingleEvalProviderPreEval extends GenericGaRoSingleEvalProvider implements GaRoSingleEvalProviderPreEvalRequesting {
	protected final List<PreEvaluationRequested> standardRequests = new ArrayList<>();
	
	public GenericGaRoSingleEvalProviderPreEval(String id, String label, String description) {
        super(id, label, description);
		for(GaRoDataTypeI dt: getGaRoInputTypes()) {
			if(dt.primaryEvalProvider() == null) continue;
			standardRequests.add(new PreEvaluationRequested(dt.primaryEvalProvider()));
		}
    }
    	
	//Overwrite in standard implementation
	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return standardRequests;
	}
	@Override
	public List<EvaluationInputImpl> timeSeriesToInject() {
		List<EvaluationInputImpl> timeSeriesToInjectStd = new ArrayList<>();
		List<String> providersAdded = new ArrayList<>();
		for(GaRoDataTypeI dt: getGaRoInputTypes()) {
			if(dt.primaryEvalProvider() == null) continue;
			if(!providersAdded.contains(dt.primaryEvalProvider())) {
				standardRequests.add(new PreEvaluationRequested(dt.primaryEvalProvider()));
				providersAdded.add(dt.primaryEvalProvider());
			}
			List<EvaluationInputImpl> preData;
			String[] ida = new String[] {dt.id()};
			switch(dt.getLevel()) {
			case ROOM:
				preData = getRoomTimeSeriesInput(dt.primaryEvalProvider(), ida);
				break;
			case GATEWAY:
				preData = getGatewayTimeSeriesInput(dt.primaryEvalProvider(), ida);
				break;
			case OVERALL:
				preData = getOverallTimeSeriesInput(dt.primaryEvalProvider(), ida);
				throw new IllegalArgumentException("Overall output types cannot be declated directly in getGaRoInputTypes!");
				//preData = getOverallTimeSeriesInput(dt.primaryEvalProvider(), dt.id());
			default:
				throw new IllegalStateException("Unknown level:"+dt.getLevel());
			}
			if(preData == null) continue;
			if(preData.size() != 1) throw new IllegalStateException("Exptected a single result for Pre-Eval "+dt.id()+", found "+preData.size());
			timeSeriesToInjectStd.add(preData.get(0));
		}
		return timeSeriesToInjectStd;
	}
	
	private Map<String, GaRoPreEvaluationProvider> providersAvailable = new HashMap<>();
	@Override
	public void preEvaluationProviderAvailable(int requestIdx, String providerId, GaRoPreEvaluationProvider provider) {
		providersAvailable.put(preEvaluationsRequested().get(requestIdx).getSourceProvider(), provider);
	}

	public GaRoPreEvaluationProvider getPreEvalProvider(String className) {
		return providersAvailable.get(className);
		/*for(GaRoPreEvaluationProvider p: providersAvailable) {
			if(p.getClass().getSimpleName().equals(className)) return p;
		}
		return null;*/
	}
	
	protected List<EvaluationInputImpl> getRoomTimeSeriesInput(String preEvaluationProviderId,
			String[] resultIdsToInject) {
		return getRoomTimeSeriesInput(preEvaluationProviderId, resultIdsToInject,
				currentStartTime, currentGwId, currentRoomId);
	}
	/** Get information for time series to inject from room level
	 * @return a list that has size equal to the length of resultIdsToInject with a single time
	 * 		series as entry each. In pre-evaluation several time series of the same input type are
	 * 		not supported as such similar time series are usually aggregated in the previous EvaluationProvider*/
	protected List<EvaluationInputImpl> getRoomTimeSeriesInput(String preEvaluationProviderId,
		String[] resultIdsToInject, long startTime, String gwId, String roomId) {
		GaRoPreEvaluationProvider prov = getPreEvalProvider(preEvaluationProviderId);
		if(prov == null) {
			throw new IllegalStateException("Could not find provider with id "+preEvaluationProviderId);
		}
		RoomData roomData = prov.getRoomData(startTime, gwId, roomId);
		List<EvaluationInputImpl> result = new ArrayList<>();
		for(String id: resultIdsToInject) {
			if(roomData == null || roomData.timeSeriesResults == null)
				return null;
			EfficientTimeSeriesArray eff = roomData.timeSeriesResults.get(id);
			List<TimeSeriesData> dataList = new ArrayList<>();
			if(eff != null) {
				TimeSeriesData tsd = new TimeSeriesDataImpl(eff.toFloatTimeSeries(), preEvaluationProviderId + "_" + id, id, InterpolationMode.STEPS);
				dataList.add(tsd);
				EvaluationInputImpl evalIn = new EvaluationInputImpl(dataList);
				result.add(evalIn);
			}
		}
		return result;
		//return Arrays.asList(new EvaluationInputImpl[] {evalIn});
	}
	
	protected List<EvaluationInputImpl> getGatewayTimeSeriesInput(String preEvaluationProviderId,
			String[] resultIdsToInject) {
		return getGatewayTimeSeriesInput(preEvaluationProviderId, resultIdsToInject, currentStartTime, currentGwId);
	}
	protected List<EvaluationInputImpl> getGatewayTimeSeriesInput(String preEvaluationProviderId,
			String[] resultIdsToInject, long startTime, String gwId) {
		GaRoPreEvaluationProvider prov = getPreEvalProvider(preEvaluationProviderId);			
		if(prov == null) {
			throw new IllegalStateException("Could not find provider with id "+preEvaluationProviderId);
		}
		RoomData roomData = prov.getOverallGatewayRoom(startTime, gwId);
		List<EvaluationInputImpl> result = new ArrayList<>();
		for(String id: resultIdsToInject) {
			if(roomData == null || roomData.timeSeriesResults == null) return null;
			EfficientTimeSeriesArray eff = roomData.timeSeriesResults.get(id);
			List<TimeSeriesData> dataList = new ArrayList<>();
			if(eff != null) {
				TimeSeriesData tsd = new TimeSeriesDataImpl(eff.toFloatTimeSeries(), preEvaluationProviderId + "_" + id, id, InterpolationMode.STEPS);
				dataList.add(tsd);
				EvaluationInputImpl evalIn = new EvaluationInputImpl(dataList);
				result.add(evalIn);
			}
		}
		return result;
	}
	
	public static interface OverallTimeSeriesProvider {
		ReadOnlyTimeSeries getTimeSeries(MultiResult result);
	}
	protected EvaluationInputImpl getOverallTimeSeriesInput(String preEvaluationProviderId,
			OverallTimeSeriesProvider tsProvider) {
		return getOverallTimeSeriesInput(preEvaluationProviderId, tsProvider, currentStartTime);
	}
	protected EvaluationInputImpl getOverallTimeSeriesInput(String preEvaluationProviderId,
			OverallTimeSeriesProvider tsProvider, long startTime) {
		GaRoPreEvaluationProvider prov = getPreEvalProvider(preEvaluationProviderId);			
		if(prov == null) {
			throw new IllegalStateException("Could not find provider with id "+preEvaluationProviderId);
		}
		MultiResult multiData = prov.getIntervalData(startTime);
		if(multiData == null) return null;
		ReadOnlyTimeSeries eff = tsProvider.getTimeSeries(multiData);
		List<TimeSeriesData> dataList = new ArrayList<>();
		if(eff != null) {
			TimeSeriesData tsd = new TimeSeriesDataImpl(eff, preEvaluationProviderId + "_" + id, id, InterpolationMode.STEPS);
			dataList.add(tsd);
			EvaluationInputImpl evalIn = new EvaluationInputImpl(dataList);
			return evalIn;
		}
		return null;
	}
	
	protected Float getPreEvalRoomValue(String preEvaluationProviderId, String resultId) {
		return getPreEvalRoomValue(preEvaluationProviderId, resultId,
				currentStartTime, currentGwId, currentRoomId);
	}
	protected Float getPreEvalRoomValue(String preEvaluationProviderId, String resultId,
			long startTime, String gwId, String roomId) {
		GaRoPreEvaluationProvider prov = getPreEvalProvider(preEvaluationProviderId);			
		RoomData roomData = prov.getRoomData(startTime, gwId, roomId);
		if(roomData == null || roomData.evalResults == null) return null;
		String val = roomData.evalResults.get(resultId);
		if(val == null) return null;
		return Float.parseFloat(val);
	}
	/** Get room value either from own previous evaluation step or from pre-evaluation. This is mainly relevant
	 * for recurrent evaluation providers that use their own previous results as input
	 * @param preEvaluationProviderId
	 * @param resultId
	 * @param startTime
	 * @param gwId
	 * @param roomId
	 * @param mySuperResult if null works like {@link #getPreEvalRoomValue(String, String, long, String, String)}
	 * @return
	 */
	protected Float getPreEvalRoomValue(String preEvaluationProviderId, String resultId,
			long startTime, String gwId, String roomId, AbstractSuperMultiResult<GaRoMultiResult> mySuperResult) {
		if(mySuperResult == null) return getPreEvalRoomValue(preEvaluationProviderId, resultId, startTime, gwId, roomId);
		RoomData roomData = getRoomData(startTime, gwId, roomId, mySuperResult);
		if(roomData == null || roomData.evalResults == null) return getPreEvalRoomValue(preEvaluationProviderId, resultId, startTime, gwId, roomId);
		String val = roomData.evalResults.get(resultId);
		if(val == null) return getPreEvalRoomValue(preEvaluationProviderId, resultId, startTime, gwId, roomId);
		return Float.parseFloat(val);
	}
	public static RoomData getRoomData(long startTime, String gwId, String roomId, AbstractSuperMultiResult<GaRoMultiResult> mySuperResult) {
		GaRoMultiResult ir = getIntervalData(startTime, mySuperResult);
		for(RoomData room: ir.roomEvals) {
			if(room.gwId.equals(gwId) && room.id.equals(roomId)) return room;
		}
		return null;
	}
	public static GaRoMultiResult getIntervalData(long startTime, AbstractSuperMultiResult<GaRoMultiResult> mySuperResult) {
		for(GaRoMultiResult ir: mySuperResult.intervalResults) {
			if((ir.startTime <= startTime) && (ir.endTime > startTime))
				return ir;
		}
		return null;
	}
	
	protected GaRoMultiResult getPreEvalMultiResult(String preEvaluationProviderId, String resultId) {
		return getPreEvalMultiResult(preEvaluationProviderId, resultId,
				currentStartTime, currentGwId, currentRoomId);
	}
	protected GaRoMultiResult getPreEvalMultiResult(String preEvaluationProviderId, String resultId,
			long startTime, String gwId, String roomId) {
		GaRoPreEvaluationProvider prov = getPreEvalProvider(preEvaluationProviderId);
		return prov.getIntervalData(startTime);
	}
	
	protected Float getPreEvalOverallValue(String preEvaluationProviderId, String resultId) {
		GaRoPreEvaluationProvider prov = getPreEvalProvider(preEvaluationProviderId);			
		AbstractSuperMultiResult<MultiResult> data1 = prov.getSuperEvalData();
		if(data1 == null) return null;
		if(!(data1 instanceof GaRoSuperEvalResult)) return null;
		GaRoSuperEvalResult<?> data = (GaRoSuperEvalResult<?>)data1;
		if(data.evalResults == null) return null;
		String val = data.evalResults.get(resultId);
		if(val == null) return null;
		return Float.parseFloat(val);		
	}
	
	protected List<EvaluationInputImpl> getOverallTimeSeriesInput(String preEvaluationProviderId,
		String[] resultIdsToInject) {
		GaRoPreEvaluationProvider prov = getPreEvalProvider(preEvaluationProviderId);
		if(prov == null) {
			throw new IllegalStateException("Could not find provider with id "+preEvaluationProviderId);
		}
		AbstractSuperMultiResult<MultiResult> data1 = prov.getSuperEvalData();
		if(data1 == null) return null;
		if(!(data1 instanceof GaRoSuperEvalResult)) return null;
		GaRoSuperEvalResult<?> data = (GaRoSuperEvalResult<?>)data1;
		if(data.timeSeriesResults == null) return null;
		List<EvaluationInputImpl> result = new ArrayList<>();
		for(String id: resultIdsToInject) {
			EfficientTimeSeriesArray eff = data.timeSeriesResults.get(id);
			List<TimeSeriesData> dataList = new ArrayList<>();
			if(eff != null) {
				TimeSeriesData tsd = new TimeSeriesDataImpl(eff.toFloatTimeSeries(), preEvaluationProviderId + "_" + id, id, InterpolationMode.STEPS);
				dataList.add(tsd);
				EvaluationInputImpl evalIn = new EvaluationInputImpl(dataList);
				result.add(evalIn);
			}
		}
		return result;
		//return Arrays.asList(new EvaluationInputImpl[] {evalIn});
	}

	/** Overwrite this if #executeSuperLevelOnly() is true*/
	protected void performSuperEval(AbstractSuperMultiResult<?> destination, List<AbstractSuperMultiResult<?>> preEvalSources) {}

	@Override
	public void performSuperEval(AbstractSuperMultiResult<?> destination) {
		List<AbstractSuperMultiResult<?>> sources = new ArrayList<>();
		for(PreEvaluationRequested pre: preEvaluationsRequested()) {
			GaRoPreEvaluationProvider prov = getPreEvalProvider(pre.getSourceProvider());
			if(prov == null) {
				throw new IllegalStateException("Could not find provider with id "+pre.getSourceProvider());
			}
			AbstractSuperMultiResult<MultiResult> data1 = prov.getSuperEvalData();
			sources.add(data1);
		}
		performSuperEval(destination, sources);
	}
}
