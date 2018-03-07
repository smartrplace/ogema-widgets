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
 * Get informed about new {@link SimulationProvider}s, or ones that are
 * no longer available. Register your listener with
 * {@link SimulationService#registerListener(SimulationProviderListener)}.
 * @param <T> the device type or quantity that can be simulated
 */
public interface SimulationProviderListener<T extends Resource> {
	
	/**
	 * note: only callbacks for SimulationProvider<? extends T> are issued
	 */
	public void simulationAvailable(SimulationProvider<?> provider);
	
	public void simulationUnavailable(SimulationProvider<?> provider);
	
	/**
	 * return here the type listened for (avoidable?)
	 */
	public Class<T> getType();

}