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
package de.iwes.sim.roominside.windowsensor;

import java.util.HashMap;
import java.util.Map;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.locations.Room;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

import de.iwes.util.format.NameFinder;

public class BooleanSensorState implements SimulationComplexConfiguration {
	
	//K
	private final BooleanResource setpoint;
	
	// value must be positive (in K)
	
	public BooleanSensorState(BooleanResource setpoint) {
		this.setpoint = setpoint;
	}

	@Override
	public String getId() {
		return "Is window open";
	}

	@Override
	public String getDescription() {
		return "Set true to indicate open window";
	}

	@Override
	public Map<String,String> getOptions() {
		Map<String, String> res = new HashMap<>();
		res.put("false", "false");
		res.put("true", "true");
		return res;
	}

	@Override
	public String getValue() {
		return String.valueOf(setpoint.getValue());
	}

	@Override
	public boolean setValue(String value) {
		boolean result = false;
		try {
			result = Boolean.parseBoolean(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return setpoint.setValue(result);		
	}	

}
