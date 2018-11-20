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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.model.locations.Location;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;

/** Selection item for {@link GaRoEvalDataProviderGateway}
 * 
 */
public class GaRoSelectionItemResource extends GaRoSelectionItem {
	//only relevant for level GW_LEVEL
	private String gwId;
	public Resource resource;
	
	public GaRoSelectionItemResource(String gwId) {
		super(GaRoMultiEvalDataProvider.GW_LEVEL, gwId);
		//this.appMan = appMan;
		this.gwId = gwId;
	}
	public GaRoSelectionItemResource(String name, Room room, GaRoSelectionItemResource superSelectionItem) {
		super(GaRoMultiEvalDataProvider.ROOM_LEVEL, name);
		this.gwSelectionItem = superSelectionItem;
		//this.gwId = superSelectionItem.gwId;
		//this.roomId = room.getKey();
		this.resource = room;
	}
	public GaRoSelectionItemResource(String name, SingleValueResource singleValue, GaRoSelectionItemResource superSelectionItem) {
		super(GaRoMultiEvalDataProvider.TS_LEVEL, name);
		this.gwId = superSelectionItem.gwId;
		this.gwSelectionItem = superSelectionItem.gwSelectionItem;
		this.roomSelectionItem = superSelectionItem;
		//this.tsId = tsId;
		this.resource = singleValue;
		//this.appMan = appMan;
	}
	
	public static Set<PhysicalElement> getDevicesByRoom(Room room) {
		List<Location> locations = room.getReferencingResources(Location.class);
		Set<PhysicalElement> devices = new HashSet<>();
		for(Location loc: locations) {
			Resource p = loc.getParent();
			if((p != null)&&(p instanceof PhysicalElement)) {
				devices.add((PhysicalElement) p);
			}
		}
		return devices;
	}

	@Override
	protected List<String> getDevicePaths(GaRoSelectionItem roomSelItem) {
		Set<PhysicalElement> devices = getDevicesByRoom((Room) ((GaRoSelectionItemResource)roomSelItem).resource);
		List<String> result = new ArrayList<>();
		for(Resource dev: devices) result.add(dev.getPath());
		return result;
	}

	@Override
	public TimeSeriesData getTimeSeriesData() {
		if(level == GaRoMultiEvalDataProvider.TS_LEVEL) {
			//RecordedDataStorage recData = getLogRecorder().getRecordedDataStorage(tsId);
			RecordedData recData;
			if(resource instanceof FloatResource)
				recData = ((FloatResource) resource).getHistoricalData();
			else if(resource instanceof IntegerResource)
				recData = ((IntegerResource) resource).getHistoricalData();
			else if(resource instanceof TimeResource)
				recData = ((TimeResource) resource).getHistoricalData();
			else
				throw new IllegalStateException("only Float, Int and Time resources are supported in getTimeSeriesData!");
			return new TimeSeriesDataImpl(recData, id,
					id, InterpolationMode.STEPS);
		}
		return null;
	}
	
	//@Override
	protected Resource getResource() {
		if(resource == null) {
			switch(level) {
			case GaRoMultiEvalDataProvider.GW_LEVEL:
				throw new IllegalArgumentException("No gateway resource available");
			case GaRoMultiEvalDataProvider.ROOM_LEVEL:
				return resource;
			case GaRoMultiEvalDataProvider.TS_LEVEL:
				throw new UnsupportedOperationException("Access to resources of data row parents not implemented yet, but should be done!");
			}
		}
		return resource;
	}
	
	@Override
	public Integer getRoomType() {
		if(resource == null) return null;
		IntegerResource type = ((Room)getResource()).type();
		if(type.isActive()) return type.getValue();
		return null;
	}

	@Override
	public String getRoomName() {
		if(resource == null) return null;
		return ResourceUtils.getHumanReadableName(getResource());
	}

	@Override
	public String getPath() {
		if(resource == null) return null;
		return getResource().getLocation();
	}

}
