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
package de.iwes.sim.roominside.thermostat;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.PhysicalElement;

public class ThermostatPattern extends ResourcePattern<Thermostat> { 
	
	/***** Values *****/
	@Existence(required=CreateMode.OPTIONAL)
	public FloatResource valveStatus = model.valve().setting().stateFeedback();
	@Existence(required=CreateMode.OPTIONAL) // the basic simulated quantities must not be optional // -> basic quantity is heat flow
	public FloatResource valveStateControl = model.valve().setting().stateControl();
	
	@Existence(required=CreateMode.OPTIONAL) 
	public TemperatureResource measuredTemperature = model.temperatureSensor().reading();
	
	/*** Configurations ***/
	@Existence(required=CreateMode.OPTIONAL)
	public TemperatureResource targetTemperature = model.temperatureSensor().settings().setpoint();
	@Existence(required=CreateMode.OPTIONAL)
	public TemperatureResource setpointFB = model.temperatureSensor().deviceFeedback().setpoint();
	//TODO: Size of radiator etc.
	public Room room = model.location().room(); //should not change when set
	
	/*** Flavor configuration ***/
	//@Existence(required=CreateMode.OPTIONAL)
	//@ValueChangedListener(activate = true)
	//public StringResource flavorConfiguration = model.getSubResource("flavorConfiguration", StringResource.class);

	/*** Homematic ***/
	@Existence(required=CreateMode.OPTIONAL)
	public FloatResource batteryCharge = model.battery().internalVoltage().reading();
	@Existence(required=CreateMode.OPTIONAL)
	public BooleanResource batteryState;
	
	// TODO make this a configurable parameter
	public float maximumPower = 100; // in W/m^2 (typically ranges from 80-120 W/m^2) 
	
	//@Existence(required=CreateMode.OPTIONAL)
	//public ThermalConnection thConn = model.getSubResource("simulatedThermalConnection", ThermalConnection.class);
//	@Existence(required=CreateMode.OPTIONAL)  // the basic simulated quantities must not be optional
//	public PowerResource thPower = model.getSubResource("simulatedThermalPower", PowerResource.class);
	@Existence(required=CreateMode.OPTIONAL)
	public PowerResource thPower = model.valve().connection().powerSensor().reading();

	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework. Must be public.
	 */
	public ThermostatPattern(Resource device) {
		super(device);
	}
	
	@Override
	public boolean accept() {
		// no custom condition, we accept all pattern matches
		batteryState = getBatteryState(model);
		return true; 
	}
	
	public static BooleanResource getBatteryState(PhysicalElement modelIn) {
		Resource parent = modelIn.getParent();
		if(parent == null) return null;
		for(Resource s: parent.getSubResources(false)) {
			if(s.getResourceType().getSimpleName().equals("HmMaintenance")) {
				return s.getSubResource("batteryLow", BooleanResource.class);
			}
		}
		return null;
	}
}
