/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package de.iwes.widgets.html.selectiontree.samples.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.tools.resource.util.LoggingUtils;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;

public class ResourceTimeSeriesOption extends LinkingOption {
	
	// may be null
	private final DeviceOptionResource deviceOption;
	private final ResourceAccess ra;
	public final static SelectionItem logging = new SelectionItemImpl("logdata", "Log data");
	public final static SelectionItem schedules = new SelectionItemImpl("schedule", "Schedule");
	public final static SelectionItem onlinedata = new SelectionItemImpl("onlinedata", "Online data");
	private final static List<SelectionItem> items = Collections.unmodifiableList(Arrays.<SelectionItem> asList(logging, schedules, onlinedata));

	public ResourceTimeSeriesOption(DeviceOptionResource deviceOption, ResourceAccess ra) {
		this.deviceOption = deviceOption;
		this.ra = ra;
	}
	
	@Override
	public String id() {
		return "resource_time_series";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Select a time series type";
	}

	@Override
	public LinkingOption[] dependencies() {
		return deviceOption != null ? new LinkingOption[]{deviceOption} : null;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
		if (deviceOption == null || dependencies == null || dependencies.isEmpty() || dependencies.iterator().next().isEmpty())
			return items;
		boolean schedulesEnabled = false;
		boolean loggingEnabled = false;
		boolean onlinedataEnabled = false;
		for (SelectionItem si : dependencies.iterator().next()) {
			final Resource r = ra.getResource(si.id());
			if (r == null)
				continue;
			if (!loggingEnabled) {
				for (SingleValueResource svr : r.getSubResources(SingleValueResource.class, true)) {
					onlinedataEnabled = true;
					if (LoggingUtils.isLoggingEnabled(svr)) { 
						loggingEnabled = true;
						break;
					}
				}
			}
			if (!schedulesEnabled && !r.getSubResources(Schedule.class, true).isEmpty())
				schedulesEnabled = true;
			if (schedulesEnabled && loggingEnabled) // then also onlinedata must be true
				return items;
		}
		final List<SelectionItem> items = new ArrayList<>();
		if (loggingEnabled)
			items.add(logging);
		if (schedulesEnabled)
			items.add(schedules);
		if (onlinedataEnabled)
			items.add(onlinedata);
		return items;
	}

	
	
}
