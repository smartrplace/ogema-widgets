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
package de.iwes.sim.roominside.person;

import java.util.Set;

import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.apps.roomsim.service.api.helpers.RoomInsideLogicBase;
import org.ogema.core.application.ApplicationManager;
import org.ogema.model.locations.Room;
import org.ogema.model.user.NaturalPerson;
import org.ogema.tools.simulation.service.api.SimulationProvider;

/**
 * Simulation code for PV plant described by a {@link PVPattern}.
 *
 * @author David Nestle, Fraunhofer IWES
 */
public class SimulatedPersonsInRoom extends RoomInsideLogicBase<Room> {

    //private final ApplicationManager appMan;
    //private final OgemaLogger logger;

    public SimulatedPersonsInRoom(ApplicationManager appMan, Room r,
    		RoomSimulationService roomSimService, SimulationProvider<Room> provider,
    		Set<NaturalPerson> addRequests) {
    	super(roomSimService, provider, r, r);
        //this.appMan = appMan;
        //this.logger = appMan.getLogger();
        
        if(addRequests != null) for(NaturalPerson np: addRequests) {
        	addPerson(np);
        }
    }

	@Override
	public void step(long stepSize) {}

	@Override
	public void close() {
		// nothing to do here
	}
	
	public void addPerson(NaturalPerson user) {
		roomSim.addPerson(user);
	}
	public void removePerson(NaturalPerson user) {
		roomSim.removePerson(user);
	}
}
