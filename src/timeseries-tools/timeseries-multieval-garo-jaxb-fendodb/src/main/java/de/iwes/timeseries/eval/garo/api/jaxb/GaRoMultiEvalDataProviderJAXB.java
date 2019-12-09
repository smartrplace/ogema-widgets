package de.iwes.timeseries.eval.garo.api.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.ogema.serialization.jaxb.Resource;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.garo.api.base.EvaluationInputImplGaRo;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI.Level;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.util.resource.ResourceHelper.DeviceInfo;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.SelectionItem;

//@Service(HierarchyMultiEvalDataProvider.class)
//@Component
public class GaRoMultiEvalDataProviderJAXB extends GaRoMultiEvalDataProvider<GaRoSelectionItemJAXB> { //HierarchyMultiEvalDataProviderGeneric<GaRoSelectionItemJAXB> {
	public static final String PROVIDER_ID = "GaRoMultiEvalDataProviderJAXB";

	private final GatewayBackupAnalysis gatewayParser;
	
	private List<SelectionItem> gwSelectionItems = null;
	/*if true the gateways available are fixed and usually less entries than
	 *the original size providing all gateways that are available in the input data 
	*/
	private boolean fixGwSelectionItems = false;
	
	public GaRoMultiEvalDataProviderJAXB(GatewayBackupAnalysis gatewayParser) {
		super();
		//super(new String[]{GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID, GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID, "timeSeries"});
		this.gatewayParser = gatewayParser; 
	}

	@Override
	public String id() {
		return PROVIDER_ID;
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Multi-Gateway GaRo Dataprovider JAXB";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Multi-Gateway GaRo Dataprovider JAXB (base)";
	}

	@Override
	public List<SelectionItem> getOptions(int level, GaRoSelectionItemJAXB superItem) {
		switch(level) {
		case GaRoMultiEvalDataProvider.GW_LEVEL:
			if(fixGwSelectionItems) return gwSelectionItems;
			List<String> gwIds = gatewayParser.getGatewayIds();
			if(gwSelectionItems == null) {
				gwSelectionItems = new ArrayList<>();
				for(String gw: gwIds) gwSelectionItems.add(new GaRoSelectionItemJAXB(gw, gatewayParser));
			}
			return gwSelectionItems;
		case GaRoMultiEvalDataProvider.ROOM_LEVEL:
			Optional<Map<String, Resource>> preR = superItem.getGwData().getAllRooms();
			if(!preR.isPresent()) {
				System.out.println("No Rooms for superItem "+superItem.id());
			}
			Map<String, Resource> roomIds = preR.orElse(new HashMap<>()); //superItem.getGwData().getAllRooms().get();
			List<SelectionItem> result = new ArrayList<>();
			for(Entry<String, Resource> room: roomIds.entrySet())
				result.add(new GaRoSelectionItemJAXB(room.getKey(), room.getValue(), superItem));
			result.add(new GaRoSelectionItemJAXB(GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID, null, superItem));
			return result;
		case GaRoMultiEvalDataProvider.TS_LEVEL:
			//CloseableDataRecorder logData = superItem.getLogRecorder();
			List<String> recIds = superItem.getLogDataIds();
			result = new ArrayList<>();
			if(superItem.resource == null) {
				for(String tsId: recIds) {
					result.add(new GaRoSelectionItemJAXB(tsId, superItem));
				}				
			} else {
				//here only use ids that belong to the room
				List<String> devicePaths = superItem.getDevicePaths();
				for(String tsId: recIds) {
					GaRoDataType gtype = GaRoEvalHelper.getDataType(tsId);
					//Gateway-specific types shall be evaluated for every room
					if(gtype != null &&  gtype.getLevel() == Level.GATEWAY) { //GaRoEvalHelper.getGatewayTypes().contains(gtype)) {
						result.add(new GaRoSelectionItemJAXB(tsId, superItem));
						continue;
					}
					for(String devE: devicePaths) {
						if(tsId.startsWith(devE)) {
							result.add(new GaRoSelectionItemJAXB(tsId, superItem));
							break;
						}
					}
				}
			}
			return result;
		default:
			throw new IllegalArgumentException("unknown level");
		}
	}

	/** Set gateways to be offered by this data provider instance. This is relevant if a
	 * MultiEvalation shall not evaluate all gateways in the data set
	 * 
	 * @param gwSelectionItemsToOffer must be a subset of the original result of
	 * {@link #getOptions(int, GaRoSelectionItemJAXB)} with level GW_LEVEL
	 */
	@Override
	public void setGatewaysOffered(List<SelectionItem> gwSelectionItemsToOffer) {
		gwSelectionItems = gwSelectionItemsToOffer;
		fixGwSelectionItems = true;
	}
	
	@Override
	public boolean providesMultipleGateways() {
		return true;
	}

	public void close() {}


	@Override
	public List<String> getGatewayIds() {
		return gatewayParser.getGatewayIds();
	}
	
	@Override
	public EvaluationInputImplGaRo getData(List<SelectionItem> items) {
		List<TimeSeriesData> tsList = new ArrayList<>();
		List<DeviceInfo> devList = new ArrayList<>();
		for(SelectionItem item: items) {
			tsList.add(terminalOption.getElement(item));
			//if(item instanceof GaRoSelectionItemJAXB) {
			//	GaRoSelectionItemJAXB jitem = (GaRoSelectionItemJAXB)item;
				String tsId = terminalOption.getElement(item).id();
				DeviceInfo dev = new DeviceInfo();
				dev.setDeviceResourceLocation(getDevicePath(tsId));
				dev.setDeviceName(getDeviceShortId(dev.getDeviceResourceLocation()));
				devList.add(dev);				
			//} else devList.add(null);
		}
		return new EvaluationInputImplGaRo(tsList, devList);
	}
	
	public static String getDevicePath(String tsId) {
		String[] subs = tsId.split("/");
		if(subs.length > 3) return subs[2];
		else return tsId; //throw new IllegalStateException("Not a valid tsId for Homematic:"+tsId);
	}
	
	public static String getDeviceShortId(String deviceLongId) {
		int len = deviceLongId.length();
		if(len < 4) return deviceLongId;
		String toTest;
		if(deviceLongId.charAt(len-2) == '_') {
			if(len < 6) return deviceLongId;
			toTest =deviceLongId.substring(len-6, len-2);
		} else toTest = deviceLongId.substring(len-4);
		return toTest;
	}
}
