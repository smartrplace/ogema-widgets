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
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.sensors.TouchSensor;

/**
 * Resource pattern for a touch sensor that recognizes when a touch button is pressed. Note that a
 * touch sensor is very similar to a single-button RemoteControl.<br>
 * Providing drivers:KNX
 * 
 * @author David Nestle
 */
public class GenericTouchSensorPattern extends ResourcePattern<TouchSensor> {
	
	/** 
	 * Motion detection<br>
	 * Providing drivers:<br>
	 * <ul>
	 *  <li>KNX
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	@Access(mode = AccessMode.READ_ONLY)
	public final BooleanResource state = model.reading();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericTouchSensorPattern(Resource device) {
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
