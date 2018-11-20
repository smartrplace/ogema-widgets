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
package de.iwes.tools.standard.pattern.sensors;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.sensors.DoorWindowSensor;

/**
 * Resource pattern for a window or door sensor<br>
 * Providing drivers: Homematic
 */
public class GenericWindowContactPattern extends ResourcePattern<DoorWindowSensor> {

	/**Providing drivers: Homematic*/
	@Existence(required=CreateMode.MUST_EXIST)
	@Access(mode=AccessMode.SHARED) // replace by READ_ONLY if write access is not required
	public final BooleanResource open = model.reading();

	/**Providing drivers: Homematic*/
	@Existence(required=CreateMode.OPTIONAL)
	public final FloatResource batteryStatus = model.battery().chargeSensor().reading();
	
	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericWindowContactPattern(Resource device) {
		super(device);
	}

	/**
	 * Provide any initial values that shall be set (this overrides any initial values set by simulation
	 * components themselves)
	 * Configure logging
	 */
	@Override
	public boolean accept() {
		return true;
	}
}
