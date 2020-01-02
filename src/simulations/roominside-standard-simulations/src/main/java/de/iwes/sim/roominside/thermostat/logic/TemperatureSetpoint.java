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
package de.iwes.sim.roominside.thermostat.logic;

import java.util.Map;

import org.ogema.core.model.units.TemperatureResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

public class TemperatureSetpoint implements SimulationComplexConfiguration {
	
	//K
	private final TemperatureResource setpoint;
	
	// value must be positive (in K)
	
	public TemperatureSetpoint(TemperatureResource setpoint) {
		this.setpoint = setpoint;
	}

	@Override
	public String getId() {
		return "Thermostat local setpoint";
	}

	@Override
	public String getDescription() {
		return "Setpoint that is displayed at the thermostat in °C";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}

	@Override
	public String getValue() {
		return String.valueOf(setpoint.getCelsius());
	}

	@Override
	public boolean setValue(String value) {
		float celsius;
		try {
			celsius = Float.parseFloat(value);
			if (celsius < -273.15f)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return setpoint.setCelsius(celsius);		
	}	

}
