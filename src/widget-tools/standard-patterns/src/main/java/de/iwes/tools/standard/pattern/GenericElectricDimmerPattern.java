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
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.devices.buildingtechnology.ElectricDimmer;

/**
 * Resource pattern for an electric dimmer<br>
 * Providing drivers: KNX
 * 
 * @author David Nestle
 */
public class GenericElectricDimmerPattern extends ResourcePattern<ElectricDimmer> {
	
	/** 
	 * Controlling setpoint for dimmer<br>
	 * Providing drivers:<br>
	 * <ul>
	 *  <li>KNX
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	@Access(mode = AccessMode.SHARED)
	public final FloatResource stateControl = model.setting().stateControl();

	/** 
	 * Feedback from dimmer<br>
	 * Providing drivers:<br>
	 * <ul>
	 *  <li>KNX
	 * </ul>
	 */
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final FloatResource stateFeedback = model.setting().stateFeedback();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericElectricDimmerPattern(Resource device) {
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
