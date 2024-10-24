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
package de.iwes.elsim.battery.logic;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;

import de.iwes.elsim.battery.BatteryPattern;

public class GeneratedPowerValue implements SimulatedQuantity {

	private final BatteryPattern pattern;
	
	public GeneratedPowerValue(BatteryPattern pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public String getId() {
		return "Current Battery power generation";
	}

	@Override
	public String getDescription() {
		return "Power in W";
	}

	/**
	 * This is a reference to the value resource that is actually simulated
	 * by this provider. 
	 */
	@Override
	public SingleValueResource value() {
		return pattern.powerReading;
	}

}
