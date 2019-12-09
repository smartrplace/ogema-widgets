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

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.extended.util.HierarchyMultiEvalDataProviderGeneric;
import de.iwes.timeseries.eval.garo.resource.GaRoMultiEvalDataProviderResource;
import de.iwes.widgets.html.selectiontree.SelectionItem;

/** Extend and implement this abstract class for a DataProvider for the GaRo system*/
public abstract class GaRoMultiEvalDataProvider<T extends GaRoSelectionItem> extends HierarchyMultiEvalDataProviderGeneric<T> {
	public static final String BUILDING_OVERALL_ROOM_ID = "##Building";
	public static final String LOCAL_GATEWAY_ID = "myGateway";

	public static final int GW_LEVEL = 0;
	public static final int ROOM_LEVEL = 1;
	public static final int TS_LEVEL = 2;
	public static final String GW_LINKINGOPTION_ID = "gateways";
	public static final String ROOM_LINKINGOPTION_ID = "rooms";
	
	protected List<SelectionItem> gwSelectionItems = null;
	
	public GaRoMultiEvalDataProvider() {
		super(new String[]{GW_LINKINGOPTION_ID, ROOM_LINKINGOPTION_ID, "timeSeries"});
//		this.gatewayParser = gatewayParser; 
	}

	@Override
	public abstract List<SelectionItem> getOptions(int level, T superItem);
	
	/** @return If true the provider is able to provide information from multiple gateways, otherwise the
	 * provider is limited to a single gateway, usually the gateway on which it operates (e.g. from
	 * the local gateway resource structure like {@link GaRoMultiEvalDataProviderResource}).
	 */
	public abstract boolean providesMultipleGateways();
	
	//TODO: With this new method the signature of method getSelectionItemsForGws may be changed
	/** Get all gateway IDs supported by the provider*/
	public abstract List<String> getGatewayIds();
	
	/** Get device information for a time series*/
	//public abstract DeviceInfo getDeviceInformation();

	@Override
	/** Provide time series together with device information here*/
	public abstract EvaluationInputImplGaRo getData(List<SelectionItem> items);
	/** Override this to provide a special input that usually overrides
	 * {@link GaRoMultiEvaluationInput#useDataProviderItemTerminal(GaRoSelectionItem)}
	 */
	public GaRoMultiEvaluationInput provideMultiEvaluationInput(DataProviderType type, DataProvider<?> dataProvider,
			GaRoDataTypeI terminalDataType, List<String> topLevelIdsToEvaluate, String topLevelOptionId) {
		return null;
	}
	
	public List<GaRoSelectionItem> getSelectionItemsForGws(List<String> gwIds) {
		List<GaRoSelectionItem> result = new ArrayList<>();
		for(SelectionItem item: getOptions(GW_LEVEL, null)) {
			if(gwIds.contains(item.id())) result.add((GaRoSelectionItem) item);
		}
		return result ;
	}
	@SuppressWarnings("unchecked")
	public List<GaRoSelectionItem> getSelectionItemsForRooms(GaRoSelectionItem gwItem, List<String> roomIds) {
		List<GaRoSelectionItem> result = new ArrayList<>();
		for(SelectionItem item: getOptions(ROOM_LEVEL, (T) gwItem)) {
			if(roomIds.contains(item.id())) result.add((GaRoSelectionItem) item);
		}
		return result ;
	}
}
