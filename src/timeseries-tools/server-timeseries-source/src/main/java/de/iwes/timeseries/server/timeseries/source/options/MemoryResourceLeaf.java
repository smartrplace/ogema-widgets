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
package de.iwes.timeseries.server.timeseries.source.options;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.recordeddata.RecordedData;
import org.ogema.recordeddata.DataRecorder;
import org.ogema.serialization.jaxb.Resource;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;
import org.smartrplace.analysis.backup.parser.api.MemoryGateway;
//import org.smartrplace.ogema.recordeddata.slotsdb.CloseableDataRecorder;
import org.smartrplace.logging.fendodb.CloseableDataRecorder;

import de.iwes.timeseries.server.timeseries.source.options.DeviceOption.ExtendedDeviceInfo;
import de.iwes.timeseries.server.timeseries.source.options.RoomOption.ExtendedRoomInfo;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;

public class MemoryResourceLeaf extends TerminalOption<RecordedData> {
	
	private final GatewayBackupAnalysis gatewayParser;
	private final LinkingOption[] dependencies;
	
	public MemoryResourceLeaf(GatewayBackupAnalysis gatewayParser,GatewayOption gws, RoomOption rooms,DeviceOption devs) {
		this.gatewayParser=gatewayParser;
		this.dependencies = new LinkingOption[]{gws,rooms,devs};
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies.clone();
	}

	@Override
	public List<SelectionItem> getOptions(final List<Collection<SelectionItem>> items) {
		final boolean gatewaysSet = items != null && !items.isEmpty() && !items.get(0).isEmpty();
		if (!gatewaysSet)
			return Collections.emptyList();
		final boolean roomsSet = items != null && items.size() > 1 && !items.get(1).isEmpty();
		final boolean devicesSet = items != null && items.size() > 2 && !items.get(2).isEmpty();
		if (devicesSet) {
			return items.get(2).stream()
				.filter(item -> item instanceof ExtendedDeviceInfo)
				.map(item -> (ExtendedDeviceInfo) item)
				.flatMap(item -> getAllTimeseries(item))
				.collect(Collectors.toList());
		}
		if (roomsSet) {
			return items.get(1).stream()
					.filter(item -> item instanceof ExtendedRoomInfo)
					.map(item -> (ExtendedRoomInfo) item)
					.flatMap(item -> getAllTimeseries(item))
					.collect(Collectors.toList());
		}
		return items.get(0).stream()
			.map(gw -> {
				try {
					return gatewayParser.getGateway(gw.id());
				} catch (UncheckedIOException | IOException e) {
					return null;
				}
			})
			.filter(gw -> gw != null)
			.flatMap(gw -> getData(gw))
			.collect(Collectors.toList());
	}

	@Override
	public String id() {
		return "memory_resource_leaf";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select a data source";
	}

	@Override
	public RecordedData getElement(SelectionItem item) {
		if (!(item instanceof ExtendedLogdataInfo))
			return null;
		return ((ExtendedLogdataInfo) item).timeseries;
	}
	
	private static Stream<SelectionItem> getAllTimeseries(final ExtendedRoomInfo roomInfo) {
		final Optional<DataRecorder> opt = roomInfo.getGateway().getLogdata();
		if (!opt.isPresent())
			return Stream.empty();
		final CloseableDataRecorder dataRecorder = (CloseableDataRecorder) opt.get();
		return dataRecorder.getAllRecordedDataStorageIDs().stream()
				.filter(id -> roomContainsDataPoint(roomInfo.getGateway(), roomInfo.getRoom(), id))
				.map(id -> dataRecorder.getRecordedDataStorage(id))
				.filter(recordedData -> recordedData != null)
				.map(recordedData -> new ExtendedLogdataInfo(roomInfo.getGateway(), recordedData));
	}
	
	private static boolean roomContainsDataPoint(final MemoryGateway gw, final Resource room, final String path) {
		final Optional<List<Resource>> opt = gw.getDevicesByRoom(room);
		if (!opt.isPresent())
			return false;
		return opt.get().stream()
			.filter(device -> path.startsWith(device.getPath()))
			.findAny()
			.isPresent();
	}
	
	private static Stream<SelectionItem> getAllTimeseries(final ExtendedDeviceInfo deviceInfo) {
		final Optional<DataRecorder> opt = deviceInfo.getGateway().getLogdata();
		if (!opt.isPresent())
			return Stream.empty();
		final CloseableDataRecorder dataRecorder = (CloseableDataRecorder) opt.get();
		return dataRecorder.getAllRecordedDataStorageIDs().stream()
				.filter(id -> id.startsWith(deviceInfo.getDevice().getPath()))
				.map(id -> dataRecorder.getRecordedDataStorage(id))
				.filter(recordedData -> recordedData != null)
				.map(recordedData -> new ExtendedLogdataInfo(deviceInfo.getGateway(), recordedData));
	}
	
	private static Stream<ExtendedLogdataInfo> getData(final MemoryGateway gateway) {
		final Optional<DataRecorder> opt = gateway.getLogdata();
		if (!opt.isPresent())
			return Stream.empty();
		return ((CloseableDataRecorder)opt.get()).getAllTimeSeries().stream()
				.map(recordedData -> new ExtendedLogdataInfo(gateway, recordedData));
	}
	
	public static class ExtendedLogdataInfo extends SelectionItemImpl {
		
		private final MemoryGateway gw;
		// may be null
		private final Resource target;
		private final RecordedData timeseries;
		
		public ExtendedLogdataInfo(MemoryGateway gw, RecordedData timeseries) {
			super("gw:" + gw.getId() + "_res:" + timeseries.getPath(), "GW: " + gw.getId() + " Resource: " + timeseries.getPath());
			this.gw = gw;
			this.timeseries = timeseries;
			Optional<Resource> opt = gw.getResource(timeseries.getPath());
			this.target = opt.isPresent() ? opt.get() : null;
		}
		
		public MemoryGateway getGateway() {
			return gw;
		}
		
		public Resource getDevice() {
			return target;
		}
		
		public RecordedData getTimeseries() {
			return timeseries;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof ExtendedLogdataInfo))
				return false;
			final ExtendedLogdataInfo other = (ExtendedLogdataInfo) obj;
			try {
				return other.gw.getId().equals(this.gw.getId()) && other.target.getPath().equals(this.target.getPath());
			} catch(NullPointerException e) {
				if(gw != null && gw.getId() != null && other.gw != null && other.gw.getId() != null) return other.gw.getId().equals(this.gw.getId());
				if(target != null && target.getPath() != null && other.target != null && other.target.getPath() != null) return other.target.getPath().equals(this.target.getPath());
				return super.equals(obj);
			}
		}
		
		@Override
		public int hashCode() {
			try {
				return gw.getId().hashCode() * 13 + target.getPath().hashCode() * 3;
			} catch(NullPointerException e) {
				if(gw != null && gw.getId() != null) return gw.getId().hashCode() * 13;
				if(target != null && target.getPath() != null) return target.getPath().hashCode() * 3;
				return super.hashCode();
			}
		}
		
	}
	
	

}

