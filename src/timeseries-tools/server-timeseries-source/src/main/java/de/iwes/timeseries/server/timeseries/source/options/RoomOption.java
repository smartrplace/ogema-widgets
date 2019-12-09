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

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;

// TODO room type options dependency
public class RoomOption extends LinkingOption {
	
	private final GatewayBackupAnalysis gatewayParser;
	private final LinkingOption[] dependencies;
	
	public RoomOption(GatewayBackupAnalysis gatewayParser,GatewayOption gws) {
		this.gatewayParser=gatewayParser;
		this.dependencies = new LinkingOption[]{gws};
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies.clone();
	}

	@Override
	public List<SelectionItem> getOptions(final List<Collection<SelectionItem>> items) {
		final boolean gatewaysSet = items != null && !items.isEmpty() && !items.get(0).isEmpty();
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
			.flatMap(gw -> getRooms(gw))
			.collect(Collectors.toList());
	}

	@Override
	public String id() {
		return "rooms";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select rooms";
	}
	
	private static Stream<ExtendedRoomInfo> getRooms(final MemoryGateway gateway) {
		final Optional<Map<String, Resource>> opt = gateway.getAllRooms();
		if (!opt.isPresent())
			return Stream.empty();
		return opt.get().values().stream().map(room -> new ExtendedRoomInfo(gateway, room));
	}
	
	static class ExtendedRoomInfo extends SelectionItemImpl {
		
		private final MemoryGateway gw;
		private final Resource room;
		
		public ExtendedRoomInfo(MemoryGateway gw, Resource room) {
			super("gw:" + gw.getId() + "_rm:" + room.getPath(), "GW: " + gw.getId() + " Room: " + room.getName());
			this.gw = gw;
			this.room = room;
		}
		
		public MemoryGateway getGateway() {
			return gw;
		}
		
		public Resource getRoom() {
			return room;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof ExtendedRoomInfo))
				return false;
			final ExtendedRoomInfo other = (ExtendedRoomInfo) obj;
			return other.gw.getId().equals(this.gw.getId()) && other.room.getPath().equals(this.room.getPath());
		}
		
		@Override
		public int hashCode() {
			return gw.getId().hashCode() * 13 + room.getPath().hashCode() * 3;
		}
		
	}

}
