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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.apps.roomsim.service.api;

import java.util.List;

import org.ogema.model.locations.Room;

public interface RoomSimulationService {
	/** Register room-inside simulation component for the room. If the room simulation is not
	 * yet active, start/step/stop calls will follow as soon as it is available based on the
	 * listener implementation of RoomInsidePattern (no need for action if the pattern is used as
	 * base implementation)
	 * @return SingleRoomSimulation object if already available, otherwise null
	 */
	SingleRoomSimulation registerRoomSimulation(Room room, RoomInsideSimulation<?> listener);
	
	/** Stop simulation (stop call once, no further step calls). If the component has not
	 * yet received a listener callback, it is just removed from the waiting list
	 * @return true if the respective simulation was running, false if it was just removed from
	 * the waiting list
	 */
	boolean unregisterRoomSimulation(Room room, RoomInsideSimulation<?> listener);

	/** Get all rooms providing a room simulation to which dependent devices can register
	 */
	List<Room> getAvailableRooms();

	/** Get all rooms providing a room simulation to which dependent devices can register
	 */
	//List<SingleRoomSimulation> getAvailableRoomSimulations();
}
