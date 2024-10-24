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
package de.iwes.tools.standard.pattern;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.actors.RemoteControl;

/**
 * Resource pattern for a remote control<br>
 * Providing drivers: Homematic
 * 
 * @author David Nestle
 */
public class GenericRemoteControlPattern extends ResourcePattern<RemoteControl> {

	/**
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	public final ResourceList<BooleanResource> longPress = model.longPress();
	
	/**
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	public final ResourceList<BooleanResource> shortPress = model.shortPress();
	
	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.<br>
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 * </ul>
	 */
	public GenericRemoteControlPattern(Resource device) {
		super(device);
	}
	
	/**
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 * </ul>
	 */
	@Existence(required = CreateMode.OPTIONAL)
	private final FloatResource batteryStatus = model.battery().chargeSensor().reading();


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
