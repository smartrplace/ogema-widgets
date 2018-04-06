package de.iwes.timeseries.eval.garo.api.base;

import java.util.List;

import de.iwes.timeseries.eval.api.extended.util.HierarchyMultiEvalDataProviderGeneric;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public abstract class GaRoMultiEvalDataProvider<R> extends HierarchyMultiEvalDataProviderGeneric<R, GaRoSelectionItem<R>> {
	//private final GatewayBackupAnalysis gatewayParser;
	public static final int GW_LEVEL = 0;
	public static final int ROOM_LEVEL = 1;
	public static final int TS_LEVEL = 2;
	public static final String GW_LINKINGOPTION_ID = "gateways";
	public static final String ROOM_LINKINGOPTION_ID = "rooms";
	
	protected List<SelectionItem> gwSelectionItems = null;
	
	public GaRoMultiEvalDataProvider() {
		super(new String[]{"gateways", "rooms", "timeSeries"});
//		this.gatewayParser = gatewayParser; 
	}

	@Override
	protected abstract List<SelectionItem> getOptions(int level, GaRoSelectionItem<R> superItem);
}
