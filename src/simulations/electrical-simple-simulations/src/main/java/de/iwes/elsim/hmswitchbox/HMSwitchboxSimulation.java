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
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.elsim.hmswitchbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.model.Resource;
import org.ogema.model.devices.sensoractordevices.SingleSwitchBox;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;
import org.ogema.tools.simulation.service.apiplus.SimulationBase;

import de.iwes.util.resource.ValueResourceHelper;

/**
 * A simulation provider, that simulates e.g. a particular sort of devices. 
 * In this example, a the power generated by a PV plant is simulated.  
 */
public class HMSwitchboxSimulation extends SimulationBase<HMSwitchboxConfigPattern, HMSwitchboxPattern> {
	
	private static final long SIM_UPDATE_INTERVAL = 4000;
	@Override
	protected long getDefaultUpdateInterval(){return SIM_UPDATE_INTERVAL;}

	// callbacks are guaranteed to come in the same thread, no need to synchronize on the map
	private final Map<String, SimulatedSwitchboxHM> simulatedObjects = new HashMap<>();
	
	public HMSwitchboxSimulation(ApplicationManager am) {
		super(am, HMSwitchboxPattern.class, true, HMSwitchboxConfigPattern.class);
	}	

	@Override
	public String getProviderId() {
		return "Switchbox simulation";
	}
	
	@Override
	public Class<? extends Resource> getSimulatedType() {
		return SingleSwitchBox.class;
	}

	@Override
	public void buildConfigurations(HMSwitchboxPattern pattern, List<SimulationConfiguration> cfgs, HMSwitchboxConfigPattern simPattern) {
		// Add here configuration values that can be edited by the user, see example below
		DevicePowerConfig ratpow = new DevicePowerConfig(simPattern.connectedDevicePower);
		cfgs.add(ratpow);
		DeviceSwitch devsw = new DeviceSwitch(simPattern.connectedDeviceSwitchedOn);
		cfgs.add(devsw);
		InternalRelay intsw = new InternalRelay(pattern.stateFeedback);
		cfgs.add(intsw);
	}
	
	@Override
	public void buildQuantities(HMSwitchboxPattern pattern, List<SimulatedQuantity> quantities, HMSwitchboxConfigPattern simPattern) {
		// Add here configuration values that can be edited by the user, see example below
	}
	
	@Override
	public String getDescription() {
		return "Simulated electrical single switch box (Homematic)";
	}
	
	/** 
	 * Perform the actual simulation here. The targetPattern points to the simulated resource (typically a device). 
	 * The configPattern points to the simulation configuration resource indicating the 
	 * simulation time interval etc.
	 * @param timeStep time since last simulation step in milliseconds
	 */
	@Override
	public void simTimerElapsed(HMSwitchboxPattern targetPattern, HMSwitchboxConfigPattern configPattern, Timer t, long timeStep) {
		SimulatedSwitchboxHM logic = simulatedObjects.get(targetPattern.model.getLocation());
		logic.updateState(timeStep);
	}

	
	@Override
	protected void initSimulation(HMSwitchboxPattern targetPattern, HMSwitchboxConfigPattern configPattern) {
		// initialize and activate also the optional fields in targetPattern
		
		//init battery simulation
		boolean doActivate = false;
		if(	ValueResourceHelper.setIfNewOrZero(targetPattern.mmxPower, 0) |
					ValueResourceHelper.setIfNew(configPattern.connectedDevicePower, 20) |
					ValueResourceHelper.setIfNew(configPattern.connectedDeviceSwitchedOn, false) |
					ValueResourceHelper.setIfNew(targetPattern.isControllable, true) |
					ValueResourceHelper.setIfNew(targetPattern.stateFeedback, false) |
					ValueResourceHelper.setIfNew(targetPattern.stateControl, false)) {
			doActivate = false;
		}
		if(doActivate) {
			targetPattern.model.activate(true);
		}		
		
		SimulatedSwitchboxHM logic = new SimulatedSwitchboxHM(appManager, targetPattern, configPattern);
		simulatedObjects.put(targetPattern.model.getLocation(), logic);
	}
	
	@Override
	protected void removeSimulation(HMSwitchboxPattern targetPattern, HMSwitchboxConfigPattern configPattern) {
		SimulatedSwitchboxHM logic = simulatedObjects.remove(targetPattern.model.getLocation());
		logic.close();
	}
}
