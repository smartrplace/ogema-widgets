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