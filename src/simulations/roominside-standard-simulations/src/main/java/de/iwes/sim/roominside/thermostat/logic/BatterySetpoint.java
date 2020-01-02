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

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

public class BatterySetpoint implements SimulationComplexConfiguration {
	
	//K
	private final FloatResource voltage;
	private final BooleanResource state;
	
	// value must be positive (in K)
	
	public BatterySetpoint(FloatResource voltage, BooleanResource state) {
		this.voltage = voltage;
		this.state = state;
	}

	@Override
	public String getId() {
		return "Battery voltage";
	}

	@Override
	public String getDescription() {
		return "Batter voltage of the device. The battery state is determined based on the voltage";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}

	@Override
	public String getValue() {
		if(voltage.isActive())
			return String.valueOf(voltage.getValue());
		else return state.getValue()?"5.0":"0.0";
	}

	@Override
	public boolean setValue(String value) {
		float val;
		try {
			val = Float.parseFloat(value);
			if (val < 0)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		boolean result = false;
		if(voltage.isActive())
			result = voltage.setValue(val);
		if(state != null)
			result = state.setValue(val>2.0f);
		return result;
	}	

}
