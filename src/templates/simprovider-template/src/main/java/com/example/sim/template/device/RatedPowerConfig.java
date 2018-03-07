/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package com.example.sim.template.device;

import java.util.Map;

import org.ogema.core.model.units.PowerResource;
import org.ogema.tools.simulation.service.api.model.SimulationComplexConfiguration;
import org.slf4j.LoggerFactory;

/**
 * A configuration parameter for the simulation, that can be manipulated by the user.
 * In this example, it is the rated power of  
 */
public class RatedPowerConfig implements SimulationComplexConfiguration {
	
	private final PowerResource maxPower;
	
	public RatedPowerConfig(PowerResource powerRange) {
		this.maxPower = powerRange;
	}
	
	@Override
	public String getValue() {
		float val = maxPower.getValue();
		return String.format("%.0f", val);
	}

	@Override
	public boolean setValue(String value) {
		try {
			value = value.replaceAll("[^\\d.]", ""); // make sure the value is numeric // FIXME dots?
			float val = Float.parseFloat(value);
			if (Float.isNaN(val) || val< 0) {
				LoggerFactory.getLogger(getClass()).info("Rated power set to negative value. Resizing to 100W");
				val = 100;
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

	@Override
	public String getId() {
		return "Set rated power";
	}

	@Override
	public String getDescription() {
		return "Set rated peak feed-in power";
	}

	@Override
	public Map<String,String> getOptions() {
		return null;
	}
}
