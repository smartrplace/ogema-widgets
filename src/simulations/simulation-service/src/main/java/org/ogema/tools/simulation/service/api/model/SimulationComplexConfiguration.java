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

package org.ogema.tools.simulation.service.api.model;

import org.ogema.tools.simulation.service.api.SimulationProvider;

/**
 * Stores configuration values for a {@link SimulationProvider}.<br>
 * {@link SimulationProvider}s shall provide custom configuration types implementing this one. 
 * Instances of this type will be displayed in a user interface.<br>
 * Use this configuration type for configuration values that are determined by resource references or
 * other information that cannot directly be derived from a SingleValueResource.
 * @author cnoelle, dnestle
 *
 */
public interface SimulationComplexConfiguration extends SimulationConfiguration {

	/**
	 * To be overridden by inheriting type:
	 * @return label indicating current value of the configuration. The label must be one of the
	 * labels provided by {@link getOptions) 
	 */
	String getValue();
	/** Notify simulation that configuration value needs to be reset (e.g. by user interface) // -> ??
	 * 
	 * @param value label of new value. The label must be one of the
	 * labels provided by {@link getOptions)
	 * @return true if label could be successfully set, false if label could not be set (e.g. because
	 * options was lost in the meantime)
	 */
	boolean setValue(String value);
}