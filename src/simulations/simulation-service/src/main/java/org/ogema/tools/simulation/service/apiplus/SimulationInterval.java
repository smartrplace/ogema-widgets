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
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur F�rderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.tools.simulation.service.apiplus;

import java.util.Map;

import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

/** SimulationConfiguration allowing to set the simulation interval by the user. Automatically added
 * to the ResourceProvider-configs by {@link SimulationBase}
 * 
 * @author dnestle
 */
public class SimulationInterval implements SimulationComplexConfiguration {
	
	public SimulationInterval(TimeResource updateInterval) {
		this.interval = updateInterval;
	}

	private final TimeResource interval;
	
	@Override
	public String getValue() {
		return String.valueOf(interval.getValue()/1000);
	}

	@Override
	public boolean setValue(String value) {
		long val;
		try {
			val = Long.parseLong(value)*1000;
		} catch (NumberFormatException e) {
			return false;
		}
		if (val < 1000) // do not allow negative or extremely small time steps
			val = 1000;
		return interval.setValue(val);
	}

	@Override
	public String getId() {
		return "Update interval";
	}

	@Override
	public String getDescription() {
		return "Set the simulation update time interval (in full seconds)";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}

}