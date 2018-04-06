package de.iwes.timeseries.eval.garo.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.timeseries.eval.api.extended.util.HierarchyMultiEvalDataProviderGeneric;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class GaRoMultiEvalDataProviderResource extends HierarchyMultiEvalDataProviderGeneric<Resource, GaRoSelectionItemResource> {
	private final ApplicationManager appMan;
	public static final int GW_LEVEL = 0;
	public static final int ROOM_LEVEL = 1;
	public static final int TS_LEVEL = 2;
	public static final String GW_LINKINGOPTION_ID = "gateways";
	public static final String ROOM_LINKINGOPTION_ID = "rooms";
	
	private List<SelectionItem> gwSelectionItems = null;
	private List<SelectionItem> roomSelectionItems = null;
	/*if true the gateways available are fixed and usually less entries than
	 *the original size providing all gateways that are available in the input data 
	*/
	private boolean fixRoomSelectionItems = false;
	
	public GaRoMultiEvalDataProviderResource(ApplicationManager appMan) {
		super(new String[]{"gateways", "rooms", "timeSeries"});
		gwSelectionItems = new ArrayList<>();
		gwSelectionItems.add(new GaRoSelectionItemResource("myGateway"));
		this.appMan = appMan; 
	}

	@Override
	protected List<SelectionItem> getOptions(int level, GaRoSelectionItemResource superItem) {
		switch(level) {
		case GW_LEVEL:
			return gwSelectionItems;
		case ROOM_LEVEL:
			if(fixRoomSelectionItems) return roomSelectionItems;
			List<Room> roomIds = appMan.getResourceAccess().getResources(Room.class);
			roomSelectionItems = new ArrayList<>();
			for(Room room: roomIds)
				roomSelectionItems.add(new GaRoSelectionItemResource(ResourceUtils.getHumanReadableName(room), room, superItem));
			return roomSelectionItems;
		case TS_LEVEL:
			//CloseableDataRecorder logData = superItem.getLogRecorder();
			Set<PhysicalElement> recIds = superItem.getDevicesByRoom((Room) superItem.getResource());
			List<SelectionItem> result = new ArrayList<>();
			for(PhysicalElement devE: recIds)
				for(SingleValueResource ts: getRecordedDataOfDevice(devE)) {
					result.add(new GaRoSelectionItemResource(ResourceUtils.getHumanReadableName(ts), ts, superItem));
				}
			return result;
		default:
			throw new IllegalArgumentException("unknown level");
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
}
