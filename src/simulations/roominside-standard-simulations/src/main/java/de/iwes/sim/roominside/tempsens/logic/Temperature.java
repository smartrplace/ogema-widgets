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
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.sim.roominside.tempsens.logic;

import org.ogema.core.model.units.TemperatureResource;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;

public class Temperature implements SimulatedQuantity {
	
	private final TemperatureResource temp;
	
	public Temperature(TemperatureResource temp) {
		this.temp = temp;
	}
	
	@Override
	public TemperatureResource value() {
		return temp;
	}

	@Override
	public String getId() {
		return "Room temperature";
	}

	@Override
	public String getDescription() {
		return "Room temperature (in C)";
	}	

}
