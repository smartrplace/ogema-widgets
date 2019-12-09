package de.iwes.timeseries.server.resource.source.options;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.gateway.remotesupervision.GatewayTransferInfo;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;

public class GatewayOption extends LinkingOption {
	
	private final ResourceAccess ra;
	
	public GatewayOption(ResourceAccess ra) {
		this.ra = ra;
	}

	@Override
	public LinkingOption[] dependencies() {
		return null;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> items) {
		return ra.getResources(GatewayTransferInfo.class).stream()
			.filter(gw -> gw.id().isActive())
			.map(gw -> new GatewayItem(gw))
			.collect(Collectors.toList());
	}

	@Override
	public String id() {
		return "gateways";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select gateways";
	}
	
	static class GatewayItem extends SelectionItemImpl {
		
		final GatewayTransferInfo gw;
		
		public GatewayItem(GatewayTransferInfo gw) {
			super(gw.id().getValue(), gw.id().getValue());
			this.gw = gw;
		}
		
	}

}
