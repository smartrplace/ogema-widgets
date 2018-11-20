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
