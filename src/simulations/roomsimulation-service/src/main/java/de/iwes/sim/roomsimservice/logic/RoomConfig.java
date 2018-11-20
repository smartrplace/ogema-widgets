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
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.sim.roomsimservice.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.apps.roomsim.service.api.RoomSimConfigPattern;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.locations.Room;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

import de.iwes.sim.roomsimservice.device.RoomsimulationServiceSimulation;
import de.iwes.util.format.NameFinder;

public class RoomConfig implements SimulationComplexConfiguration {
	RoomSimConfigPattern target;
	//final RoomSimulationService roomSimProvider;
	final ResourceAccess resAcc;
	final RoomsimulationServiceSimulation roomSimService;
	
	/** Construcutor
	 * 
	 * @param roomSimulationToConnect path to the resource inside the simulation configuration resource.
	 *      The location of the current room selected is NOT sufficient
	 * @param roomSimProvider
	 */
	public RoomConfig(RoomSimConfigPattern roomSimulationToConnect, ResourceAccess resAcc, RoomsimulationServiceSimulation rommSimService) { 
		this.target = roomSimulationToConnect;
		//this.roomSimProvider = roomSimProvider;
		this.resAcc = resAcc;
		this.roomSimService = rommSimService;
	}

	@Override
	public String getId() {
		return "Room";
	}

	@Override
	public String getDescription() {
		return "Room to simulate";
	}

	@Override
	public Map<String, String> getOptions() {
		Map<String, String> res = new HashMap<>();
		for(Room r: getRooms()) {
			res.put(NameFinder.getUniqueID(r), ResourceUtils.getHumanReadableName(r)); 
		}
		return res;
	}

	@Override
	public String getValue() {
		if (!target.target.isActive()) return "";
		return NameFinder.getUniqueID(target.target);
	}

	@Override
	public boolean setValue(String value) {
		for(Room r: getRooms()) {
			if(NameFinder.getUniqueID(r).equals(value)) {
				if(roomSimService.isSimulationActive(target)) {
					roomSimService.deactivateSimulation(target);
				}
				target.target.setAsReference(r);
				//Note that activation will be done automatically if possible via patternAvailable
				//if(active) roomSimService.activateSimulation(target);
				return true;
			}
		}
		return false;
	}
	
	private List<Room> getRooms() {
		return resAcc.getResources(Room.class);
	}
}
