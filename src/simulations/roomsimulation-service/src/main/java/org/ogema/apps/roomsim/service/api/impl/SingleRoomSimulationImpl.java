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
package org.ogema.apps.roomsim.service.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.ogema.apps.roomsim.service.api.RoomInsideSimulation;
import org.ogema.apps.roomsim.service.api.RoomSimConfig;
import org.ogema.apps.roomsim.service.api.RoomSimConfigPattern;
import org.ogema.apps.roomsim.service.api.SingleRoomSimulation;
import org.ogema.apps.roomsim.service.api.util.SingleRoomSimulationBaseImpl;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.model.locations.Room;

import de.iwes.sim.roomsimservice.device.RoomsimulationServicePattern;

/**An instance per room has to be held. Note: The simulation is only offered for rooms that are configured
 * by a RoomSimConfig*/
public class SingleRoomSimulationImpl extends SingleRoomSimulationBaseImpl implements SingleRoomSimulation {
	public SingleRoomSimulationImpl(RoomsimulationServicePattern roomPattern,
			RoomSimConfigPattern configPattern, OgemaLogger logger) {
		super(roomPattern.model, configPattern, logger);
		this.roomPattern = roomPattern;
		this.configPattern = configPattern;
	}

	private RoomsimulationServicePattern roomPattern;
	private RoomSimConfigPattern configPattern;
	
	@Override
	public boolean unregisterInsideRoomComponent(RoomInsideSimulation<?> component) {
		component.close();
		return insideComponents.remove(component);
	}
	
	@Override
	public List<RoomInsideSimulation<?>> getConnectedSimulations() {
		List<RoomInsideSimulation<?>> res = new ArrayList<>();
		for(RoomInsideSimulationData ridata: insideComponents) {
			if(!(ridata.pattern instanceof RoomInsideSimulation))
				continue;
			res.add((RoomInsideSimulation<?>) ridata.pattern);
		}
		return res;
	}


	@Override
	public float getVolume() {
		return roomPattern.volume.getValue();
	}

	@Override
	public Room getRoom() {
		return roomPattern.model;
	}

	@Override
	public RoomSimConfig getConfigResource() {
		return configPattern.model;
	}
}
