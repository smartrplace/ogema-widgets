package de.iwes.timeseries.eval.api.extended.util;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;

public abstract class GenericTerminalOption extends TerminalOption<TimeSeriesData> {
	private final String id;
	private final String label;
	private final LinkingOption[] dependencies;
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies;
	}

	public GenericTerminalOption(String id, String label, LinkingOption[] dependencies) {
		super();
		this.id = id;
		this.label = label;
		this.dependencies = dependencies;
	}

	@Override
	public abstract List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies);
}
