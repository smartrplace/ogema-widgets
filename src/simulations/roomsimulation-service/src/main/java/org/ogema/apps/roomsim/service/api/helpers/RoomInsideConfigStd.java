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

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.locations.Room;
import org.ogema.tools.simulation.service.apiplus.SimulationConfigurationModel;

public interface RoomInsideConfigStd extends SimulationConfigurationModel {
	/**This is a reference to the room inside which the component shall be simulated. Note
	 * that this could be different from the room that the device is located in, but usually
	 * both references should point to the same room (if the room information exists for the
	 * simulated device)
	 */
	public Room roomSimulationToConnect();
	
	/**If true the room in the target resource will be changed when the simulated room is changed
	 * via the RoomConnect config. The default behaviour is TRUE.
	 */
	public BooleanResource adaptRoomWithSimulation();
}
