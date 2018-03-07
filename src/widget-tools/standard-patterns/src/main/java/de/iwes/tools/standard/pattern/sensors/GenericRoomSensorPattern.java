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
