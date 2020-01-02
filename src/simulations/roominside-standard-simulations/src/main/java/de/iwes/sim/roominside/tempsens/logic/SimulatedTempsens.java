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
package de.iwes.sim.roominside.tempsens.logic;

import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.apps.roomsim.service.api.helpers.RoomInsideLogicBase;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.simulation.service.api.SimulationProvider;

import de.iwes.sim.roominside.tempsens.TempsensConfigPattern;
import de.iwes.sim.roominside.tempsens.TempsensPattern;

/**
 * Simulation code for PV plant described by a {@link PVPattern}.
 *
 * @author David Nestle, Fraunhofer IWES
 */
public class SimulatedTempsens extends RoomInsideLogicBase<TemperatureSensor> {

    private final ApplicationManager appMan;
    private final OgemaLogger logger;
    private final TempsensPattern targetPattern;
    private final TempsensConfigPattern configPattern;
    
    public SimulatedTempsens(ApplicationManager appMan, TempsensPattern pv,
    		TempsensConfigPattern configPattern,
    		RoomSimulationService roomSimService, SimulationProvider<TemperatureSensor> provider) {
    	super(roomSimService, provider, configPattern.model.target(), configPattern.roomSimulationToConnect,
    			configPattern);
        this.appMan = appMan;
        this.logger = appMan.getLogger();
        this.targetPattern = pv;
        this.configPattern = configPattern;
    }

	@Override
	public void step(long stepSize) {
		targetPattern.reading.setKelvin(roomSim.getTemperature().getKelvin());
		logger.debug("Set temperature sensor:"+roomSim.getTemperature().getKelvin());
	}

	@Override
	public void close() {
		// nothing to do here
	}
}
