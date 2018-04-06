package de.iwes.timeseries.eval.api.extended;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public interface DataProviderResInfoGeneric<R, T> extends DataProvider<T> {
	R getResource(SelectionItem item);
}
