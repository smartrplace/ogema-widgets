package de.iwes.timeseries.eval.api.extended;

import de.iwes.widgets.html.selectiontree.LinkingOptionType;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public interface MultiEvaluationItemSelector {
	public boolean useDataProviderItem(LinkingOptionType linkingOptionType, SelectionItem item);
}
