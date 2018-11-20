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
 * Resource pattern for a heating thermal valve that may also provide information of a thermostat<br>
 * Note: Remove annotations '@Existence(required = CreateMode.OPTIONAL)' if you require an element
 * in your application, remove fields you do not need in your application at all<br>
 * Providing drivers: Homematic, KNX
 * 
 * @author David Nestle
 */
public class GenericThermalValvePattern extends ResourcePattern<ThermalValve> {

	/**
	 * Note: consider using a {@link GenericThermostatPattern} if you need the valve to be
	 * part of a thermostat.<br>
	 * Providing drivers: Homematic
	 */
	@Existence(required = CreateMode.OPTIONAL)
	private final Thermostat thermostat = findThermostatParent(model);
//	private final Thermostat thermostat = ResourceHelper.getFirstParentOfType(model, Thermostat.class);

	/**Either temperature setpoint or direct valve setpoint must exist
	 * Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.SHARED)
	public final TemperatureResource tempSetpoint = thermostat.temperatureSensor().settings().setpoint();
	
	/**Either temperature setpoint or direct valve setpoint must exist
	 * Providing drivers: KNX*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.SHARED)
	public final FloatResource valveSetpoint = model.setting().stateControl();
	
	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	private final TemperatureSensor tempSens = thermostat.temperatureSensor();
	
	/** Device temperature reading<br>
	 * Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final TemperatureResource temperature = tempSens.reading();
	
	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final TemperatureResource setpointFB = tempSens.deviceFeedback().setpoint();

	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final FloatResource valvePosition = model.setting().stateFeedback();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericThermalValvePattern(Resource device) {
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
	
	private final static Thermostat findThermostatParent(final ThermalValve valve) {
		Resource r = valve.getLocationResource();
		while (r != null) {
			r = r.getParent();
			if (r instanceof Thermostat)
				return (Thermostat) r;
		}
		return null;
	}
	
}
