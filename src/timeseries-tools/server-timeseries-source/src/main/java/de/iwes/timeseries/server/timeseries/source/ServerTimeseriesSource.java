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
package de.iwes.timeseries.server.timeseries.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
//import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.server.timeseries.source.options.DeviceOption;
import de.iwes.timeseries.server.timeseries.source.options.GatewayOption;
import de.iwes.timeseries.server.timeseries.source.options.MemoryResourceLeaf;
import de.iwes.timeseries.server.timeseries.source.options.RoomOption;
import de.iwes.timeseries.server.timeseries.source.options.MemoryResourceLeaf.ExtendedLogdataInfo;

@Service(DataProvider.class)
@Component
public class ServerTimeseriesSource implements DataProvider<RecordedData> {
	
	private volatile LinkingOption[] options;
	private volatile MemoryResourceLeaf resources;
	
	@Reference
	private GatewayBackupAnalysis gatewayParser;
	
	@Activate
	protected void activate() {
		final GatewayOption gws = new GatewayOption(gatewayParser);
		final RoomOption rooms = new RoomOption(gatewayParser, gws);
		final DeviceOption devices = new DeviceOption(gatewayParser, gws, rooms);
		this.resources = new MemoryResourceLeaf(gatewayParser, gws, rooms, devices);
		this.options = new LinkingOption[]{gws, rooms, devices, resources};
	}
	
	@Deactivate
	protected void deactivate() {
		this.options = null;
		this.resources = null;
	}

	@Override
	public String description(OgemaLocale arg0) {
		return "Backup log data provider (reads SlotsDB data collected from other gateways)";
	}

	@Override
	public String id() {
		return "backupLogdataProvider";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Backup log data provider";
	}

	@Override
	public EvaluationInput getData(List<SelectionItem> items) {
		Objects.requireNonNull(items);
		final List<TimeSeriesData> timeSeriesData = new ArrayList<>();
		for (SelectionItem item : items) {
			if (!(item instanceof ExtendedLogdataInfo)) {
				throw new IllegalArgumentException("Argument must be of type " + 
						ExtendedLogdataInfo.class.getSimpleName() + ", got " + item.getClass().getSimpleName());
			}
			ExtendedLogdataInfo resourceItem = (ExtendedLogdataInfo) item;
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
