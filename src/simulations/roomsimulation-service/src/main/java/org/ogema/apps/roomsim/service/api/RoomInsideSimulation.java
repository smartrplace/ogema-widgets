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
package org.ogema.apps.roomsim.service.api;

import org.ogema.core.model.Resource;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.apiplus.SimulationPattern;

public interface RoomInsideSimulation<T extends Resource> {
	public void step(long stepSize);
	public void close();
	
	/** for provider-less simulations may be null*/
	public SimulationProvider<T> getProvider();
	
	/**Notification from framework that room service simulation is available. This is
	 * required as the simulation may not have started when the initial connection
	 * is made*/
	public boolean simulationAvailable(SingleRoomSimulation singleRoomSimulation);

	public SimulationPattern<?> getSimulationPattern();
	//public List<SimulationConfiguration> getConfigurations();
}
