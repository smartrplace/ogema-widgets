package de.iwes.timeseries.eval.api.extended.util;

import java.util.Collection;
import java.util.List;

import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class HierarchyLinkingOptionGeneric<R, T extends HierarchySelectionItemGeneric<R>> extends GenericLinkingOption {
	private final HierarchyMultiEvalDataProviderGeneric<R, T> provider;

	public HierarchyLinkingOptionGeneric(String id, String label, LinkingOption[] dependencies,
			HierarchyMultiEvalDataProviderGeneric<R, T> provider) {
		super(id, label, dependencies);
		this.provider = provider;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
		return provider.getOptions(dependencies, dependencies.size());
	}
}
