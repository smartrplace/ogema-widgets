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
package de.iwes.timeseries.eval.garo.resource;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;

@SuppressWarnings("unchecked")
@Deprecated //base class should be sufficient
public class GenericGaRoMultiEvaluationResource<P extends GaRoSingleEvalProvider> extends GenericGaRoMultiEvaluation<P> {
	
	public GenericGaRoMultiEvaluationResource(List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		super(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw,
				singleProvider, doBasicEval, resultsRequested);
	}

	@Override
	protected List<GaRoSelectionItem> startRoomLevel(List<GaRoSelectionItem> levelItems, GaRoMultiResult result, String gw) {
		int[] roomTypeList = roomEval.getRoomTypes();
		if(roomTypeList == null) return levelItems;
		List<GaRoSelectionItem> retVal = new ArrayList<>();
		for(GaRoSelectionItem lvlIt: levelItems) {
			//Room room = (Room) lvlIt.getResource();
			//IntegerResource typeRes = room.type();
			int roomType = lvlIt.getRoomType(); //typeRes.getValue();
			boolean found = false;
			for(int rt: roomTypeList) {
				if(roomType < 0) {
					if(rt < -1) { //-1 means overall-room
						found = true;
						break;
					}
				}
				else if(rt == roomType) {
					found = true;
					break;
				}
			}
			if(found) retVal.add(lvlIt);
		}
		return retVal;
		//result.dpNumGw = 0;
		//result.resultsGw = new HashMap<>();
	}

	/*@Override
	protected Integer getRoomType(Resource room) {
		IntegerResource type = ((Room)room).type();
		if(type.isActive()) return type.getValue();
		return null;
	}

	@Override
	protected String getName(Resource room) {
		return ResourceUtils.getHumanReadableName(room);
	}

	@Override
	protected String getPath(Resource room) {
		return room.getLocation();
	}*/
}
