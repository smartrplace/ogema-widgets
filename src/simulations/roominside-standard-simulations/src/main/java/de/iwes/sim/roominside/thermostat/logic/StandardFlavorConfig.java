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

import java.util.HashMap;
import java.util.Map;

import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

import de.iwes.sim.roominside.thermostat.ThermostatConfigPattern;
import de.iwes.sim.roominside.thermostat.ThermostatSimulation;

public class StandardFlavorConfig implements SimulationComplexConfiguration {
	
	//K
	private final ThermostatConfigPattern homeConfigPattern;
	
	// value must be positive (in K)
	
	public StandardFlavorConfig(ThermostatConfigPattern homeConfigPattern) {
		this.homeConfigPattern = homeConfigPattern;
	}

	@Override
	public String getId() {
		return "Standard Thermostat Flavor Configuration";
	}

	@Override
	public String getDescription() {
		return "Standard Thermostat Flavor Configuration";
	}

	@Override
	public Map<String,String> getOptions() {
		Map<String, String> res = new HashMap<>();
		res.put("Standard", "Standard");
		res.put("HomematicV1", "HomematicV1");
		return res;
	}

	@Override
	public String getValue() {
		return getSettingResource().getValue();
	}

	@Override
	public boolean setValue(String value) {
		if(value.equals("Standard")) {
			getSettingResource().setValue(value);
			return true;
		} else if(value.equals("HomematicV1")) {
			getSettingResource().setValue(value);
			return true;
		} 
		return false;
	}
	
	StringResource getSettingResource() {
		if(ThermostatSimulation.globalConfig == null) {
			ThermostatSimulation.globalConfig = homeConfigPattern;
			homeConfigPattern.isGlobalConfig.create();
			homeConfigPattern.isGlobalConfig.setValue(true);
			homeConfigPattern.standardFlavorConfiguration.create();
			homeConfigPattern.standardFlavorConfiguration.setValue("Standard");
			homeConfigPattern.model.activate(true);
		}
		return ThermostatSimulation.globalConfig.standardFlavorConfiguration;
	}
}
