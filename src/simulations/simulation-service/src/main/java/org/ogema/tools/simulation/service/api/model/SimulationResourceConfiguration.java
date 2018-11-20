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

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.simulation.service.api.SimulationProvider;

/**
 * Stores configuration values for a {@link SimulationProvider}.<br>
 * {@link SimulationProvider}s shall provide custom configuration types implementing this one. 
 * Instances of this type will be displayed in a user interface.<br>
 * Use this configuration type if the configuration value is directly represented by a SingleValueResource.
 * @author cnoelle, dnestle
 *
 */
public interface SimulationResourceConfiguration extends SimulationConfiguration {

	/**
	 * To be overridden by inheriting type.
	 */
	SingleValueResource value();
}