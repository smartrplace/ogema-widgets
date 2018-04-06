package de.iwes.timeseries.eval.garo.api.base;

import java.util.List;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.extended.DataProviderResInfoGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationItemSelector;
import de.iwes.timeseries.eval.api.extended.util.AbstractMultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.widgets.html.selectiontree.LinkingOptionType;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class GaRoMultiEvaluationInput<R> extends AbstractMultiEvaluationInputGeneric<R> {
	private final GaRoDataType terminalDataType;
	private final List<String> topLevelIdsToEvaluate;
	private final String topLevelOptionId;
	//private final LinkingOptionType terminalOptionType;
	
	public GaRoMultiEvaluationInput(DataProviderType type, DataProviderResInfoGeneric<R, ?> dataProvider,
			GaRoDataType terminalDataType, List<String> topLevelIdsToEvaluate, String topLevelOptionId) {
		super(type, dataProvider);
		this.terminalDataType = terminalDataType;
		this.topLevelIdsToEvaluate = topLevelIdsToEvaluate;
		this.topLevelOptionId = topLevelOptionId;
		//this.terminalOptionType = type.selectionOptions()[type.selectionOptions().length-1];
	}
	
	@Override
	public MultiEvaluationItemSelector itemSelector() {
		return new MultiEvaluationItemSelector() {
			@Override
			public boolean useDataProviderItem(LinkingOptionType linkingOptionType, SelectionItem item) {
				if(linkingOptionType.id().equals(topLevelOptionId)) {
					if(topLevelIdsToEvaluate == null) return true;
					@SuppressWarnings("unchecked")
					GaRoSelectionItem<R> gsi = (GaRoSelectionItem<R>)item;
					if(gsi.gwSelectionItem == null) return topLevelIdsToEvaluate.contains(gsi.id());
					else if(topLevelIdsToEvaluate.contains(gsi.gwSelectionItem.id())) return true;
					return false;
				}
				if(linkingOptionType.id().equals(GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID)) return true;
				if(linkingOptionType.id().equals(GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID)) return true;
				if(terminalDataType == GaRoDataType.Any) return true;
				@SuppressWarnings("unchecked")
				GaRoSelectionItem<R> gsi = (GaRoSelectionItem<R>)item;
				return GaRoEvalHelper.getDataType(gsi.id()) == terminalDataType;
			}
		};
	}
}
