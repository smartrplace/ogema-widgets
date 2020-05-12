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
	 * labels provided by {@link SimulationConfiguration#getOptions()}
	 */
	String getValue();
	/** Notify simulation that configuration value needs to be reset (e.g. by user interface) // -&gt; ??
	 * 
	 * @param value label of new value. The label must be one of the
	 * labels provided by {@link SimulationConfiguration#getOptions()}
	 * @return true if label could be successfully set, false if label could not be set (e.g. because
	 * options was lost in the meantime)
	 */
	boolean setValue(String value);
}