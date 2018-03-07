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
