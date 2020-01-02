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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.sim.roominside.hmmotionsensor;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.units.BrightnessResource;
import org.ogema.core.model.units.VoltageResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.devices.sensoractordevices.SensorDevice;
import org.ogema.model.sensors.LightSensor;
import org.ogema.model.sensors.MotionSensor;
import org.ogema.model.sensors.Sensor;

public class HMMotionSensorPattern extends ResourcePattern<SensorDevice> { 
	
	/***** Values planned for generic pattern*****/
	@Existence(required = CreateMode.MUST_EXIST)
	private final ResourceList<Sensor> sensors = model.sensors();

	/** 
	 * TODO check units in ZWave driver
	 * Providing drivers:<br>
	 * <ul>
	 *  <li>ZWave
	 * </ul>
	 */
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final BrightnessResource brightness = sensors.getSubResource("lightSensor", LightSensor.class).reading();
	
	/** 
	 * Providing drivers:<br>
	 * <ul>
	 *  <li>ZWave
	 * </ul>
	 */
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final BooleanResource motion = sensors.getSubResource("motionSensor", MotionSensor.class).reading();
	
	/**
	 * TODO: Currently the value is provided 0..100 by homematic, should be 0.0..1.0 (TODO check for ZWave)
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 *  <li>ZWave
	 * </ul>
	 */
	@Existence(required = CreateMode.OPTIONAL)
	public final FloatResource batteryStatus = model.electricityStorage().chargeSensor().reading();

	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	public final IntegerResource elStorageType = model.electricityStorage().type();
	
	/***** Values *****/
	@Existence(required=CreateMode.OPTIONAL)
	public VoltageResource batteryVoltage = model.electricityStorage().internalVoltage().reading();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework. Must be public.
	 */
	public HMMotionSensorPattern(Resource device) {
		super(device);
	}
	
	@Override
	public boolean accept() {
		// no custom condition, we accept all pattern matches
		return true; 
	}
}
