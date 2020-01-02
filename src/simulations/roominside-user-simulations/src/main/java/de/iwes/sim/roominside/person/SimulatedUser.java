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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.model.locations.Room;
import org.ogema.model.user.NaturalPerson;
import org.ogema.tools.simulation.service.api.SimulationProvider;

import de.iwes.roominside.person.logic.ProgramEnter4Thermostat;
import de.iwes.sim.tools.SimUtils;

/**
 * Simulation code for PV plant described by a {@link PVPattern}.
 *
 * @author David Nestle, Fraunhofer IWES
 */
public class SimulatedUser  {
	private final Map<String, Set<NaturalPerson>> addRequests;

    public final ApplicationManager appMan;
    public final OgemaLogger logger;
    public final UserSimConfigPattern configPattern;

    //private final Map<String, SimulatedPersonsInRoom> simulatedRooms;
    private final UserSimulation mainSimulation;
    private final RoomSimulationService roomSimService;
    
    private float probabilityOfLeavingRoomPerMilli;
    private float probabilityOfGoingFromRoomToRoom;
    private float probabilityOfComingBackToBuildingPerMilli;
    public List<Room> roomsToUse;
    
    public ProgramEnter4Thermostat programEnter4Thermostat = null;
    
    public void reCalculateProbility() {
    	if((configPattern.useOnlySimulatedRooms.isActive()&&
    			configPattern.useOnlySimulatedRooms.getValue())) {
    		throw new UnsupportedOperationException("Rooms not in simulation are currently not supported!");
    	}
    	roomsToUse = roomSimService.getAvailableRooms();
    	probabilityOfLeavingRoomPerMilli = 1.0f/configPattern.averageTimeInRoom.getValue();
    	if(roomsToUse.size() < 2) {
    		probabilityOfGoingFromRoomToRoom = 0;
    	} else {
        	//definition
    		probabilityOfGoingFromRoomToRoom = 0.5f*(1.0f-configPattern.probabilityOutsideBuilding.getValue());
    	}
    	long averageTimeInBuilding = (long) (configPattern.averageTimeInRoom.getValue()*SimUtils.infiniteProbabilityAggregation(probabilityOfGoingFromRoomToRoom));
    	float probOut = configPattern.probabilityOutsideBuilding.getValue();
    	long averageTimeOutsideBuilding = (long) (probOut*averageTimeInBuilding/(1-probOut));
    	probabilityOfComingBackToBuildingPerMilli = 1.0f/averageTimeOutsideBuilding;
System.out.println("Recalculated Probabilites for user "+configPattern.target.getLocation()+" found rooms:"+roomsToUse.size());
    }
    
	public SimulatedUser(ApplicationManager appMan, UserSimulation mainSimulation,
    		UserSimConfigPattern configPattern,
    		RoomSimulationService roomSimService, SimulationProvider<NaturalPerson> provider,
    		Map<String, Set<NaturalPerson>> addRequests) {
        this.appMan = appMan;
        this.logger = appMan.getLogger();
        this.mainSimulation = mainSimulation;
        this.configPattern = configPattern;
        this.roomSimService = roomSimService;
        this.addRequests = addRequests;
        
        shiftToRoom(configPattern.currentPosition.getValue());
        reCalculateProbility();
    }

	public void step(long stepSize) {
		if(configPattern.currentProgram.getValue().equals("Manual")) return;
		if(configPattern.currentProgram.getValue().equals("EnterRoom4Thermostat")) {
			programEnter4Thermostat.enter4thermostatStep();
		}
		if(configPattern.currentPosition.getValue().equals("outside")) {
			if(SimUtils.performRandomTest(probabilityOfComingBackToBuildingPerMilli, stepSize)) {
				if(roomsToUse.isEmpty()) mainSimulation.getRoomConnector(null); //we just want to trigger a check
				if(!roomsToUse.isEmpty()) {
					reCalculateProbility();
					findNewRoom(null);
				} else {
					//do nothing, we cannot go inside as there are no rooms
				}
			}
		} else {
			if(SimUtils.performRandomTest(probabilityOfLeavingRoomPerMilli, stepSize)) {
				leaveRoom();
			}
		}
	}
	
	/**Call this if user is in a room and shall go out of this room into another room or
	 * into outside	 */
	public void leaveRoom() {
		if(SimUtils.performRandomTest(probabilityOfGoingFromRoomToRoom)) {
			findNewRoom(configPattern.currentPosition.getValue());
		} else {
			shiftToRoom("outside");
		}
	}
	
	/**Call this if user shall go into a room, but not into outside. If the user is
	 * currently in the room this room may be excluded as option*/
	public void findNewRoom(String excluded) {
		Room r;
		int count = 0;
		do {
			int ridx = (int) (roomsToUse.size()*Math.random());
			r = roomsToUse.get(ridx);
			count ++;
			if(count > 100) {
				throw new IllegalStateException("excluded:"+excluded);
			}
		} while (r.getLocation().equals(excluded));
		shiftToRoom(r.getLocation());
	}
	
	public void shiftToRoom(String newLocation) {
		if(!configPattern.currentPosition.getValue().equals("outside")) {
			SimulatedPersonsInRoom i = mainSimulation.getRoomConnector(configPattern.currentPosition.getValue());
			if(i == null) {
				addRequestList(newLocation).remove(configPattern.model.target());
			} else {
				i.removePerson(configPattern.model.target());			
			}
		}
		if(!newLocation.equals("outside")) {
			SimulatedPersonsInRoom i =  mainSimulation.getRoomConnector(newLocation);
			if(i == null) {
				addRequestList(newLocation).add(configPattern.model.target());
			} else {
				i.addPerson(configPattern.model.target());
			}
		}		
		configPattern.currentPosition.setValue(newLocation);
	}
	
	private Set<NaturalPerson> addRequestList(String roomLocation) {
		Set<NaturalPerson> res = addRequests.get(roomLocation);
		if(res == null) {
			res = new HashSet<>();
			addRequests.put(roomLocation, res);
		}
		return res;
	}
	
	public SimulatedPersonsInRoom getCurrentLocationRoomConnection() {
		return  mainSimulation.getRoomConnector(configPattern.currentPosition.getValue());
	}
}
