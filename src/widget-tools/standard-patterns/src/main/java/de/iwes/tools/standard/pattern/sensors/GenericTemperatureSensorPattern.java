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
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.sensors.TemperatureSensor;

/**
 * Resource pattern for a temperature sensor<br>
 * Note: Remove annotations '@Existence(required = CreateMode.OPTIONAL)' if you require an element
 * in your application, remove fields you do not need in your application at all<br>
 * Providing drivers: Homematic, KNX
 * 
 * @author David Nestle
 */
public class GenericTemperatureSensorPattern extends ResourcePattern<TemperatureSensor> {
	
	/** 
	 * Device temperature reading<br>
	 * Providing drivers:<br>
	 * <ul>
	 * <li> Homematic
	 * <li> KNX
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	@Access(mode = AccessMode.READ_ONLY)
	public final TemperatureResource temperature = model.reading();
	
	/**
	 * Note: if the temperature sensor is part of a SensorDevice, like in {@link GenericRoomSensorPattern},
	 * then the battery is attached to the sensor device, not to the temperature sensor. 
	 * Therefore access to the battery may not be possible via this resource, even if the information
	 * is available.
	 */
	@Existence(required=CreateMode.OPTIONAL)
	public final FloatResource batteryStatus = model.battery().chargeSensor().reading();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericTemperatureSensorPattern(Resource device) {
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
