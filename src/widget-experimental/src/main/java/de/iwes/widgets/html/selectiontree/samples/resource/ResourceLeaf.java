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
package de.iwes.widgets.html.selectiontree.samples.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.locations.Room;
import org.ogema.tools.resource.util.LoggingUtils;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.html.selectiontree.samples.LinkingOptionUtils;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;
import de.iwes.widgets.resource.timeseries.TimeSeriesDataType;

public class ResourceLeaf extends TerminalOption<ReadOnlyTimeSeries> {

	// TODO need another prefix for Online but non-blocking?
	public final static String LOG_PREFIX = "Log";
	public final static String SCHEDULE_PREFIX = "Schedule";
	public final static String ONLINE_PREFIX = "Online";
	private final NameService nameService;
	private final ResourceAccess ra;
	private final OnlineTimeSeriesCache onlineTSCache;
	// only depends on room if no device is selected
	private final RoomOptionResource roomOption;
	private final DeviceOptionResource deviceOption; 
	private final ResourceTimeSeriesOption timeSeriesOption;
	private final LinkingOption[] dependencies;
	private final int roomIdx;
	private final int deviceIdx;
	private final int timeSeriesIdx;
	
	public ResourceLeaf(final RoomOptionResource roomOption, final DeviceOptionResource deviceOption, final ResourceTimeSeriesOption timeSeriesOption,
				final ResourceAccess ra, final NameService nameService, final OnlineTimeSeriesCache onlineTSCache) {
		this.ra = ra;
		this.nameService = nameService; 
		this.onlineTSCache = onlineTSCache;
		this.roomOption = roomOption;
		this.deviceOption = deviceOption;
		this.timeSeriesOption = timeSeriesOption;
		this.dependencies = createDependenciesArray(roomOption,deviceOption,timeSeriesOption);
		int cnt = 0;
		this.roomIdx = roomOption != null ? cnt++ : -1;
		this.deviceIdx = deviceOption != null ? cnt++ : -1;
		this.timeSeriesIdx = timeSeriesOption != null ? cnt++ : -1;
	}
	
	@Override
	public String id() {
		return "resource_leaf";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Select a data source";
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies.clone();
	}

	@Override
	public List<SelectionItem> getOptions(final List<Collection<SelectionItem>> dependencies) {
		if (dependencies == null || (timeSeriesOption != null && dependencies.size() < timeSeriesIdx+1)) // in particular, no time series type can be selected -> this is mandatory
			return Collections.emptyList();
		boolean log = false;
		boolean schedule = false;
		boolean online = false;
		if (timeSeriesOption != null) {
			for (SelectionItem si : dependencies.get(timeSeriesIdx)) {
				if (si.equals(ResourceTimeSeriesOption.logging)) 
					log = true;
				else if (si.equals(ResourceTimeSeriesOption.schedules))
					schedule = true;
				else if (si.equals(ResourceTimeSeriesOption.onlinedata))
					online = true;
			}
		} else {
			log = true;
			schedule = true;
			online = true;
		}
		if (!log && !schedule && !online)
			return Collections.emptyList(); 
		final List<Resource> devices = LinkingOptionUtils.getDevices(deviceOption, dependencies, deviceIdx);
		final List<Resource> rooms;
		final List<SelectionItem> items = new ArrayList<>();
		if (devices == null) 
			rooms = LinkingOptionUtils.getRooms(roomOption, dependencies, roomIdx);
		else
			rooms = null;
		if (rooms != null && !rooms.isEmpty()) 
			addValuesPerRoom(ra, rooms, items, nameService, log, schedule, online, onlineTSCache);
		else if (devices != null && !devices.isEmpty()) 
			addValuesPerDevice(devices, items, nameService, log, schedule, online, onlineTSCache);
		else
			addAllValues(ra, items, nameService, log, schedule, online, onlineTSCache);
		return items;
	}
	
	private final static void addValuesPerRoom(final ResourceAccess ra, final List<Resource> rooms, final List<SelectionItem> items, final NameService nameService,
			final boolean log, final boolean schedule, final boolean online, OnlineTimeSeriesCache onlineTSCache) {
		if (log || online) {
			for (SingleValueResource svr : ra.getResources(SingleValueResource.class)) {
				if (!svr.isActive() || svr instanceof StringResource || !roomMatches(svr, rooms))
					continue;
				if (log && LoggingUtils.isLoggingEnabled(svr)) {
					items.add(getSelectionItem(svr, nameService, LOG_PREFIX, null));
				}
				if (online)
					items.add(getSelectionItem(svr, nameService, ONLINE_PREFIX,  onlineTSCache));
			}
		}
		if (schedule) {
			for (Schedule sched : ra.getResources(Schedule.class)) {
				if (sched.isActive() && roomMatches(sched, rooms))
					items.add(getSelectionItem(sched, nameService, SCHEDULE_PREFIX, null));
			}
		}
	}
	
