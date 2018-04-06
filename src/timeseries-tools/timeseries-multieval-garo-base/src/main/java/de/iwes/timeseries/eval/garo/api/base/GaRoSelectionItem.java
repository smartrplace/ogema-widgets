package de.iwes.timeseries.eval.garo.api.base;

import java.io.UncheckedIOException;
import java.util.List;

import de.iwes.timeseries.eval.api.extended.util.HierarchySelectionItemGeneric;

/** Selection item for {@link GaRoEvalDataProviderGateway}
 * 
 */
public abstract class GaRoSelectionItem<R> extends HierarchySelectionItemGeneric<R> {
	public GaRoSelectionItem(int level, String id) {
		super(level, id);
	}

	public R resource;
	//private final ApplicationManager appMan;

	//only relevant for level GW_LEVEL
	//private String gwId;
	
	//only relevant for level GW_ROOM
	protected List<String> devicePath;
	
	//only relevant for level ROOM_LEVEL, TS_LEVEL
	protected GaRoSelectionItem<R> gwSelectionItem;
	
	//only relevant for level TS_LEVEL
	protected GaRoSelectionItem<R> roomSelectionItem;	
	//private String tsId;
	
	public List<String> getDevicePaths() {
		if(level == 0)
			throw new IllegalStateException("Device paths are not available on GW_LEVEL");
		if(level == 1) {
			if(devicePath == null) try {
				this.devicePath = getDevicePaths(this);
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return devicePath;
		} else {
			if(roomSelectionItem.devicePath == null) try {
				roomSelectionItem.devicePath = getDevicePaths(roomSelectionItem);
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return roomSelectionItem.devicePath;
		}
	}
	
	protected abstract List<String> getDevicePaths(GaRoSelectionItem<R> roomSelItem);

	@Override
	public R getResource() {
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
}
