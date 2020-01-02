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
package de.iwes.roominside.person.logic;

import java.util.HashMap;
import java.util.Map;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.locations.Room;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

import de.iwes.sim.roominside.person.SimulatedUser;
import de.iwes.sim.roominside.person.UserSimConfigPattern;

public class User2Room implements SimulationComplexConfiguration {
	
	//K
	private final StringResource currentPosition;
	private final Map<String,SimulatedUser> simulatedObjects;
	private final UserSimConfigPattern configPattern;
	
	// value must be positive (in K)
	
	public User2Room(StringResource currentPosition, Map<String,SimulatedUser> simulatedObjects,
			UserSimConfigPattern configPattern) {
		this.currentPosition = currentPosition;
		this.simulatedObjects = simulatedObjects;
		this.configPattern = configPattern;
	}

	@Override
	public String getId() {
		return "Current room of user";
	}

	@Override
	public String getDescription() {
		return "Current room in which user is situated";
	}

	@Override
	public Map<String,String> getOptions() {
		Map<String, String> res = new HashMap<>();
		res.put("outside", "outside");
		SimulatedUser simUser = simulatedObjects.get(configPattern.target.getLocation());
		if(simUser != null) for(Room r: simUser.roomsToUse) {
			res.put(r.getLocation(), ResourceUtils.getHumanReadableName(r)); 
		}
		return res;
	}

	@Override
	public String getValue() {
		return currentPosition.getValue();
	}

	@Override
	public boolean setValue(String value) {
		SimulatedUser simUser = simulatedObjects.get(configPattern.target.getLocation());
		if(simUser != null) {
			if(value.equals("outside")) simUser.shiftToRoom("outside");
			for(Room r: simUser.roomsToUse) {
				if(r.getLocation().equals(value)) {
					simUser.shiftToRoom(value);
					if(simUser.configPattern.currentProgram.getValue().equals("EnterRoom4Thermostat")) {
						simUser.programEnter4Thermostat.roomSet();
					}
					return true;
				}
			}
		}
		return false;
	}	

}
