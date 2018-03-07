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
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.sensors.OccupancySensor;

/**
 * Resource pattern for an occupancy motion sensor. Note that a very sensitive motion sensor can be modeled as
 * a OccupancySensor which is the case for KNX. Homematic and ZWave MotionSensors may act like a
 * KNX OccupancySensor.<br>
 * Providing drivers:KNX
 * 
 * @author David Nestle
 */
public class GenericOccupancySensorPattern extends ResourcePattern<OccupancySensor> {
	
	/** 
	 * Motion detection<br>
	 * Providing drivers:<br>
	 * <ul>
	 *  <li>KNX
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	@Access(mode = AccessMode.READ_ONLY)
	public final BooleanResource motion = model.reading();
	
	/**
	 * Note: if the occupancy sensor is part of a SensorDevice, like in {@link GenericRoomSensorPattern},
	 * then the battery is attached to the sensor device, not to the motion sensor itself. 
	 * Therefore access to the battery may not be possible via this resource, even if the information
	 * is available. This is often the case, since a motion detector usually comes bundled with a 
	 * brightness sensor.
	 */
	@Existence(required=CreateMode.OPTIONAL)
	public final FloatResource batteryStatus = model.battery().chargeSensor().reading();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericOccupancySensorPattern(Resource device) {
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
