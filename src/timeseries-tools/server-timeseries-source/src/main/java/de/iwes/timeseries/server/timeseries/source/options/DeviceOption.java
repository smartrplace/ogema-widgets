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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.serialization.jaxb.Resource;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;
import org.smartrplace.analysis.backup.parser.api.MemoryGateway;
//import org.smartrplace.ogema.recordeddata.slotsdb.CloseableDataRecorder;

import de.iwes.timeseries.server.timeseries.source.options.RoomOption.ExtendedRoomInfo;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;

// TODO device type options; see MemoryGatewayImpl
public class DeviceOption extends LinkingOption {
	
	private final GatewayBackupAnalysis gatewayParser;
	private final LinkingOption[] dependencies;
	
	public DeviceOption(GatewayBackupAnalysis gatewayParser,GatewayOption gws, RoomOption rooms) {
		this.gatewayParser=gatewayParser;
		this.dependencies = new LinkingOption[]{gws,rooms};
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies.clone();
	}

	@Override
	public List<SelectionItem> getOptions(final List<Collection<SelectionItem>> items) {
		final boolean gatewaysSet = (items != null) && !items.isEmpty() && !items.get(0).isEmpty();
		final boolean roomsSet = (items != null) && items.size() > 1 && !items.get(1).isEmpty();
		if (roomsSet) {
			items.get(1).stream()
				.filter(item -> item instanceof ExtendedRoomInfo)
				.map(item -> (ExtendedRoomInfo) item)
				.flatMap(item -> getDevices(item))
				.collect(Collectors.toList());
		}
		final Collection<SelectionItem> gateways = gatewaysSet ? items.iterator().next() : null;
		return gatewayParser.getGatewayIds().stream()
			.filter(gw -> gatewaysSet && gateways.stream().anyMatch(item -> item.id().equals(gw)))
			.map(gw -> {
				try {
					return gatewayParser.getGateway(gw);
				} catch (UncheckedIOException | IOException e) {
					return null;
				}
			})
			.filter(gw -> gw != null)
			.flatMap(gw -> getDevices(gw))
			.collect(Collectors.toList());
	}

	@Override
	public String id() {
		return "devices";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select devices";
	}
	
	private static Stream<ExtendedDeviceInfo> getDevices(final MemoryGateway gateway) {
		final Optional<Map<String, Resource>> opt = gateway.getAllDevices();
		if (!opt.isPresent())
			return Stream.empty();
		return opt.get().values().stream().map(dev -> new ExtendedDeviceInfo(gateway, dev));
	}
	
	private static Stream<ExtendedDeviceInfo> getDevices(final ExtendedRoomInfo roomInfo) {
		final Optional<List<Resource>> optional = roomInfo.getGateway().getDevicesByRoom(roomInfo.getRoom());
		if (!optional.isPresent())
			return Stream.empty();
		return optional.get().stream()
			.map(device -> new ExtendedDeviceInfo(roomInfo.getGateway(), device));
	}
	
	static class ExtendedDeviceInfo extends SelectionItemImpl {
		
		private final MemoryGateway gw;
		private final Resource device;
		
		public ExtendedDeviceInfo(MemoryGateway gw, Resource device) {
			super("gw:" + gw.getId() + "_dev:" + device.getPath(), "GW: " + gw.getId() + " Device: " + device.getName());
			this.gw = gw;
			this.device = device;
		}
		
		public MemoryGateway getGateway() {
			return gw;
		}
		
		public Resource getDevice() {
			return device;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof ExtendedDeviceInfo))
				return false;
			final ExtendedDeviceInfo other = (ExtendedDeviceInfo) obj;
			return other.gw.getId().equals(this.gw.getId()) && other.device.getPath().equals(this.device.getPath());
		}
		
		@Override
		public int hashCode() {
			return gw.getId().hashCode() * 13 + device.getPath().hashCode() * 3;
		}
		
	}

}
