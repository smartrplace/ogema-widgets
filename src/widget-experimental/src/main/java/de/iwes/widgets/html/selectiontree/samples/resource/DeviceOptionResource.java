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
import java.util.Collections;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.locations.Building;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.BaseDeviceTypeOption;
import de.iwes.widgets.html.selectiontree.samples.DeviceOption;
import de.iwes.widgets.html.selectiontree.samples.RoomOption;
import de.iwes.widgets.html.selectiontree.samples.BaseDeviceTypeOption.DeviceTypeItem;

public class DeviceOptionResource extends DeviceOption<Resource> {
	
	private final ResourceAccess ra;

	public DeviceOptionResource(BaseDeviceTypeOption devTypeOption, RoomOption<Resource> roomOption, ResourceAccess ra) {
		super(devTypeOption, roomOption);
		this.ra = ra;
	}

	@Override
	protected List<Resource> getAllDevices() {
		final List<PhysicalElement> pes = ra.getResources(PhysicalElement.class);
		final List<Resource> actualDevices = new ArrayList<>();
		for (PhysicalElement pe: pes) {
			if (filter(pe))
				actualDevices.add(pe);
		}
		return actualDevices;
	}
	
	protected final static boolean filter(final PhysicalElement pe) {
		return !(pe instanceof Room || pe instanceof BuildingPropertyUnit || pe instanceof Building);
	}

	@Override
	protected Resource getDevice(SelectionItem item) {
		final Resource r = ra.getResource(item.id());
		if (!(r instanceof PhysicalElement)) {
			LoggerFactory.getLogger(getClass()).error("Not a physical element: " + item.id());
			return null;
		}
		return r;
	}

	@Override
	protected Resource getDeviceRoom(Resource device) {
		final Resource roomByPath = ResourceUtils.getDeviceLocationRoom(device);
		return roomByPath != null ? roomByPath.getLocationResource() : null;
	}

	@Override
	protected List<String> getDeviceTypes(Resource device) {
		if (devTypeOption == null)
			return Collections.emptyList();
		final List<String> result = new ArrayList<>();
		for (SelectionItem typeItem : devTypeOption.getOptions(null)) {
			if (((DeviceTypeItem) typeItem).deviceType.isAssignableFrom(device.getResourceType()))
				result.add(((DeviceTypeItem) typeItem).deviceType.getName());
		}
		return result;
	}

	@Override
	protected SelectionItem getItem(Resource device) {
		return new ResourceSelectionItem(device, null);
	}

}
