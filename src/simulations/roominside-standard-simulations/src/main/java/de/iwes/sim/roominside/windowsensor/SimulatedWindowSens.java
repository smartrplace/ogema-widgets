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
package de.iwes.sim.roominside.windowsensor;

import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.apps.roomsim.service.api.helpers.RoomInsideLogicBase;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.model.sensors.DoorWindowSensor;
import org.ogema.tools.simulation.service.api.SimulationProvider;

import de.iwes.sim.tools.SimulatedChargeSens;

/**
 * Simulation code for PV plant described by a {@link PVPattern}.
 *
 * @author David Nestle, Fraunhofer IWES
 */
public class SimulatedWindowSens extends RoomInsideLogicBase<DoorWindowSensor> {

    private final ApplicationManager appMan;
    private final OgemaLogger logger;
    private final HMWindowSensorPattern targetPattern;
    private final HMWindowSensorConfigPattern configPattern;
    
    private final SimulatedChargeSens chargeSens;
    
    public SimulatedWindowSens(ApplicationManager appMan, HMWindowSensorPattern pv,
    		HMWindowSensorConfigPattern configPattern,
    		RoomSimulationService roomSimService, SimulationProvider<DoorWindowSensor> provider) {
    	super(roomSimService, provider, configPattern.model.target(), configPattern.roomSimulationToConnect,
    			configPattern);
        this.appMan = appMan;
        this.logger = appMan.getLogger();
        this.targetPattern = pv;
        this.configPattern = configPattern;
        chargeSens = new SimulatedChargeSens(appMan, targetPattern.batteryStatus,
        		targetPattern.batteryVoltage, configPattern.internalChargeState);
    }

	@Override
	public void step(long stepSize) {
		if(targetPattern.open.getValue()) {
			if(configPattern.thermalLossWhenOpen.isActive()) {
				float thLoss = configPattern.thermalLossWhenOpen.getValue();
				if(Float.isNaN(thLoss)) thLoss = 0;
				roomSim.addThermalEnergy(thLoss*stepSize*0.001f);
				//float co2cur = roomSim.getCO2().getValue();
				//roomSim.addCO2((co2cur-380)*0.0001f*stepSize*0.001f);
			}
		}
		chargeSens.step(stepSize);
		logger.debug("State of window sensor:"+targetPattern.open.getValue());
	}

	@Override
	public void close() {
		// nothing to do here
	}
}
