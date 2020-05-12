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
package org.ogema.simulation.shared.api;

import java.util.List;
import java.util.Set;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.BrightnessResource;
import org.ogema.core.model.units.ConcentrationResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.locations.Room;
import org.ogema.model.user.NaturalPerson;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;

/** Interface for a room simulation acting as a base for dependent simulations of devices that are inside
 * the room or otherwise attached to it
 * @author dnestle
 *
 */
public interface SingleRoomSimulationBase {
	/**Get reference to the relevant resource*/
	Resource getRoom();
	
	/**Get all room-inside-simulations connected to this SingleRoomSimulation*/
	List<?> getConnectedSimulations();
	
	SimulationConfiguration getConfigurationById(String simulationConfiguration);
	
	/**Get (average) room temperature*/
	TemperatureResource getTemperature();
	/** 0..1*/
	FloatResource getRelativeHumidity();
	/** g H2O total in the room*/
	float getAbsoluteHumidity();
	void addThermalEnergy(float joule);
	/** add vapor to the room as absolute amount in g H2O*/
	void addHumidity(float gH2O);
	ConcentrationResource getCO2();
	void addCO2(float mgCO2);
	/** average lumen*/
	BrightnessResource getLight();
	/** add luminous flux provided by a light source to the room*/
	void addLight(float lumen);
	boolean isPersonMoving();
	void addPersonMoving();
	/** m3*/
	float getVolume();

	/** Add person to the room. Note that this information will not be stored persistently by the
	 * room simulation service
	 * @param personNum number of people to be added
	 */
	void addUnknownPerson(int personNum);

	/** Remove person from the room. Note that this information will not be stored persistently by the
	 * room simulation service
	 * @param personNum number of people to be removed
	 * @return The total number of people cannot be lower than
	 * the list of known users in the room afterwards. If true the number of people to be removed could
	 * be performed, if the number of unknown people in the room has to be limited to zero false will be returned
	 */
	boolean removeUnknownPerson(int personNum);

	/** Add person to the room. Note that this information will not be stored persistently by the
	 * @param user to be added. If the user is already in the room the action will have no effect
	 * @return true if the user was not in the room before
	 */
	boolean addPerson(NaturalPerson user);

	/** Remove person from the room. Note that this information will not be stored persistently by the
	 * @param user to be removed. If the user is not in the room the method will have no effect
	 * @return true if the user was in the room before
	 */
	boolean removePerson(NaturalPerson user);

	/** Get total number of people in the room
	 * 
	 * @return including known and unknown people
	 */
	int getTotalRoomOccupancy();
	
	/** Get known users in the room
	 * @return list of known users in the room 
	 */
	
	Set<NaturalPerson> getKnownUsersInRoom();
}
