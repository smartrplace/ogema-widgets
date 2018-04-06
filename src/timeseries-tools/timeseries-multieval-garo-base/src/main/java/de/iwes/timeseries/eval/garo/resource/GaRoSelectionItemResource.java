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

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;

/** Selection item for {@link GaRoEvalDataProviderGateway}
 * 
 */
public class GaRoSelectionItemResource extends GaRoSelectionItem<Resource> {
	//only relevant for level GW_LEVEL
	private String gwId;
	
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
	
	public Set<PhysicalElement> getDevicesByRoom(Room room) {
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
	protected List<String> getDevicePaths(GaRoSelectionItem<Resource> roomSelItem) {
		Set<PhysicalElement> devices = getDevicesByRoom((Room) roomSelItem.resource);
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
}
