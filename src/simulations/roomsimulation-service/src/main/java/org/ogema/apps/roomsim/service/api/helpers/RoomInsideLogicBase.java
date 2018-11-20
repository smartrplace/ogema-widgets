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

import org.ogema.apps.roomsim.service.api.RoomInsideSimulation;
import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.apps.roomsim.service.api.SingleRoomSimulation;
import org.ogema.core.model.Resource;
import org.ogema.model.locations.Room;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.apiplus.SimulationPattern;

import de.iwes.util.linkingresource.RoomHelper;

/** Class that can be extended to utilize creation of a simulation connecting to the
 * Room Simulation Service. This derived class is intended to be instantiated once per
 * device wheres the SimBase is the base class for the simulation that is only instantiated
 * once per simulation type.
 * @author dnestle
 *
 */
public abstract class RoomInsideLogicBase<T extends Resource> implements RoomInsideSimulation<T> {
	public RoomInsideLogicBase(RoomSimulationService roomSimService, SimulationProvider<T> provider,
			T parentSimulatedResource, Room roomSimulationToConnect, SimulationPattern<?> simPattern) {
		this.roomSimService = roomSimService;
		this.provider = provider;
		this.model = parentSimulatedResource;
		this.roomSimulationToConnect = roomSimulationToConnect;
		this.simPattern = simPattern;
		init();
	}

	public RoomInsideLogicBase(RoomSimulationService roomSimService, SimulationProvider<T> provider,
			T parentSimulatedResource, Room roomSimulationToConnect) {
		super();
		this.roomSimService = roomSimService;
		this.provider = provider;
		this.model = parentSimulatedResource;
		this.roomSimulationToConnect = roomSimulationToConnect;
		this.simPattern = null;
		init();
	}
	
	protected final SimulationProvider<T> provider;
	protected final RoomSimulationService roomSimService;
	protected final T model;
	/**Provide information to which room the pattern belongs, usually by a reference inside location.room
	 * of the pattern itself or a super resource found by {@link RoomHelper.getResourceLocationRoom}
	 * The pattern simulation is not started until the room is available, but if the room is found via a
	 * pattern path it should be set optional so that the pattern is shown as element to be configured
	 * e.g. in the simulation GUI 
	 */
	protected final Room roomSimulationToConnect;
	protected final SimulationPattern<?> simPattern;
	
	public SingleRoomSimulation roomSim;

	/** for provider-less simulations may be null*/
	public SimulationProvider<T> getProvider() {
		return provider;
	}
	
	public void activate() {
		if(roomSim == null) {
			if(roomSimulationToConnect != null &&  roomSimulationToConnect.exists()) {
				roomSimService.registerRoomSimulation( roomSimulationToConnect, this);
				//inject.roomSimulationProvider().registerRoomSimulation(r, this);
				//Room may not yet be available, so we have wait for the listener
				model.activate(true);
			}
		}
	}
	
	public void deactivate() {
		if(roomSim != null) {
			roomSim.unregisterInsideRoomComponent(this);
			roomSim = null;
			if(simPattern != null) simPattern.active = false;
		}
		//this.active = false;
		System.out.println("  Deactivated simulation pattern " + this);
		//model.deactivate(true);
	}
	
	public boolean isSimulationActive() {
		return (roomSim != null);
	}
	
	@Override
	public String toString() {
		return "model: " + model.getLocation() + ", type: " + getClass().getName();
	}
	
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
	
	public synchronized boolean init() {
		while(synch) try {
			System.out.println("RoomInsidePattern:init:sleep:"+model.getLocation());		
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synch = true;
		if(roomSim != null) {
			System.out.println("RoomInsidePattern:init:deactivate");		
			deactivate();
		}
		activate();

		synch = false;
		return true;
	}
	
	public boolean simulationAvailable(SingleRoomSimulation singleRoomSimulation) {
//		System.out.println("simulationAvailable "+singleRoomSimulation.getVolume()+" for "+provider.getProviderId());			

		roomSim = singleRoomSimulation;
		if(simPattern != null) simPattern.active = true;
		//this.active = true;
		return true;
	}
	
	public SimulationPattern<?> getSimulationPattern() {
		return simPattern;
	}
}
