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

import org.ogema.core.model.simple.FloatResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.slf4j.LoggerFactory;

public class SOCConfig implements SimulationComplexConfiguration {
	
	private final FloatResource soc;
	
	@Override
	public String getValue() {
		return String.format("%.0f %%", soc.getValue()*100);
	}

	@Override
	public boolean setValue(String value) {
		try {
			value = value.replaceAll("[^\\d.]", "");
			float val = Float.parseFloat(value);
			if (Float.isNaN(val) || val< 0) {
				LoggerFactory.getLogger(getClass()).info("SOC set to negative value. Resizing to 10%");
				val = 10;
			} else if ( Float.isInfinite(val)) {
				LoggerFactory.getLogger(getClass()).info("SOC infinite. Resizing to 100%");
				val = 100;
			}
			return soc.setValue(val*0.01f);
		} catch(NumberFormatException e) {
			return false;
		}
	}	

	
	public SOCConfig(FloatResource soc) {
		this.soc = soc;
	}

	@Override
	public String getId() {
		return "Reset SOC";
	}

	@Override
	public String getDescription() {
		return "Set State-of-charge to new level, will be used for further simulation. If this is performed during"
				+ "a simulation step the operation may not take effect (just repeat)";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}
}
