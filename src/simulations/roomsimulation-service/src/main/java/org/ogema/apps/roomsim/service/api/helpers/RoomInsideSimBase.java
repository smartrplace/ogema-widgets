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
package org.ogema.apps.roomsim.service.api.helpers;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.simulation.service.apiplus.SimulationBase;

import de.iwes.sim.roomsimservice.device.RoomInsideConfigPattern;

public abstract class RoomInsideSimBase<T extends RoomInsideConfigPattern<?>, R extends ResourcePattern<?>>
		extends SimulationBase<T, R> {

	protected abstract RoomInsideLogicBase<?> getSimulatedObjects(String simLocation);

	public RoomInsideSimBase(ApplicationManager am, Class<R> targetPatternClass,
			boolean useIntervalListener, Class<T> simPatternClass) {
		super(am, targetPatternClass, useIntervalListener, simPatternClass);
	}

	@Override
	public void activateSimulation(T configPattern) {
		if(!configPattern.roomSimulationToConnect.exists()) return;
		R targetPattern = simulatedDevices.get(configPattern);
		RoomInsideLogicBase<?> base = getSimulatedObjects(targetPattern.model.getLocation());
		if(base == null) {
			initSimulation(simulatedDevices.get(configPattern), configPattern);
		}
	}

	@Override
	public boolean isSimulationActivatable(String deviceLocation) {
		T configPattern = getSimPattern(deviceLocation);
		return configPattern.roomSimulationToConnect.exists();
		/*
		R targetPattern = simulatedDevices.get(configPattern);
		RoomInsideLogicBase<?> base = getSimulatedObjects(targetPattern.model.getLocation());
		if(base == null) {
			return false;
		} else {
			return true;
		}*/
	}
	
	public boolean setRoom(T configPattern, Room newRoom) {
		if(!configPattern.roomSimulationToConnect.exists()) return false;
		R targetPattern = simulatedDevices.get(configPattern);
		if(targetPattern.model instanceof PhysicalElement) {
			Room dest = ((PhysicalElement) targetPattern.model).location().room();
			dest.setAsReference(newRoom);
			return true;
		}
		return false;
	}
}
