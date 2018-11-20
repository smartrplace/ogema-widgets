/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.timeseries.eval.base.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
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

@Component(
	service=Application.class,
	property= {
		DataProvider.PROVIDER_ID + "=" + ResourceDataProvider.ID,
		DataProvider.PROVIDER_LABEL  + "=" + ResourceDataProvider.DEFAULT_LABEL
	}
)
public class ResourceDataProvider implements DataProvider<Resource>, Application {
	
	final static String ID = "resourceDataProvider";
	final static String DEFAULT_LABEL = "Resource data provider";
	private ApplicationManager am;
	private Logger logger;
	@SuppressWarnings("rawtypes")
	private ServiceRegistration<DataProvider> sreg;
	private volatile LinkingOption[] opts;
	private volatile ResourceLeaf resourceLeaf;
	private volatile BundleContext context;
	
	@Reference(cardinality=ReferenceCardinality.OPTIONAL, policy=ReferencePolicy.DYNAMIC)
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
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						sreg.unregister();
					} catch (Exception ignore) {} 
				}
			}).start();
		}
		sreg = null;
		opts = null;
		resourceLeaf = null;
	}
	
	@Override
	public String id() {
		return ID;
	}
	
	@Override
	public String label(OgemaLocale locale) {
		return DEFAULT_LABEL;
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
