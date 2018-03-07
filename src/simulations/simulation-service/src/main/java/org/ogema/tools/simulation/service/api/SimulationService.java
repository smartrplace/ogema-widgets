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