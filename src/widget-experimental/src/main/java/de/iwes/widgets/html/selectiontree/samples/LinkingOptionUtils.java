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
import java.util.List;

import de.iwes.widgets.html.selectiontree.SelectionItem;

public class LinkingOptionUtils {
	
	public static <T> List<T> getRooms(final RoomOption<T> roomOption, final List<Collection<SelectionItem>> dependencies, int roomIdx) {
		if (roomOption == null) 
			return null;
		if (dependencies == null || dependencies.isEmpty() || dependencies.size() < roomIdx+1) {
			return roomOption.selectionRequired() ? Collections.<T> emptyList() : null;
		}
		final Collection<SelectionItem> roomItems = dependencies.get(roomIdx);
		if (roomItems.isEmpty()) { 
			if (roomOption.selectionRequired())
				return Collections.emptyList();
			else 
				return null;
		} else {
			final List<T> roomsLocal = new ArrayList<>();
			for (SelectionItem item: roomItems) {
				final T room = roomOption.getRoom(item);
				if (room != null)
					roomsLocal.add(room);
			}
			return roomsLocal.isEmpty() && !roomOption.selectionRequired() ? null : roomsLocal;
		}
	}
	
	/**
	 * Returns null if no filtering according to rooms shall take place 
	 * (e.g. if the roomOption parameter is null)
	 * @param deviceOption
	 * @param dependencies
	 * @param devicesIdx
	 * 		index of deviceOption in dependencies list
	 * @return
	 */
	public static <T> List<T> getDevices(final DeviceOption<T> deviceOption, final List<Collection<SelectionItem>> dependencies, int devicesIdx) {
		if (deviceOption == null) 
			return null;
		if (dependencies == null || dependencies.isEmpty() || dependencies.size() < devicesIdx+1) {
			return deviceOption.selectionRequired() ? Collections.<T> emptyList() : null;
		}
		final Collection<SelectionItem> roomItems = dependencies.get(devicesIdx);
		if (roomItems.isEmpty()) { 
			if (deviceOption.selectionRequired())
				return Collections.emptyList();
			else 
				return null;
		} else {
			final List<T> devicesLocal = new ArrayList<>();
			for (SelectionItem item: roomItems) {
				final T room = deviceOption.getDevice(item);
				if (room != null)
					devicesLocal.add(room);
			}
			return devicesLocal.isEmpty() && !deviceOption.selectionRequired() ? null : devicesLocal;
		}
	}

}
