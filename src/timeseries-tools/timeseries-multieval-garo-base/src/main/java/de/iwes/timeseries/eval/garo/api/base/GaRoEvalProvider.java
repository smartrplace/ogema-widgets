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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.util.GenericLinkingOptionType;
import de.iwes.widgets.html.selectiontree.LinkingOptionType;

/**
 * Calculate basic field test evaluations
 */
public abstract class GaRoEvalProvider<T extends GaRoMultiResult> extends AbstractMultiEvaluationProvider<T> {
	protected final GaRoDataTypeI[] inputTypesFromRoom;
	protected final GaRoDataTypeI[] inputTypesFromGw;
	private List<DataProviderType> inputDataTypes;
	
	@Override
	public List<Configuration<?>> getConfigurations() {
		return Collections.emptyList();
	}

	//TODO: Use options from server-timeseries-source?
	public static LinkingOptionType gw = new GenericLinkingOptionType(GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID, "Select gateways", null);
	public static LinkingOptionType room = new GenericLinkingOptionType(GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID, "Select rooms", new LinkingOptionType[]{gw});
	
	public static class InitData {
		GaRoDataTypeI[] inputTypesFromRoom;
		GaRoDataTypeI[] inputTypesFromGw;
		public InitData(GaRoDataTypeI[] gaRoDataTypeIs, GaRoDataTypeI[] inputTypesFromGw) {
			this.inputTypesFromRoom = gaRoDataTypeIs;
			this.inputTypesFromGw = inputTypesFromGw;
		}
	}
	@Override
	protected void preInit(Object initDataRaw) {
		InitData in = (InitData)initDataRaw;
		int len = 0;
		if(in.inputTypesFromRoom != null) len += in.inputTypesFromRoom.length;
		if(in.inputTypesFromGw != null) len += in.inputTypesFromGw.length;
		DataProviderType[] arr = new DataProviderType[len];
		int idx = 0;
		
		if(in.inputTypesFromRoom != null) for(final GaRoDataTypeI type: in.inputTypesFromRoom) {
			DataProviderType dataProvider = new DataProviderType() {
				@Override
				public LinkingOptionType[] selectionOptions() {
					LinkingOptionType lot = new GenericLinkingOptionType(type.id(), "Select "+type.label(null),
							new LinkingOptionType[]{room});
					return new LinkingOptionType[]{gw, room, lot};
				}
				
			};
			arr[idx] = dataProvider;
			idx++;
		}
		if(in.inputTypesFromGw != null) for(final GaRoDataTypeI type: in.inputTypesFromGw) {
			DataProviderType dataProvider = new DataProviderType() {
				@Override
				public LinkingOptionType[] selectionOptions() {
					LinkingOptionType lot = new GenericLinkingOptionType(type.id(), "Select "+type.label(null),
							new LinkingOptionType[]{gw});
					return new LinkingOptionType[]{gw, lot};
				}
				
			};
			arr[idx] = dataProvider;
			idx++;
		}
		inputDataTypes = Collections.<DataProviderType> unmodifiableList(Arrays.asList(arr));
	}
	public GaRoEvalProvider(GaRoDataTypeI[] gaRoDataTypeIs, GaRoDataType[] inputTypesFromGw) {
		super(new InitData(gaRoDataTypeIs, inputTypesFromGw));
		this.inputTypesFromGw = inputTypesFromGw;
		this.inputTypesFromRoom = gaRoDataTypeIs;
	}	
	
	@Override
	public List<DataProviderType> inputDataTypes() {
		return inputDataTypes;
	}
	
	public GaRoDataTypeI[] getInputTypesFromRoom() {
		return inputTypesFromRoom;
	}

	public GaRoDataTypeI[] getInputTypesFromGw() {
		return inputTypesFromGw;
	}
}
