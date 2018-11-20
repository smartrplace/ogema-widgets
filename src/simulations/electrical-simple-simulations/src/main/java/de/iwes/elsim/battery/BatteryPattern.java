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
package de.iwes.elsim.battery;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.EnergyResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.resourcemanager.AccessMode;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.actors.MultiSwitch;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.devices.storage.ElectricityStorage;
import org.ogema.model.ranges.EnergyRange;
import org.ogema.model.ranges.PowerRange;
import org.ogema.model.sensors.PowerSensor;
import org.ogema.model.sensors.StateOfChargeSensor;

// FIXME why are all the fields optional? In practice they are not...
public class BatteryPattern extends ResourcePattern<ElectricityStorage> { 
	
	// retain 95% soc after a month
	private static final double DEFAULT_SELF_DISCHARGE = Math.pow(0.95, 1./(30*24*60*60)); // 5% self discharge per month as per second value
	
    @Existence(required=CreateMode.OPTIONAL)
    private final StateOfChargeSensor chargeSensor = model.chargeSensor();
    
    @Access(mode = AccessMode.EXCLUSIVE, required = true)
    @Existence(required=CreateMode.OPTIONAL)
    public final FloatResource soc = chargeSensor.reading();
    
    @Existence(required=CreateMode.OPTIONAL)
    public final EnergyRange ratedEnergy = model.ratedEnergy();
    @Existence(required=CreateMode.OPTIONAL)
    public final EnergyResource minE = ratedEnergy.lowerLimit();
    @Existence(required=CreateMode.OPTIONAL)
    public final EnergyResource maxE = ratedEnergy.upperLimit();
    
    @Existence(required=CreateMode.OPTIONAL)
    private final ElectricityConnection electricityConnection = model.electricityConnection();
    @Existence(required=CreateMode.OPTIONAL)
    private final PowerSensor powerSensor = electricityConnection.powerSensor();

    @Access(mode = AccessMode.EXCLUSIVE, required = true)
    @Existence(required=CreateMode.OPTIONAL)
    public final PowerResource powerReading = powerSensor.reading();
    @Existence(required=CreateMode.OPTIONAL)
    public final PowerRange powerRange = powerSensor.ratedValues();
    @Existence(required=CreateMode.OPTIONAL)
    public PowerResource minPower = powerRange.lowerLimit();
    @Existence(required=CreateMode.OPTIONAL)
    public PowerResource maxPower = powerRange.upperLimit();
    
    // TODO explain
    @Existence(required=CreateMode.OPTIONAL)
    public final PowerRange powerRangePUN = powerSensor.deviceSettings().controlLimits();
    @Existence(required=CreateMode.OPTIONAL)
    public final PowerResource minPowerPUN = powerRangePUN.lowerLimit();
    @Existence(required=CreateMode.OPTIONAL)
    public final PowerResource maxPowerPUN = powerRangePUN.upperLimit();

    @Existence(required=CreateMode.OPTIONAL)
    private final MultiSwitch setting = model.setting();
    @Existence(required=CreateMode.OPTIONAL)
    public final FloatResource stateControl = setting.stateControl();
    @Existence(required=CreateMode.OPTIONAL) // TODO explain
    public final PowerResource stateControlPVNetz = powerSensor.deviceSettings().setpoint();

    @Access(mode = AccessMode.EXCLUSIVE, required = true)
    @Existence(required=CreateMode.OPTIONAL)
    public final FloatResource stateFeedback = setting.stateFeedback();
    
    /**
     * Note: this is assumed to be the self-discharge rate per day!
     */
    // if this is not present, a default self-discharge rate of 5%/month is assumed
    @Existence(required=CreateMode.OPTIONAL)
    public final FloatResource selfDischargeRate = model.selfDischargeRate();
    
	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework. Must be public.
	 */
	public BatteryPattern(Resource device) {
		super(device);
	}
	
	/**
	 * 
	 * @return
	 */
	public double getSelfDischargeEfficiencyPerSecond() {
		double sd = selfDischargeRate.isActive() ? (1-selfDischargeRate.getValue()) : DEFAULT_SELF_DISCHARGE;
		if (sd < 0 || sd > 1)
			sd = DEFAULT_SELF_DISCHARGE;
		return sd;
	}
	
}
