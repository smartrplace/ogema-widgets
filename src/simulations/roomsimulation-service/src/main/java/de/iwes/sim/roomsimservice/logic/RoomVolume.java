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
package de.iwes.sim.roomsimservice.logic;

import java.util.Map;

import org.ogema.core.model.units.VolumeResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.slf4j.LoggerFactory;

public class RoomVolume implements SimulationComplexConfiguration {
	
	private final VolumeResource volume;
	
	// value must be positive (m^3)
	//@Override
	//public VolumeResource value() {
	//	return volume;
	//}
	@Override
	public String getValue() {
		return String.format("%.2f", volume.getValue());
	}

	@Override
	public boolean setValue(String value) {
		try {
			float val = Float.parseFloat(value);
			if (Float.isNaN(val) || val< 0) {
				LoggerFactory.getLogger(getClass()).info("Simulated room size set to negative value. Resizing to 10 m^3");
				val = 10;
			} else if ( Float.isInfinite(val)) {
				LoggerFactory.getLogger(getClass()).info("Simulated room size infinite. Resizing to 1000 m^3");
				val = 1000;
			}
			return volume.setValue(val);
		} catch(NumberFormatException e) {
			return false;
		}
	}	

	
	public RoomVolume(VolumeResource volume) {
		this.volume = volume;
	}

	@Override
	public String getId() {
		return "Room volume";
	}

	@Override
	public String getDescription() {
		return "Set the room volume (in m^3)";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}
}
