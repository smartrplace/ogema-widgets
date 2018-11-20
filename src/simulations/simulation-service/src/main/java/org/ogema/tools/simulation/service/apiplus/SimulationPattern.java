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

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;

/**
 * Super class for a simulated pattern
 */
public abstract class SimulationPattern<T extends SimulationConfigurationModel> extends ResourcePattern<T> {
	
	public final TimeResource updateInterval = model.updateInterval();
	
	// FIXME missing accept method? Use ContextSensitivePattern!
	@ValueChangedListener(activate = true) // is it necessary?
	public final StringResource simProviderId = model.simulationProviderId();
	
	public final Resource target = model.target();
	
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
