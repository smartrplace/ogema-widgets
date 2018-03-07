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

import java.util.Map;

import org.ogema.tools.simulation.service.api.SimulationProvider;

/**
 * Stores configuration values for a {@link SimulationProvider}.<br>
 * {@link SimulationProvider}s shall provide custom configuration types implementing this one. 
 * Instances of this type will be displayed in a user interface.
 * This interface should not be used directly, but use {@link SimulationResourceConfiguration} or
 * {@link SimulationComplexConfiguration}
 * @author cnoelle, dnestle
 *
 */
public interface SimulationConfiguration extends AnnotatedData {

	/**
	 * If the set of allowed values for {@link #value()} is finite, 
	 * the SimulationProvider may provide the list here (convert numerical values to Strings). <br>
	 * If the allowed values are not restricted, return null.  <br>
	 * Keys: actual values that may be written to {@link #value()} (e.g. certain resource locations) or 
	 * that may be used as labels in {@link getValue} or {@link setValue}<br>
	 * Values: Human-readable names for display in a UI
	 * 
	 * @return
	 * 		null, if the set of allowed options is not restricted
	 */
	Map<String, String> getOptions();

}