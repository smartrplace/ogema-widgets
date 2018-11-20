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
package org.ogema.tools.simulation.service.api;

import org.ogema.core.model.Resource;

/**
 * All simulation applications should register themselves with this service. This requires that 
 * they provide an implementation of the interface {@link SimulationProvider}.  <br>
 * If an application can simulate more than one device type, it should register multiple SimulationProviders.  
 * @author cnoelle
 */
public interface SimulationService  {
	
	/**
	 * Register a simulation application.
	 * @param provider: the app, which must implement the interface {@link SimulationProvider}
	 */
	public void registerSimulationProvider(SimulationProvider<? extends Resource> provider);
	
	/**
	 * Unregister a simulation application
	 */
	public void unregisterSimulationProvider(SimulationProvider<? extends Resource> provider);
}