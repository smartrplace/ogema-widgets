package de.iwes.timeseries.server.resource.source.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.server.resource.source.options.GatewayOption.GatewayItem;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;

public class ResourceLeaf extends TerminalOption<Schedule> {
	
	private final LinkingOption[] dependencies;
	private final OnlineTimeSeriesCache onlineCache;
	private final ResourceAccess ra;
	
	public ResourceLeaf(GatewayOption gws, TimeSeriesTypes types, OnlineTimeSeriesCache onlineCache, ResourceAccess ra) {
		this.dependencies = new LinkingOption[]{gws, types};
		this.onlineCache = onlineCache;
		this.ra = ra;
	}

	@Override
	public Schedule getElement(SelectionItem item) {
		return ra.getResource(item.id());
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies.clone();
	}

	@Override
	public List<SelectionItem> getOptions(final List<Collection<SelectionItem>> items) {
		final boolean gatewaysSelected = items != null && !items.isEmpty() && !items.iterator().next().isEmpty();
		final boolean typeSelected = gatewaysSelected && items.size() > 1 && !items.get(1).isEmpty();
		if (!gatewaysSelected || !typeSelected)
			return Collections.emptyList();
		final boolean schedulesSelected = items.get(1).stream().anyMatch(item -> item.id().equals("schedule"));
		final boolean onlineDataSelected = items.get(1).stream().anyMatch(item -> item.id().equals("onlinedata"));
		return items.iterator().next().stream()
			.filter(item -> item instanceof GatewayItem)
			.map(item -> (GatewayItem) item)
			.flatMap(gw -> getItems(gw, schedulesSelected, onlineDataSelected, onlineCache).stream())
			.collect(Collectors.toList());
	}
	
	private static List<SelectionItem> getItems(GatewayItem gw, boolean schedules, boolean online, OnlineTimeSeriesCache onlineCache) {
		final List<SelectionItem> list = new ArrayList<>();
		if (schedules) {
			list.addAll(gw.gw.getSubResources(Schedule.class, true).stream()
					.map(schedule -> new ResourceLeafItem(schedule, false, null))
					.collect(Collectors.toList()));
		} 
		if (online) {
			list.addAll(gw.gw.getSubResources(SingleValueResource.class,true).stream()
					.map(res -> new ResourceLeafItem(res, true, onlineCache))
					.collect(Collectors.toList()));
		}
		return list;
	}

	@Override
	public String id() {
		return "resourceLeaf";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select a gateway resource";
	}
	
	public static class ResourceLeafItem extends SelectionItemImpl {

		private final Resource res;
		private final boolean online;
		private final OnlineTimeSeriesCache onlineCache;
		
		public ResourceLeafItem(Resource res, boolean online, OnlineTimeSeriesCache onlineCache) {
			super(res.getPath(), res.getPath());
			this.res = res;
			this.online = online;
			this.onlineCache = onlineCache;
		}
		
		public ReadOnlyTimeSeries getTimeseries() {
			if (online) 
				return onlineCache.getResourceValuesAsTimeSeries((SingleValueResource) res);
			else
				return (Schedule) res;
		}
		
		
	}

}
