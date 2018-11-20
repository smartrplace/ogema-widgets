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
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.units.BrightnessResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.devices.sensoractordevices.SensorDevice;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.model.sensors.LightSensor;
import org.ogema.model.sensors.MotionSensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;

/**
 * Resource pattern for a room sensor that contains typically temperature and humditiy measurement, but may
 * also contain more/other sensors<br>
 * Note: Remove annotations '@Existence(required = CreateMode.OPTIONAL)' if you require an element
 * in your application, remove fields you do not need in your application at all<br>
 * If you are only interested in one particular sensor, use a dedicated sensor pattern instead.<br>
 * Providing drivers: Homematic (no light, motion), ZWave (no humidity)
 * 
 * @author David Nestle
 */
public class GenericRoomSensorPattern extends ResourcePattern<SensorDevice> {
	
	/**
	 * Providing drivers: all
	 * Note that the resource may be inactive
	 */
	@Existence(required = CreateMode.OPTIONAL)
	private final ResourceList<Sensor> sensors = model.sensors();

	/** 
	 * Device temperature reading.<br>
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 *  <li>ZWave
	 * </ul>
	 */
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final TemperatureResource temperature = sensors.getSubResource("temperatureSensor", TemperatureSensor.class).reading();

	/** 
	 * Device humidity reading<br>
	 * Providing drivers: Homematic
	 */
	@Existence(required = CreateMode.OPTIONAL)
	@Access(mode = AccessMode.READ_ONLY)
	public final FloatResource humidity = sensors.getSubResource("humiditySensor", HumiditySensor.class).reading();
	
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
	
	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericRoomSensorPattern(Resource device) {
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
