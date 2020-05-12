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

import org.ogema.apps.roomsim.service.api.RoomInsideSimulation;
import org.ogema.simulation.shared.api.RoomInsideSimulationBase;

/** An instance per external simulation component per room needs to be held*/
public class RoomInsideSimulationData {
	public RoomInsideSimulationData(RoomInsideSimulationBase insideSim) {
		this.pattern = insideSim;
	}
	public RoomInsideSimulationBase pattern;
	public long stepTimeMissed = 0;

	//synchronize init, start, step, stop // -> maybe use a lock instead?
	private boolean synch = false;
	
	public synchronized boolean isBusy() {
		return synch;
	}
	public synchronized boolean requestBusy() {
		if(synch) return false;
		else {
			synch = true;
			return true;
		}
	}
	public synchronized void releaseBusy() {
		synch = false;
	}
	
	@Deprecated
	protected synchronized boolean preInit(RoomInsideSimulation<?> inject) {
		while(synch) try {
			//System.out.println("RoomInsidePattern:init:sleep:"+inject.getRoom().getLocation());		
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synch = true;
		return true;
	}

}
