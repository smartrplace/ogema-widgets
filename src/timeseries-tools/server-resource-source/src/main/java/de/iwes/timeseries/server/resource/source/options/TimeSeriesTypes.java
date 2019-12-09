package de.iwes.timeseries.server.resource.source.options;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;

public class TimeSeriesTypes extends LinkingOption {
	
	public final static SelectionItem schedules = new SelectionItemImpl("schedule", "Schedule");
	public final static SelectionItem onlinedata = new SelectionItemImpl("onlinedata", "Online data");
	private final static List<SelectionItem> items = Collections.unmodifiableList(Arrays.
			<SelectionItem> asList(schedules, onlinedata));

	@Override
	public LinkingOption[] dependencies() {
		return null;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> arg0) {
		return items;
	}

	@Override
	public String id() {
		return "timeSeriesType";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select time series type";
	}

}
