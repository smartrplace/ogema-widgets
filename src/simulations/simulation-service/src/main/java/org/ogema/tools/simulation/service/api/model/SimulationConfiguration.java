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
	 * If the set of allowed values for #value() is finite, 
	 * the SimulationProvider may provide the list here (convert numerical values to Strings). <br>
	 * If the allowed values are not restricted, return null.  <br>
	 * Keys: actual values that may be written to #value() (e.g. certain resource locations) or 
	 * that may be used as labels in #getValue or #setValue<br>
	 * Values: Human-readable names for display in a UI
	 * 
	 * @return
	 * 		null, if the set of allowed options is not restricted
	 */
	Map<String, String> getOptions();

}