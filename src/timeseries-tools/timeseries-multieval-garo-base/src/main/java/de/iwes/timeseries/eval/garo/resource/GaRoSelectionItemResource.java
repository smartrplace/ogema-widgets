package de.iwes.timeseries.eval.garo.resource;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ogema.core.model.Resource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.model.locations.Location;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.recordeddata.DataRecorder;
import org.ogema.recordeddata.RecordedDataStorage;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoSelectionItem;

public class GaRoSelectionItemResource extends GaRoSelectionItem {
	//only relevant for level GW_LEVEL
	//private final ApplicationManager appMan;
	private final DataRecorder logData;
	private List<String> recIds;
	
	public Resource resource;
	
	//only relevant for level TS_LEVEL
	public GaRoSelectionItemResource getRoomSelectionItem() {
		return (GaRoSelectionItemResource) roomSelectionItem;
	}
	
	public GaRoSelectionItemResource(String gwId) {
		super(GaRoMultiEvalDataProvider.GW_LEVEL, gwId);
		this.logData = null;
	}
	public GaRoSelectionItemResource(String name, Resource room, GaRoSelectionItemResource superSelectionItem,
			DataRecorder dataRecorder) {
		super(GaRoMultiEvalDataProvider.ROOM_LEVEL, name);
		this.logData = dataRecorder;
		this.gwSelectionItem = GaRoMultiEvalDataProviderResource.gwSelectionItem;
		this.roomSelectionItem = this;
		//this.gwId = superSelectionItem.gwId;
		//this.roomId = room.getKey();
		this.resource = room;
	}
	public GaRoSelectionItemResource(String tsId, GaRoSelectionItemResource superSelectionItem) {
		super(GaRoMultiEvalDataProvider.TS_LEVEL, tsId);
		this.logData = superSelectionItem.logData;
		this.gwSelectionItem = superSelectionItem.gwSelectionItem;
		this.roomSelectionItem = superSelectionItem;
		//this.tsId = tsId;
	}
	
	public DataRecorder getLogRecorder() {
		return logData;
		/*if(level == 0) {
			if(logData == null) try {
				this.logData = getGwData().getLogdata().get();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return logData;
		} else {
			if(getGwSelectionItem().logData == null) try {
				getGwSelectionItem().logData = getGwSelectionItem().getGwData().getLogdata().get();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return getGwSelectionItem().logData;
		}*/
	}
	public List<String> getLogDataIds() {
		if(level == 0) {
			if(recIds == null) try {
				this.recIds = getLogRecorder().getAllRecordedDataStorageIDs();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return recIds;
		} else {
			if(getRoomSelectionItem().recIds == null) try {
				getRoomSelectionItem().recIds = getRoomSelectionItem().getLogRecorder().getAllRecordedDataStorageIDs();
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return getRoomSelectionItem().recIds;
		}
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
		for(PhysicalElement dev: devices) result.add(dev.getPath());
		return result;
	}

	@Override
	public TimeSeriesData getTimeSeriesData() {
		if(level == GaRoMultiEvalDataProvider.TS_LEVEL) {
			RecordedDataStorage recData = getLogRecorder().getRecordedDataStorage(id);
			return new TimeSeriesDataImpl(recData, id,
					id, InterpolationMode.STEPS);
		}
		return null;
	}
	
	//TODO: Shall be replaced
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
		return getRoomTypeStatic(getResource());
	}
	
	public static Integer getRoomTypeStatic(Resource room) {
		if(!(room instanceof Room)) return null;
		Room rroom = (Room)room;
		if(!rroom.type().isActive()) return null;
		return rroom.type().getValue();		
	}

	@Override
	public String getRoomName() {
		if(resource == null) return null;
		return getResource().getName();
	}

	@Override
	public String getPath() {
		if(resource == null) return null;
		return getResource().getPath();
	}
}
