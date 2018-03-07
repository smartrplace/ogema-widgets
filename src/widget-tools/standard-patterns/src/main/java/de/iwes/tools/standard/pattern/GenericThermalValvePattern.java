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
