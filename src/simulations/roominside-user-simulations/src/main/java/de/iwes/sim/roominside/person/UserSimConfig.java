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

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.user.NaturalPerson;
import org.ogema.tools.simulation.service.apiplus.SimulationConfigurationModel;

public interface UserSimConfig extends SimulationConfigurationModel {
	/**location of room person is situated. "outside" if outside building*/
	StringResource currentPosition();
	/**default: true*/
	BooleanResource useOnlySimulatedRooms();
	
	/**For first step person is in all rooms with same probability when in the building*/
	FloatResource probabilityOutsideBuilding();
	
	/**Average time users stays in one room before moving to another room or outside the
	 * building
	 */
	TimeResource averageTimeInRoom();
	
	/**User control program*/
	StringResource currentProgram();
	TimeResource currentProgramStepStartTime();
	StringResource programStep();
	
	@Override
	NaturalPerson target();
}
