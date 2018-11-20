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
package de.iwes.elsim.hmswitchbox;

import java.util.Map;

import org.ogema.core.model.units.PowerResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;

public class DevicePowerConfig implements SimulationComplexConfiguration {
	
	private final PowerResource connectedDevicePower;
	
	@Override
	public String getValue() {
		return String.format("%.1f", connectedDevicePower.getValue());
	}

	@Override
	public boolean setValue(String value) {
		try {
			value = value.replaceAll("[^\\d.]", "");
			float val = Float.parseFloat(value);
			return connectedDevicePower.setValue(val);
		} catch(NumberFormatException e) {
			return false;
		}
	}	

	
	public DevicePowerConfig(PowerResource connectedDevicePower) {
		this.connectedDevicePower = connectedDevicePower;
	}

	@Override
	public String getId() {
		return "Set power of connected device";
	}

	@Override
	public String getDescription() {
		return "Set power of device connected to switch box (W)";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}
}
