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
