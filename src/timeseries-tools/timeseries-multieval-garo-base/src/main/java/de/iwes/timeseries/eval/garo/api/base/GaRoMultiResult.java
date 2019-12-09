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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.tools.resource.util.TimeUtils;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiResult;
import de.iwes.timeseries.eval.api.helper.EfficientTimeSeriesArray;
import de.iwes.timeseries.eval.garo.api.helper.base.SpecialGaRoEvalResult;
import de.iwes.util.resource.ResourceHelper.DeviceInfo;

/** Result for the evaluations of all rooms and gateways in the multi-evaluation for a single evaluation interval*/
public class GaRoMultiResult extends AbstractMultiResult {
	/**Overwrite this to provide your own instance of RoomData*/
	public RoomData getNewRoomEval() {
		return new RoomData();
	}
	
	public static class RoomData {
		public String id;
		public String gwId;
		
		//real data
		public int roomType = -1;
		
		public Map<String, String> evalResults;
		/** Time series results are not stored in evalResults. This map has to be initialized on first
		 * entry written to it*/
		public Map<String, EfficientTimeSeriesArray> timeSeriesResults;
		/*We expect a single result object for each input here. Note that this by standard only
		 * contains the SingleValueResults*/
		Map<ResultType, SingleEvaluationResult> evalResultObjects;
		public Map<ResultType, SingleEvaluationResult> evalResultObjects() {
			return evalResultObjects;
		}
		public void initEvalResultObjects() {
			evalResultObjects = new HashMap<>();
		}
		
		public long gapTime = 0;
	}
	
	public static class GaRoOverallResults {
		public int gwCount = 0;
		public int gwCountWithData = 0;
	}
	public static class GaRoStdOverallResults extends GaRoOverallResults {
		//internal data
		public Map<GaRoDataType, SpecialGaRoEvalResult> results = new HashMap<>();

		//results to export to JSON
		public int countRooms = 0;
		public int countRoomsWithData = 0;		
		public int countRoomsWithAllDeviceData = 0;		
		public int countTimeseries = 0;
		public Map<Integer, Integer> countRoomsByType;
		
		public List<String> missingSources = null;
	}

	/**Per-room results to export, init in application if used*/
	public List<RoomData> roomEvals = null;
	
	/**Overall results to export, init in application if used*/
	public GaRoOverallResults overallResults = null;

	/**Information on time series evaluated, e.g. used to determine CSV export*/
	public List<GaRoTimeSeriesId> timeSeriesEvaluated = new ArrayList<>();

	public GaRoMultiResult(List<MultiEvaluationInputGeneric> inputData, long start, long end, Collection<ConfigurationInstance> configurations) {
		super(inputData, start, end, configurations);
	}
	
	//Helpers, not for export.
	protected String gwId;
	/** Get gwId that is currently processed*/
	public String gwId() {
		return gwId;
	};
	//per room
	protected RoomData roomData;
	/** Get element of roomEvals that is currently processed
	 */
	public RoomData roomData() {
		return roomData;
	}
	
	@Override
	public String getSummary() {
		return "GaRoWinResult: start:"+TimeUtils.getDateAndTimeString(startTime)+
				" end:"+TimeUtils.getDateAndTimeString(endTime) + 
	        	" Found total " + ((overallResults!=null)?overallResults.gwCountWithData:"-")+ " of "+ ((overallResults!=null)?overallResults.gwCount:"-");
	}
}
