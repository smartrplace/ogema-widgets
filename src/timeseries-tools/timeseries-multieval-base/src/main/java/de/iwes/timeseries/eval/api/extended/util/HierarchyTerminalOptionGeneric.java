package de.iwes.timeseries.eval.api.extended.util;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class HierarchyTerminalOptionGeneric<R, T extends HierarchySelectionItemGeneric<R>> extends GenericTerminalOption {
	private final HierarchyMultiEvalDataProviderGeneric<R, T> provider;
	
	public HierarchyTerminalOptionGeneric(String id, String label, LinkingOption[] dependencies,
			HierarchyMultiEvalDataProviderGeneric<R, T> provider) {
		super(id, label, dependencies);
		this.provider = provider;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
		//if(dependencies.size() != provider.selectionOptions().length-1)
		//	System.out.println("Warning( to be removed) : TerminalOption.getOptions requires dependencies exactly for all non-terminal options.");
			//Note: When a DataProvider has a shorter dependency tree than the governing  DataPovider then
			//the number of dependencies given here might be longer than necessary
			//throw new IllegalArgumentException("TerminalOption.getOptions requires dependencies exactly for all non-terminal options.");
		return provider.getOptions(dependencies, provider.selectionOptions().length-1);
	}

	@Override
	public TimeSeriesData getElement(SelectionItem item) {
		@SuppressWarnings("unchecked")
		T selItem = (T)item;
		return selItem.getTimeSeriesData();
	}
}