	private final static boolean roomMatches(final Resource target, final List<Resource> targets) {
		final Room location = ResourceUtils.getDeviceLocationRoom(target);
		if (location == null)
			return false;
		for (Resource r : targets) {
			if (location.equalsLocation(r))
				return true;
		}
		return false;
	}
	
	private final static void addAllValues(final ResourceAccess ra, final List<SelectionItem> items, final NameService nameService,
			final boolean log, final boolean schedule, final boolean online, OnlineTimeSeriesCache onlineTSCache) {
		if (log || online) {
			for (SingleValueResource svr : ra.getResources(SingleValueResource.class)) {
				if (!svr.isActive() || svr instanceof StringResource)
					continue;
				if (log && LoggingUtils.isLoggingEnabled(svr)) {
					items.add(getSelectionItem(svr, nameService, LOG_PREFIX, null));
				}
				if (online)
					items.add(getSelectionItem(svr, nameService, ONLINE_PREFIX, onlineTSCache));
			}
		}
		if (schedule) {
			for (Schedule sched : ra.getResources(Schedule.class)) {
				if (sched.isActive())
					items.add(getSelectionItem(sched, nameService, SCHEDULE_PREFIX, null));
			}
		}
	}
	
	private final static void addValuesPerDevice(final List<Resource> devices, final List<SelectionItem> items, final NameService nameService,
				final boolean log, final boolean schedule, final boolean online, OnlineTimeSeriesCache onlineTSCache) {
		for (Resource device : devices) {
			if (log || online) {
				for (SingleValueResource svr : device.getSubResources(SingleValueResource.class, true)) {
					if (!svr.isActive() || svr instanceof StringResource)
						continue;
					if (log && LoggingUtils.isLoggingEnabled(svr)) {
						items.add(getSelectionItem(svr, nameService, LOG_PREFIX, null));
					}
					if (online)
						items.add(getSelectionItem(svr, nameService, ONLINE_PREFIX, onlineTSCache));
				}
			}
			if (schedule) {
				for (Schedule sched : device.getSubResources(Schedule.class, true)) {
					if (sched.isActive())
						items.add(getSelectionItem(sched, nameService, SCHEDULE_PREFIX, null));
				}
			}
			
		}
	}
	
	@Override
	public ReadOnlyTimeSeries getElement(final SelectionItem item) {
		if (!(item instanceof ResourceLeafSelectionItem))
			throw new IllegalArgumentException("Only accepts ResourceLeafSelectionItems, got " + item.getClass().getSimpleName());
		return ((ResourceLeafSelectionItem) item).getTimeSeries();
	}
	
	private final static SelectionItemImpl getSelectionItem(final Resource resource, final NameService nameService, final String typePrefix, OnlineTimeSeriesCache onlineTSCache) {
		final String id = typePrefix + "::" + resource.getPath();
		// currently the getHumanReadableName simply returns the location for the resource; but it may be improved in the future
		final String nameServiceName = nameService != null ? nameService.getName(resource, OgemaLocale.ENGLISH) : null;
		final String label = typePrefix + ": " + (nameServiceName != null ? nameServiceName : ResourceUtils.getHumanReadableName(resource));
		return new ResourceLeafSelectionItem(id, label, resource, typePrefix, onlineTSCache);
	}
	
	public static class ResourceLeafSelectionItem extends SelectionItemImpl {
		
		private final Resource resource;
		private final String typeKey;
		private final TimeSeriesDataType dataType;
		private final OnlineTimeSeriesCache onlineTSCache;

		private ResourceLeafSelectionItem(String id, String label, Resource resource, String typeKey, OnlineTimeSeriesCache onlineTSCache) {
			super(id, label);
			this.resource = resource;
			this.typeKey = typeKey;
			this.onlineTSCache = onlineTSCache;
			this.dataType = typeKey == ONLINE_PREFIX ? TimeSeriesDataType.ONLINE_DATA : TimeSeriesDataType.READ_ONLY_TIME_SERIES;
		}
		
		public Resource getResource() {
			return resource;
		}
		
		public SingleValueResource getSingleValueResource() {
			switch (typeKey) {
			case SCHEDULE_PREFIX:
				return resource.getParent();
			default:
				return (SingleValueResource) resource;
			}
		}
		
		public TimeSeriesDataType getDataType() {
			return dataType;
		}
		
		public ReadOnlyTimeSeries getTimeSeries() {
			switch (typeKey) {
			case LOG_PREFIX:
				return LoggingUtils.getHistoricalData((SingleValueResource) resource);
			case SCHEDULE_PREFIX:
				return (Schedule) resource;
			case ONLINE_PREFIX:
				return onlineTSCache.getResourceValuesAsTimeSeries((SingleValueResource) resource);
			default:
				throw new IllegalStateException("Something went totally wrong...");
			}
		}
		
	}
	
}
