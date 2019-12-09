package de.iwes.timeseries.server.resource.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;
import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.server.resource.source.options.GatewayOption;
import de.iwes.timeseries.server.resource.source.options.ResourceLeaf;
import de.iwes.timeseries.server.resource.source.options.TimeSeriesTypes;
import de.iwes.timeseries.server.resource.source.options.ResourceLeaf.ResourceLeafItem;

@Service(Application.class)
@Component
@Property(name=DataProvider.PROVIDER_ID, value=ServerResourceSource.ID)
public class ServerResourceSource implements DataProvider<ReadOnlyTimeSeries>, Application {
	
	final static String ID = "gatewaysResourceProvider";
	private final static TimeSeriesTypes types = new TimeSeriesTypes();
	private volatile BundleContext context;
	private volatile LinkingOption[] options;
	private volatile ResourceLeaf resources;
	@SuppressWarnings("rawtypes")
	private volatile ServiceRegistration<DataProvider> sreg;
	
	@Reference
	private OnlineTimeSeriesCache onlineCache;
	
	@Activate
	protected void start(BundleContext ctx) {
		this.context = ctx; 
	}
	
	@Deactivate
	protected void stop() {
		this.context = null;
	}
	
	@Override
	public void start(ApplicationManager appManager) {
		final GatewayOption gws = new GatewayOption(appManager.getResourceAccess());
		this.resources = new ResourceLeaf(gws, types, onlineCache, appManager.getResourceAccess());
		this.options = new LinkingOption[]{gws,types,resources};
		sreg = context.registerService(DataProvider.class, this, null);
	}

	@Override
	public void stop(AppStopReason reason) {
		@SuppressWarnings("rawtypes")
		final ServiceRegistration<DataProvider> sreg = this.sreg;
		if (sreg != null) {
			try {
				sreg.unregister();
			} catch (Exception ignore) {}
		}
		this.resources = null;
		this.options = null;
		this.sreg = null;
	}
	
	@Override
	public String description(OgemaLocale arg0) {
		return "Access gateway-specific data stored in resources on the supervision server (for usage on server)";
	}

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Server gateway resource resource provider";
	}

	@Override
	public EvaluationInput getData(List<SelectionItem> items) {
		Objects.requireNonNull(items);
		final List<TimeSeriesData> timeSeriesData = new ArrayList<>();
		for (SelectionItem item : items) {
			if (!(item instanceof ResourceLeafItem)) {
				throw new IllegalArgumentException("Argument must be of type " + 
						ResourceLeafItem.class.getSimpleName() + ", got " + item.getClass().getSimpleName());
			}
			ResourceLeafItem resourceItem = (ResourceLeafItem) item;
			TimeSeriesDataImpl dataImpl = new TimeSeriesDataImpl(resourceItem.getTimeseries(), resourceItem.label(OgemaLocale.ENGLISH), // TODO? 
					resourceItem.label(OgemaLocale.ENGLISH), null);
			timeSeriesData.add(dataImpl);
		}
		return new EvaluationInputImpl(timeSeriesData);
	}

	@Override
	public TerminalOption<? extends ReadOnlyTimeSeries> getTerminalOption() {
		return resources;
	}

	@Override
	public LinkingOption[] selectionOptions() {
		final LinkingOption[] opts = this.options;
		return opts != null ? opts.clone() : null;
	}
	
}
