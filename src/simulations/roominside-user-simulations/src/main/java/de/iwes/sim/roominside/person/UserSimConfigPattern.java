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

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.simulation.service.apiplus.SimulationPattern;

public class UserSimConfigPattern extends SimulationPattern<UserSimConfig> {
	public UserSimConfigPattern(Resource res) {
		super(res);
	}

	@Existence(required=CreateMode.OPTIONAL)
	public StringResource currentPosition = model.currentPosition();

	@Existence(required=CreateMode.OPTIONAL)
	BooleanResource useOnlySimulatedRooms = model.useOnlySimulatedRooms();
	
	@Existence(required=CreateMode.OPTIONAL)
	FloatResource probabilityOutsideBuilding = model.probabilityOutsideBuilding();
	
	@Existence(required=CreateMode.OPTIONAL)
	TimeResource averageTimeInRoom = model.averageTimeInRoom();

	@Existence(required=CreateMode.OPTIONAL)
	public StringResource currentProgram = model.currentProgram();
	@Existence(required=CreateMode.OPTIONAL)
	public StringResource programStep = model.programStep();
	@Existence(required=CreateMode.OPTIONAL)
	public TimeResource currentProgramStepStartTime = model.currentProgramStepStartTime();
}
