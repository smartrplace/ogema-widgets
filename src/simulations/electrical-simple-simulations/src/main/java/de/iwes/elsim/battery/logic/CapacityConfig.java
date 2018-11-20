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
package de.iwes.elsim.battery.logic;

import java.util.Map;

import org.ogema.core.model.units.EnergyResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.slf4j.LoggerFactory;

public class CapacityConfig implements SimulationComplexConfiguration {
	
	private final EnergyResource upperLimit;
	
	@Override
	public String getValue() {
		return String.format("%.1f", upperLimit.getValue()*SimulatedBattery.J_TO_KWH);
	}

	@Override
	public boolean setValue(String value) {
		try {
			value = value.replaceAll("[^\\d.]", "");
			float val = Float.parseFloat(value);
			if (Float.isNaN(val) || val< 0) {
				LoggerFactory.getLogger(getClass()).info("Capacity set to negative value. Resizing to 1kWh");
				val = 1;
			} else if ( Float.isInfinite(val)) {
				LoggerFactory.getLogger(getClass()).info("Capacity infinite. Resizing to 1000kWh");
				val = 1000;
			}
			return upperLimit.setValue(val*SimulatedBattery.KWH_TO_J);
		} catch(NumberFormatException e) {
			return false;
		}
	}	

	
	public CapacityConfig(EnergyResource upperLimitCapacity) {
		this.upperLimit = upperLimitCapacity;
	}

	@Override
	public String getId() {
		return "Set capacity of battery";
	}

	@Override
	public String getDescription() {
		return "Set capacity of battery as upper Limit of rated energy (kWh)";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}
}
