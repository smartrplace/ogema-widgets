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
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.template.LabelledItem;

/**
 * All simulation applications shall implement this interface. 
 * @param <T> the device or quantity type that can be simulated
 * @author cnoelle
 */
public interface SimulationProvider<T extends Resource> extends LabelledItem {
	
	/**
	 * @return a unique providerId
	 * @deprecated use {@link #id()} instead
	 */
	@Deprecated
	public String getProviderId();
	
	@Override
	default String id() {
		return getProviderId();
	}
	
	@Override
	default String label(OgemaLocale locale) {
		return getProviderId();
	}
	
	/**
	 * A short description for display in a UI
	 * @deprecated use {@link #description(OgemaLocale)} instead.
	 */
	@Deprecated
	public String getDescription();
	
	@Override
	default String description(OgemaLocale locale) {
		return getDescription();
	}
	
	/**
	 * @return the device type T
	 */
	public Class<T> getSimulatedType();
	
	/**
	 * @return the list of simulated devices, or simulated objects
	 */
	public List<T> getSimulatedObjects();
	
	/**
	 * Create a new device (or other object) of type T and activate it. The deviceLocation will be chosen
	 * as the toplevel resource name, and hence must be unique. If a resource of the 
	 * given name already exists and it has the correct type, this returns the existing 
	 * device; the SimulationProvider shall try to gain access to the resource, activate it and all subresources 
	 * controlled by the provider, and simulate it in the following. <br> 
	 * If the resource exists and has an incompatible type, it catches the ResourceAlreadyExistsException and returns null.<br>
	 * Whether or not this immediately starts the simulation is left to the implementation.
	 * @return the newly created device resource, or null if the SimulationProvider does not support the creation of
	 * additional simulated objects (for instance, it might only be able to simulate a single device)
	 * @throws IllegalArgumentException if deviceLocation contains illegal characters
	 */
	public T createSimulatedObject(String deviceLocation);
	
	/** 
	 * Start the simulation for this device (or object). <br>
	 * @return true, if simulation is now running, false if no device with given deviceLocation was found, or the simulation could not be started,
	 * 	e.g. because some configuration is still missing.
	 */
	public boolean startSimulation(String deviceLocation);
	
	/** 
	 * Stop the simulation for this device  (or object). Does not change active status <br>
	 * @return true, if resource had been simulated, false otherwise.
	 */
	public boolean stopSimulation(String deviceLocation);
	
	/**
	 * is the device/object currently being simulated?
	 */
	public boolean isSimulationActive(String deviceLocation);
	/**
	 * Will a call to startSimulation be successful ? (if not parameters or other conditions may be missing)
	 */
	public boolean isSimulationActivatable(String deviceLocation);
	
	/**
	 * Get configuration settings for a particular simulated object, identified by its deviceLocation. May return null, 
	 * if either no object corresponding to the deviceLocation has been found, or no configuration is available.
	 */
	public List<SimulationConfiguration> getConfigurations(String deviceLocation);
	
	/**
	 * Get references to the simulated value resources. 
	 */
	public List<SimulatedQuantity> getSimulatedQuantities(String deviceLocation);
}