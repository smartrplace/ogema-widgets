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
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.devices.connectiondevices.ThermalValve;
import org.ogema.model.sensors.TemperatureSensor;

/**
 * Resource pattern for a heating thermostat that connects to information provided by a
 * homematic device<br>
 * Note: Remove annotations '@Existence(required = CreateMode.OPTIONAL)' if you require an element
 * in your application, remove fields you do not need in your application at all<br>
 * Providing drivers: Homematic
 * 
 * @author David Nestle
 */
public class GenericThermostatPattern extends ResourcePattern<Thermostat> {

	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	private final TemperatureSensor tempSens = model.temperatureSensor();
	
	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	private final ThermalValve valve = model.valve();
	
	/** Device temperature reading<br>
	 * Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final TemperatureResource temperature = tempSens.reading();
	
	/**Either temperature setpoint or direct valve setpoint must exist
	 * Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.SHARED)
	public final TemperatureResource tempSetpoint = tempSens.settings().setpoint();
	/**Either temperature setpoint or direct valve setpoint must exist
	 * Providing drivers: KNX*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.SHARED)
	public final FloatResource valveSetpoint = valve.setting().stateControl();

	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final TemperatureResource setpointFB = tempSens.deviceFeedback().setpoint();

	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final FloatResource valvePosition = valve.setting().stateFeedback();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericThermostatPattern(Resource device) {
		super(device);
	}

	/**
	 * Provide any initial values that shall be set (this overrides any initial values set by simulation
	 * components themselves)
	 * Configure logging
	 */
	@Override
	public boolean accept() {
		if((!valveSetpoint.isActive()) && (!tempSetpoint.isActive())) return false;
		return true;
	}
}
