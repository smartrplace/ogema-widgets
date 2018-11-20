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
import java.util.List;

import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;

public class GaRoStdPreEvaluationProvider<T extends GaRoMultiResult, S extends GaRoSuperEvalResult<T>>
		implements GaRoPreEvaluationProvider {
	private final S superEval;
	private final Class<S> superResultClass;
	public final String jsonInputFile;

	@SuppressWarnings("unchecked")
	public GaRoStdPreEvaluationProvider(S superEval) {
		this.superEval = superEval;
		superResultClass = (Class<S>) superEval.getClass();
		this.jsonInputFile = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GaRoStdPreEvaluationProvider(Class<? extends GaRoSuperEvalResult> superResultClass2,
			String jsonInputFile) {
		this.superResultClass = (Class<S>) superResultClass2;
		this.jsonInputFile = jsonInputFile;
		//this.resultClass = (Class<T>) resultClass2;
		//TODO: This will not work as the resultClass is missing
		this.superEval = MultiEvaluationUtils.importFromJSON(jsonInputFile, superResultClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public S getSuperEvalData() {
		return superEval;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getIntervalData(long startTime) {
		for(T ir: superEval.intervalResults) {
			if((ir.startTime <= startTime) && (ir.endTime > startTime))
				return ir;
		}
		return null;
	}

	@Override
	public RoomData getRoomData(long startTime, String gwId, String roomId) {
		T ir = getIntervalData(startTime);
		for(RoomData room: ir.roomEvals) {
			if(room.gwId.equals(gwId) && room.id.equals(roomId)) return room;
		}
		return null;
	}
	
	@Override
	public List<RoomData> getGatewayData(long startTime, String gwId) {
		T ir = getIntervalData(startTime);
		List<RoomData> result = new ArrayList<>();
		for(RoomData room: ir.roomEvals) {
			if(room.gwId.equals(gwId)) result.add(room);
		}
		return result;
	}
	
	@Override
	public RoomData getOverallGatewayRoom(long startTime, String gwId) {
		T ir = getIntervalData(startTime);
		for(RoomData room: ir.roomEvals) {
			if(room.gwId.equals(gwId) && room.id.equals(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID)) return room;
		}
		return null;
	}

	public Class<S> getSuperResultClass() {
		return superResultClass;
	}

	//public Class<T> getResultClass() {
	//	return resultClass;
	//}

}
