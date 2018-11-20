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
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.units.ElectricCurrentResource;
import org.ogema.core.model.units.EnergyResource;
import org.ogema.core.model.units.FrequencyResource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.model.units.VoltageResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.actors.OnOffSwitch;
import org.ogema.model.devices.sensoractordevices.SingleSwitchBox;

/**
 * Resource pattern for an electrical switch box that may contain power and more electrical measurement<br>
 * Note: Remove annotations '@Existence(required = CreateMode.OPTIONAL)' if you require an element
 * in your application, remove fields you do not need in your application at all<br>
 * Providing drivers: Homematic, ZWave (no measurements), KNX (no measurements)
 * 
 * @author David Nestle
 */
public class GenericSwitchboxPattern extends ResourcePattern<SingleSwitchBox> {

	/**
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 *  <li>ZWave
	 *  <li>KNX
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	public final OnOffSwitch swtch = model.onOffSwitch();

	/**
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 *  <li>ZWave
	 *  <li>KNX
	 * </ul>
	 */
	@Existence(required = CreateMode.MUST_EXIST)
	public final BooleanResource stateControl = swtch.stateControl();

	/**
	 * Providing drivers:<br>
	 * <ul>
	 * 	<li>Homematic
	 *  <li>ZWave
	 * </ul>
	 */
	@Existence(required = CreateMode.OPTIONAL)
	public final BooleanResource stateFeedback = swtch.stateFeedback();

	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	public final BooleanResource isControllable = swtch.controllable();

	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	public final PowerResource mmxPower = model.electricityConnection().powerSensor().reading();
	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	public final EnergyResource mmxEnergy = model.electricityConnection().energySensor().reading();
	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	public final VoltageResource mmxVoltage = model.electricityConnection().voltageSensor().reading();
	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	public final ElectricCurrentResource mmxCurrent = model.electricityConnection().currentSensor().reading();
	/**Providing drivers: Homematic*/
	@Existence(required = CreateMode.OPTIONAL)
	public final FrequencyResource mmxFrequency = model.electricityConnection().frequencySensor().reading();
	
	/**
	 * Constructor for the access pattern. This constructor is invoked by the framework.
	 */
	public GenericSwitchboxPattern(Resource device) {
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
