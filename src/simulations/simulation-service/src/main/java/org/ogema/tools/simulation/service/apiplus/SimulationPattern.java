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

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

/**
 * Super class for a simulated pattern
 */
public abstract class SimulationPattern<T extends SimulationConfigurationModel> extends ResourcePattern<T> {
	
	public TimeResource updateInterval = model.updateInterval();
	
	// FIXME missing accept method? Use ContextSensitivePattern!
	@ValueChangedListener(activate = true) // is it necessary?
	public StringResource simProviderId = model.simulationProviderId();
	
	public Resource target = model.target();
	
	public boolean active = false; // only used internally
	
	public SimulationPattern(Resource res) {
		super(res);
	}
	
	@Override
	public String toString() {
		return "Simulation pattern for model: " + model.getLocation() + ", type: " + getClass().getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (obj == null || !(obj instanceof SimulationPattern))
			return false;
		SimulationPattern<?> other = (SimulationPattern<?>) obj;
		return this.model.equalsLocation(other.model);
	}
	
	@Override
	public int hashCode() {
		return model.getLocation().hashCode();
	}
	
}
