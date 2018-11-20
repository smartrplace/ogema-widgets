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
