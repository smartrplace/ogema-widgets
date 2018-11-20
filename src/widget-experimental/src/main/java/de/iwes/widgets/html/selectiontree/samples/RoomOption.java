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
import java.util.Iterator;
import java.util.List;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

// TODO dependency to gateway
public abstract class RoomOption<T> extends LinkingOption {
	
	// may be null
	private final RoomTypeOption rto;
	
	public RoomOption(RoomTypeOption rto) {
		this.rto = rto;
	}
	
	protected abstract List<T> getAllRooms();
	protected abstract T getRoom(SelectionItem item);
	protected abstract SelectionItem getItem(T room);
	protected abstract int getRoomType(T room);
	
	@Override
	public String id() {
		return "room_option";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Select a room";
	}

	@Override
	public LinkingOption[] dependencies() {
		return rto != null ? new LinkingOption[]{rto} : null;
	}
	
	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
		final List<Integer> roomTypes = getRoomTypes(dependencies);
		final List<T> rooms = getAllRooms();
		final Iterator<T> it = rooms.iterator();
		final List<SelectionItem> items = new ArrayList<>();
		while (it.hasNext()) {
			final T room = it.next();
			if (roomTypes != null) {
				int type = getRoomType(room);
				if (!roomTypes.contains(Integer.valueOf(type))) {
					it.remove();
					continue;
				}
			}
			items.add(getItem(room));
		}
		return items;
	}

	private final List<Integer> getRoomTypes(final List<Collection<SelectionItem>> dependencies) {
		if (rto == null || dependencies == null || dependencies.isEmpty()) 
			return null;
		final Collection<SelectionItem> roomTypeItems = dependencies.iterator().next();
		if (roomTypeItems.isEmpty())  
			return null;
		final List<Integer> roomTypes = new ArrayList<>();
		for (SelectionItem item: roomTypeItems) {
			try {
				roomTypes.add(Integer.parseInt(item.id()));
			} catch (NumberFormatException e) {}
		}
		return roomTypes;
	}
	
}
