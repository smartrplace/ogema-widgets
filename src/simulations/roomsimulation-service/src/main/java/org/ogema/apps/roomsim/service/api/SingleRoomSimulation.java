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
import org.ogema.simulation.shared.api.SingleRoomSimulationBase;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;

/** Interface for a room simulation acting as a base for dependent simulations of devices that are inside
 * the room or otherwise attached to it
 * @author dnestle
 *
 */
public interface SingleRoomSimulation extends SingleRoomSimulationBase {
	/**Get reference to the relevant resource*/
	@Override
	Room getRoom();
	
	/**Get configuration resource for the room simulation*/
	RoomSimConfig getConfigResource();
	
	/**Get all room-inside-simulations connected to this SingleRoomSimulation*/
	@Override
	List<RoomInsideSimulation<?>> getConnectedSimulations();
	
	@Override
	SimulationConfiguration getConfigurationById(String simulationConfiguration);
	
	/** Unregister component. Note that registration of components is done via {@link RoomSimulationService}
	 * 
	 * @param component
	 * @return true if the component was found as registered
	 */
	boolean unregisterInsideRoomComponent(RoomInsideSimulation<?> component);
}
