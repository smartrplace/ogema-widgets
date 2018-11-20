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
package de.iwes.elsim.meter.bpunit.logic;

import java.util.Map;

import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

public class DataGapConfig implements SimulationComplexConfiguration {
	
	private final TimeResource dataInterruption;
	
	@Override
	public String getValue() {
		return String.format("%d", dataInterruption.getValue()/1000);
	}

	@Override
	public boolean setValue(String value) {
		try {
			value = value.replaceAll("[^\\d.]", "");
			int val = Integer.parseInt(value);
			if (val< 0) {
				val = 0;
			}
			dataInterruption.setValue(val*1000);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}	

	
	public DataGapConfig(TimeResource dataInterruption) {
		this.dataInterruption = dataInterruption;
	}

	@Override
	public String getId() {
		return "Simulate data transmission interruption";
	}

	@Override
	public String getDescription() {
		return "Set seconds of new data transmission interruption";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}
}
