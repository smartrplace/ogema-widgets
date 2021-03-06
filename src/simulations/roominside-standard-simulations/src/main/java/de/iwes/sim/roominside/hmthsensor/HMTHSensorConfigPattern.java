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
package de.iwes.sim.roominside.hmthsensor;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;

import de.iwes.sim.roomsimservice.device.RoomInsideConfigPattern;

public class HMTHSensorConfigPattern extends RoomInsideConfigPattern<HMTHSensorConfig> {
	public HMTHSensorConfigPattern(Resource res) {
		super(res);
	}

	@Existence(required=CreateMode.OPTIONAL)
	public FloatResource internalChargeState = model.internalChargeState();
}
