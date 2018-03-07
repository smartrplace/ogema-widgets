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
package de.iwes.widgets.html.selectiontree.samples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

// TODO gateway dependency
public abstract class DeviceOption<T> extends LinkingOption {

	protected final BaseDeviceTypeOption devTypeOption;
	private final RoomOption<T> roomOption;
	private final LinkingOption[] dependencies;
	
	public DeviceOption(BaseDeviceTypeOption devTypeOption, RoomOption<T> roomOption) {
		this.devTypeOption = devTypeOption;
		this.roomOption = roomOption;
		this.dependencies = createDependenciesArray(roomOption, devTypeOption);
	}
	
	protected abstract List<T> getAllDevices();
	protected abstract T getDevice(SelectionItem item);
	protected abstract T getDeviceRoom(T device);
	protected abstract List<String> getDeviceTypes(T device);
	protected abstract SelectionItem getItem(T device);
	
	@Override
	public String id() {
		return "device";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Select a device";
	}

	@Override
	public LinkingOption[] dependencies() {
		return dependencies.clone();
	}
	
	@Override
	public List<SelectionItem> getOptions(final List<Collection<SelectionItem>> dependencies) {
		final List<T> rooms = LinkingOptionUtils.getRooms(roomOption, dependencies, 0);
		if (rooms != null && rooms.isEmpty())
			return Collections.emptyList();
		final List<String> deviceTypes = getDeviceTypes(dependencies);
		final List<T> devices = getAllDevices();
		final List<SelectionItem> items = new ArrayList<>();
		// filter according to room and device type
		if (dependencies != null && !dependencies.isEmpty()) {
			final Iterator<T> it = devices.iterator();
			while (it.hasNext()) {
				final T dev = it.next();
				if (rooms != null) {
					T r = getDeviceRoom(dev);
					if (r == null || !rooms.contains(r)) {
						it.remove();
						continue;
					}
				}
				if (deviceTypes != null) {
					if (!containsOneItem(deviceTypes, getDeviceTypes(dev))) {
						it.remove();
						continue;
					}
				}
				items.add(getItem(dev));
			}
		}
		return items;
	}
	
	private static final boolean containsOneItem(List<String> target, List<String> actual) {
		if (actual == null || actual.isEmpty())
			return false;
		for (String s : actual) {
			if (target.contains(s))
				return true;
		}
		return false;
	}
	
	
	private final List<String> getDeviceTypes(final List<Collection<SelectionItem>> dependencies) {
		if (devTypeOption == null || dependencies == null || dependencies.isEmpty()) 
			return null;
		final Collection<SelectionItem> devTypesItems = dependencies.get(dependencies.size()-1);
		if (devTypesItems.isEmpty())  
			return null;
		final List<String> devTypes = new ArrayList<>();
		for (SelectionItem item: devTypesItems) {
			devTypes.add(item.id());
		}
		return devTypes;
	}

}
