package de.iwes.timeseries.eval.base.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.html.selectiontree.samples.BaseDeviceTypeOption;
import de.iwes.widgets.html.selectiontree.samples.RoomTypeOption;
import de.iwes.widgets.html.selectiontree.samples.resource.DeviceOptionResource;
import de.iwes.widgets.html.selectiontree.samples.resource.ResourceLeaf;
import de.iwes.widgets.html.selectiontree.samples.resource.ResourceLeaf.ResourceLeafSelectionItem;
import de.iwes.widgets.html.selectiontree.samples.resource.ResourceTimeSeriesOption;
import de.iwes.widgets.html.selectiontree.samples.resource.RoomOptionResource;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;

@Service(Application.class)
@Component
public class ResourceDataProvider implements DataProvider<Resource>, Application {
	
	private ApplicationManager am;
	private Logger logger;
	@SuppressWarnings("rawtypes")
	private ServiceRegistration<DataProvider> sreg;
	private volatile LinkingOption[] opts;
	private volatile ResourceLeaf resourceLeaf;
	private volatile BundleContext context;
	
	@Reference(cardinality=ReferenceCardinality.OPTIONAL_UNARY, policy=ReferencePolicy.DYNAMIC)
	private volatile NameService nameService;
	
	@Reference
	private OnlineTimeSeriesCache onlineTimeSeriesCache;
	
	@Activate
	protected void start(BundleContext ctx) {
		this.context = ctx;
	}
	
	@Deactivate
	protected void stop() {
		this.context = null;
	}
	
	@Override
	public void start(final ApplicationManager appManager) {
		this.am = appManager;
		this.logger = appManager.getLogger();
		final RoomTypeOption roomTypes = new RoomTypeOption();
		final RoomOptionResource rooms = new RoomOptionResource(roomTypes, am.getResourceAccess());
		final BaseDeviceTypeOption deviceTypes = new BaseDeviceTypeOption();
		final DeviceOptionResource devices = new DeviceOptionResource(deviceTypes, rooms, am.getResourceAccess());
		final ResourceTimeSeriesOption timeSeries = new ResourceTimeSeriesOption(devices, am.getResourceAccess());
		this.resourceLeaf = new ResourceLeaf(rooms, devices, timeSeries, am.getResourceAccess(), 
				nameService, onlineTimeSeriesCache);
		opts = new LinkingOption[] {roomTypes, rooms, deviceTypes, devices, timeSeries, resourceLeaf};
		sreg = context.registerService(DataProvider.class, this, null);
		logger.info("ResourceDataProvider service started");
	}

	@Override
	public void stop(AppStopReason reason) {
		am = null;
		logger = null;
		if (sreg != null) {
			try {
				sreg.unregister();
			} catch (Exception ignore) {} 
		}
		sreg = null;
		opts = null;
		resourceLeaf = null;
	}
	
	@Override
	public String id() {
		return "resourceDataProvider";
	}
	
	@Override
	public String label(OgemaLocale locale) {
		return "Resource data provider";
	}
	
	@Override
	public String description(OgemaLocale locale) {
		return "Get OGEMA timeseries (schedules, log data, online resource values)";
	}

	
	@Override
	public LinkingOption[] selectionOptions() {
		return opts.clone();
	}
	
	@Override
	public TerminalOption<? extends ReadOnlyTimeSeries> getTerminalOption() {
		return resourceLeaf;
	}

	@Override
	public EvaluationInput getData(List<SelectionItem> items) {
		Objects.requireNonNull(items);
		final List<TimeSeriesData> timeSeriesData = new ArrayList<>();
		for (SelectionItem item : items) {
			if (!(item instanceof ResourceLeafSelectionItem)) {
				throw new IllegalArgumentException("Argument must be of type " + 
						ResourceLeafSelectionItem.class.getSimpleName() + ", got " + item.getClass().getSimpleName());
			}
			ResourceLeafSelectionItem resourceItem = (ResourceLeafSelectionItem) item;
			ReadOnlyTimeSeries timeSeries = resourceItem.getTimeSeries();
			TimeSeriesDataImpl dataImpl = new TimeSeriesDataImpl(timeSeries, resourceItem.label(OgemaLocale.ENGLISH), // TODO? 
					resourceItem.label(OgemaLocale.ENGLISH), null);
			timeSeriesData.add(dataImpl);
		}
		return new EvaluationInputImpl(timeSeriesData);
	}

}
