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

import java.util.List;

import org.ogema.core.model.Resource;

/**
 * Provides access to all {@link SimulationProvider}s that have been registered at the
 * {@link SimulationService}.  
 * @author cnoelle
 */
public interface SimulationServiceAdmin {

	/**
	 * Get a list of all simulation applications
	 */
	public List<SimulationProvider<? extends Resource>> getAllSimulationProviders();
	
	/**
	 * Get a list of all simulation applications for a specific device type
	 */
	public List<SimulationProvider<? extends Resource>> getSimulationProviders(Class<? extends Resource> simulatedDeviceType);
	
	/**
	 * Get a list of all devices simulated by the registered simulation applications
	 */
	public List<Resource> getAllSimulatedObjects();
	
	/**
	 * Get a list of all simulated devices of a particular type
	 */
	public List<Resource> getSimulatedObjects(Class<? extends Resource> simulatedDeviceType);
	
	/**
	 * Get a list of the (device, and other) types that can be simulated by the registered simulation applications
	 */
	public List<Class<? extends Resource>> getSimulatedTypes();
	
	/**
	 * Register a listener to get informed about new SimulationProviders, or ones that are no longer available
	 */
	public void registerListener(SimulationProviderListener<? extends Resource> listener);
	
	/**
	 * Deregister a listener
	 */
	public void deregisterListener(SimulationProviderListener<? extends Resource> listener);

}