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

import java.util.Map;

import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

import de.iwes.sim.roominside.person.SimulatedUser;
import de.iwes.sim.roominside.person.UserSimConfigPattern;

public class AverageTimeInRoom implements SimulationComplexConfiguration {
	
	//K
	private final TimeResource averageTimeInRoom;
	private final Map<String,SimulatedUser> simulatedObjects;
	private final UserSimConfigPattern configPattern;
	
	// value must be positive (in K)
	
	public AverageTimeInRoom(TimeResource averageTimeInRoom, Map<String,SimulatedUser> simulatedObjects,
			UserSimConfigPattern configPattern) {
		this.averageTimeInRoom = averageTimeInRoom;
		this.simulatedObjects = simulatedObjects;
		this.configPattern = configPattern;
	}

	@Override
	public String getId() {
		return "Average time of user in room";
	}

	@Override
	public String getDescription() {
		return "Average time a user stays in one room before moving to another room or out of the building";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}

	@Override
	public String getValue() {
		return String.format("%d min", averageTimeInRoom.getValue()/60000);
	}

	@Override
	public boolean setValue(String value) {
		value = value.replaceAll("[^\\d.,-]", "");
		try {
			long val = Long.parseLong(value);
			if(val < 0)
				return false;
			boolean result = averageTimeInRoom.setValue(val*60000);
			SimulatedUser simUser = simulatedObjects.get(configPattern.target.getLocation());
			if(simUser != null) {
				simUser.reCalculateProbility();
			}
			return result;
		} catch (NumberFormatException e) {
			return false;
		}
	}	

}
