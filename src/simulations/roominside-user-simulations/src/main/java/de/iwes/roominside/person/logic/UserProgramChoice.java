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
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

import de.iwes.sim.roominside.person.SimulatedPersonsInRoom;
import de.iwes.sim.roominside.person.SimulatedUser;
import de.iwes.sim.roominside.person.UserSimConfigPattern;

public class UserProgramChoice implements SimulationComplexConfiguration {
	
	//K
	private final StringResource currentProgram;
	private final Map<String,SimulatedUser> simulatedObjects;
	private final UserSimConfigPattern configPattern;
	
	// value must be positive (in K)
	
	public UserProgramChoice(StringResource currentProgram, Map<String,SimulatedUser> simulatedObjects,
			UserSimConfigPattern configPattern) {
		this.currentProgram = currentProgram;
		this.simulatedObjects = simulatedObjects;
		this.configPattern = configPattern;
	}

	@Override
	public String getId() {
		return "Current action of user";
	}

	@Override
	public String getDescription() {
		return "Current action user id performing";
	}

	@Override
	public Map<String,String> getOptions() {
		Map<String, String> res = new HashMap<>();
		res.put("Manual", "Manual Control");
		res.put("Random", "Random Moving");
		res.put("EnterRoom4Thermostat", "Enter Room and control Thermostat");
		return res;
	}

	@Override
	public String getValue() {
		return currentProgram.getValue();
	}

	@Override
	public boolean setValue(String value) {
		if(value.equals("Manual")) {
			currentProgram.setValue(value);
			return true;
		} else if(value.equals("Random")) {
			currentProgram.setValue(value);
			return true;
		} else if(value.equals("EnterRoom4Thermostat")) {
			SimulatedUser simUser = simulatedObjects.get(configPattern.target.getLocation());
			boolean result = currentProgram.setValue(value);
			if(simUser != null) {
				SimulatedPersonsInRoom i = simUser.getCurrentLocationRoomConnection();
				if(i != null) {
					simUser.programEnter4Thermostat = new ProgramEnter4Thermostat(simUser.configPattern, simUser,
							i.roomSim, simUser.appMan);
					simUser.configPattern.programStep.setValue("waiting for room selection");
				}
				return result;
			}
		}
		return false;
	}	
}
