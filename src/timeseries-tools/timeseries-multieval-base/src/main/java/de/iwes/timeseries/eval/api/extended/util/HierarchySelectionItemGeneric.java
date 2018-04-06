package de.iwes.timeseries.eval.api.extended.util;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public abstract class HierarchySelectionItemGeneric<T> implements SelectionItem {
	protected final int level;
	protected final String id;

	public HierarchySelectionItemGeneric(int level, String id) {
		this.level = level;
		this.id = id;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return id();
	}
	
	public abstract T getResource();
	
	/**only relevant for SelectionItems of TerminalOption, otherwise just return null*/
	public abstract TimeSeriesData getTimeSeriesData();
}
