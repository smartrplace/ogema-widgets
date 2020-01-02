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
package de.iwes.roominside.person.logic;

import java.util.HashMap;
import java.util.Map;

import org.ogema.apps.roomsim.service.api.SingleRoomSimulation;
import org.ogema.core.application.ApplicationManager;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;

import de.iwes.sim.roominside.person.SimulatedUser;
import de.iwes.sim.roominside.person.UserSimConfigPattern;

public class ProgramEnter4Thermostat {
	private final UserSimConfigPattern configPattern;
	private final SimulatedUser simUser;
	private final SingleRoomSimulation roomSim;
	private final ApplicationManager appMan;
	
    public final Map<String, Long> enterRoom4Thermostat = new HashMap<String, Long>();

	public ProgramEnter4Thermostat(UserSimConfigPattern configPattern, SimulatedUser simUser,
			SingleRoomSimulation roomSim, ApplicationManager appMan) {
		this.configPattern = configPattern;
		this.simUser = simUser;
		this.roomSim = roomSim;
		this.appMan = appMan;

		enterRoom4Thermostat.put("enter room", 10000l);
        enterRoom4Thermostat.put("leave room", 10000l);
	}

	public void enter4thermostatStep() {
		switch(configPattern.programStep.getValue()) {
		case "waiting for room selection":
			return;
		case "enter room":
			long endTime = configPattern.currentProgramStepStartTime.getValue() +
					enterRoom4Thermostat.get("enter room");
			if(appMan.getFrameworkTime() >  + endTime) {
				//set thermostat
				changeThermostatSetpointByEmotionalComfortUser();
				configPattern.programStep.setValue("leave room");
				configPattern.currentProgramStepStartTime.setValue(appMan.getFrameworkTime());
			}
			return;
		case "leave room":
			endTime = configPattern.currentProgramStepStartTime.getValue() +
					enterRoom4Thermostat.get("leave room");
			if(appMan.getFrameworkTime() >  + endTime) {
				simUser.leaveRoom();
				configPattern.programStep.setValue("leave room");
				configPattern.currentProgramStepStartTime.setValue(appMan.getFrameworkTime());
			}
			return;
		default:
			throw new IllegalStateException("no valid program step for program EnterRoom4Thermostat");
		}
	}
	
	public void changeThermostatSetpointByEmotionalComfortUser() {
		SimulationConfiguration sc = roomSim.getConfigurationById("Thermostat setpoint");
		if(sc != null) {
			if(roomSim.getTemperature().getCelsius() > 22f) {
				((SimulationComplexConfiguration) sc).setValue("19.0");			
			} else {
				((SimulationComplexConfiguration) sc).setValue("26.0");							
			}
		}
	}

	public void roomSet() {
		simUser.configPattern.currentProgramStepStartTime.setValue(simUser.appMan.getFrameworkTime());
		simUser.configPattern.programStep.setValue("enter room");		
	}
}
