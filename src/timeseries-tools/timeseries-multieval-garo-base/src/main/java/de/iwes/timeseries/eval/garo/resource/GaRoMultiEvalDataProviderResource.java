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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.devices.connectiondevices.ElectricityConnectionBox;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.Sensor;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.widgets.html.selectiontree.SelectionItem;

/** Note that this class should inherit from {@link GaRoMultiEvalDataProvider}, but the seconds generic
 * parameter of HierarchyMultiEvalDataProviderGeneric shall be set explicitly here*/
public class GaRoMultiEvalDataProviderResource extends GaRoMultiEvalDataProvider<GaRoSelectionItemResource> {
	//HierarchyMultiEvalDataProviderGeneric<GaRoSelectionItemResource> {
	private final ApplicationManager appMan;
	
	private List<SelectionItem> gwSelectionItems = null;
	private List<SelectionItem> roomSelectionItems = null;
	/*if true the gateways available are fixed and usually less entries than
	 *the original size providing all gateways that are available in the input data 
	*/
	private boolean fixRoomSelectionItems = false;
	
	public GaRoMultiEvalDataProviderResource(ApplicationManager appMan) {
		super();
		//super(new String[]{GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID, GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID, "timeSeries"});
		gwSelectionItems = new ArrayList<>();
		gwSelectionItems.add(new GaRoSelectionItemResource(LOCAL_GATEWAY_ID));
		this.appMan = appMan; 
	}

	@Override
	protected List<SelectionItem> getOptions(int level, GaRoSelectionItemResource superItem) {
		switch(level) {
		case GaRoMultiEvalDataProvider.GW_LEVEL:
			return gwSelectionItems;
		case GaRoMultiEvalDataProvider.ROOM_LEVEL:
			if(fixRoomSelectionItems) return roomSelectionItems;
			List<Room> roomIds = appMan.getResourceAccess().getResources(Room.class);
			roomSelectionItems = new ArrayList<>();
			for(Room room: roomIds)
				roomSelectionItems.add(new GaRoSelectionItemResource(ResourceUtils.getHumanReadableName(room), room, superItem));
			roomSelectionItems.add(new GaRoSelectionItemResource(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID, (Room)null, superItem));
			return roomSelectionItems;
		case GaRoMultiEvalDataProvider.TS_LEVEL:
			//CloseableDataRecorder logData = superItem.getLogRecorder();
			List<SelectionItem> result = new ArrayList<>();
			if(superItem.resource == null) {
				//TODO: Also specifiy the types in GaRoEvalHelper or similar
				addResources(ElectricityConnectionBox.class, result, superItem);
				addResources(Thermostat.class, result, superItem);
				addResources(Sensor.class, result, superItem);
				//We would need to add the sema-info-Resource here
			} else {
				//add resource types that are overall
				addResources(ElectricityConnectionBox.class, result, superItem);

				//here only use ids that belong to the room
				Set<PhysicalElement> recIds = GaRoSelectionItemResource.getDevicesByRoom((Room) superItem.getResource());
				for(PhysicalElement devE: recIds) {
					for(SingleValueResource ts: getRecordedDataOfDevice(devE)) {
						result.add(new GaRoSelectionItemResource(ResourceUtils.getHumanReadableName(ts), ts, superItem));
					}
				}
			}
			return result;
		default:
			throw new IllegalArgumentException("unknown level");
		}
	}
	
	private void addResources(Class<? extends PhysicalElement> type, List<SelectionItem> result,
			GaRoSelectionItemResource superItem) {
		List<? extends PhysicalElement> recIds = appMan.getResourceAccess().getResources(type);
		for(PhysicalElement devE: recIds) {
			for(SingleValueResource ts: getRecordedDataOfDevice(devE)) {
				result.add(new GaRoSelectionItemResource(ResourceUtils.getHumanReadableName(ts), ts, superItem));
			}
		}
	}

	public static List<SingleValueResource> getRecordedDataOfDevice(PhysicalElement device) {
		throw new UnsupportedOperationException("not implemented yet!");
	}

	@Override
	public void setGatewaysOffered(List<SelectionItem> gwSelectionItemsToOffer) {
		roomSelectionItems = gwSelectionItemsToOffer;
		fixRoomSelectionItems = true;
	}

	@Override
	public boolean providesMultipleGateways() {
		return false;
	}

	@Override
	public List<String> getGatewayIds() {
		return Arrays.asList(LOCAL_GATEWAY_ID);
	}
}
