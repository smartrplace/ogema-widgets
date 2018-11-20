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
package de.iwes.elsim.pv.logic;

import java.util.Map;

import org.ogema.core.model.units.PowerResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.slf4j.LoggerFactory;

public class RatedPowerConfig implements SimulationComplexConfiguration {
	
	private final PowerResource maxPower;
	
	@Override
	public String getValue() {
		float val = maxPower.getValue();
		return String.format("%.0f", val);
	}

	@Override
	public boolean setValue(String value) {
		try {
			value = value.replaceAll("[^\\d.]", "");
			float val = Float.parseFloat(value);
			if (Float.isNaN(val) || val< 0) {
				LoggerFactory.getLogger(getClass()).info("Rated power set to negative value. Resizing to 100W");
				val = 10;
			} else if ( Float.isInfinite(val)) {
				LoggerFactory.getLogger(getClass()).info("Rated power infinite. Resizing to 10MW");
				val = 1e7f;
			} 
			maxPower.setValue(val);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}	

	
	public RatedPowerConfig(PowerResource powerRange) {
		this.maxPower = powerRange;
	}

	@Override
	public String getId() {
		return "Set rated power";
	}

	@Override
	public String getDescription() {
		return "Set rated power to same value for charging and discharging (in W)";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}
}
