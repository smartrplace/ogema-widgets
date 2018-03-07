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
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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