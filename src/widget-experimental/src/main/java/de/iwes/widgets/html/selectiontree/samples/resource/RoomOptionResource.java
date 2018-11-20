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

import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.locations.Room;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.RoomOption;
import de.iwes.widgets.html.selectiontree.samples.RoomTypeOption;

// TODO dependency to gateway
public class RoomOptionResource extends RoomOption<Resource> {
	
	private final ResourceAccess ra;
	
	public RoomOptionResource(RoomTypeOption rto, ResourceAccess ra) {
		super(rto);
		this.ra = ra;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List<Resource> getAllRooms() {
		return (List) ra.getResources(Room.class);
	}

	@Override
	protected Resource getRoom(SelectionItem item) {
		final Resource r = ra.getResource(item.id());
		if (!(r instanceof Room)) {
			LoggerFactory.getLogger(getClass()).error("Not a room: " + item.id());
			return null;
		}
		return r;
	}

	@Override
	protected SelectionItem getItem(Resource room) {
		return new ResourceSelectionItem(room, null); // TODO name service
	}

	@Override
	protected int getRoomType(Resource room) {
		if (!(room instanceof Room))
			return -1;
		final Room r = (Room) room;
		if (!r.type().isActive())
			return -1;
		return r.type().getValue();
	}


}
