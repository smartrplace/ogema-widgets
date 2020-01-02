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
package de.iwes.sim.roominside.std;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.tools.simulation.service.api.SimulationService;

import de.iwes.sim.roominside.hmmotionsensor.HMMotionSensorSimulation;
import de.iwes.sim.roominside.hmthsensor.HMTHSensorSimulation;
import de.iwes.sim.roominside.tempsens.TempsensSimulation;
import de.iwes.sim.roominside.thermostat.ThermostatSimulation;
import de.iwes.sim.roominside.windowsensor.HMWindowSensorSimulation;

/**
 * Template OGEMA simulation provider app
 */
@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class RoominsideComponentApp implements Application {
	public static final String urlPath = "org/smartrplace/external/roominsidestandardsimulations";

    private OgemaLogger log;
//    private ApplicationManager appManager;
    private ThermostatSimulation firstSimulation;
    private TempsensSimulation tempsensSimulation;
    private HMTHSensorSimulation thsensSimulation;
    private HMMotionSensorSimulation motionsensSimulation;
    private HMWindowSensorSimulation windowsensSimulation;

    @Reference
    private SimulationService simulationService;

    @Reference
    RoomSimulationService roomSimulationProvider;
    
   /*
     * This is the entry point to the application.
     */
 	@SuppressWarnings("unchecked")
	@Override
    public void start(ApplicationManager appManager) {

        // Remember framework references for later.
//        this.appManager = appManager;
        log = appManager.getLogger();

        // 
        firstSimulation = new ThermostatSimulation(appManager, roomSimulationProvider);
        simulationService.registerSimulationProvider(firstSimulation);
        tempsensSimulation = new TempsensSimulation(appManager, roomSimulationProvider);
        simulationService.registerSimulationProvider(tempsensSimulation);
        thsensSimulation = new HMTHSensorSimulation(appManager, roomSimulationProvider);
        simulationService.registerSimulationProvider(thsensSimulation);
        motionsensSimulation = new HMMotionSensorSimulation(appManager, roomSimulationProvider);
        simulationService.registerSimulationProvider(motionsensSimulation);
        windowsensSimulation = new HMWindowSensorSimulation(appManager, roomSimulationProvider);
        simulationService.registerSimulationProvider(windowsensSimulation);
     }

     /*
     * Callback called when the application is going to be stopped.
     */
    @SuppressWarnings("unchecked")
	@Override
    public void stop(AppStopReason reason) {
    	final SimulationService simulationService = this.simulationService;
    	if (firstSimulation != null) {
    		firstSimulation.stop();
    		if (simulationService != null)
    			simulationService.unregisterSimulationProvider(firstSimulation);
    	}
    	firstSimulation = null;
    	if (tempsensSimulation != null) {
    		tempsensSimulation.stop();
    		if (simulationService != null)
    			simulationService.unregisterSimulationProvider(tempsensSimulation);
    	}
    	tempsensSimulation = null;
    	if (thsensSimulation != null) {
    		thsensSimulation.stop();
    		if (simulationService != null)
    			simulationService.unregisterSimulationProvider(thsensSimulation);
    	}
    	thsensSimulation = null;
    	if (motionsensSimulation != null) {
    		motionsensSimulation.stop();
    		if (simulationService != null)
    			simulationService.unregisterSimulationProvider(motionsensSimulation);
    	}
    	motionsensSimulation = null;
    	if (windowsensSimulation != null) {
    		windowsensSimulation.stop();
    		if (simulationService != null)
    			simulationService.unregisterSimulationProvider(windowsensSimulation);
    	}
    	windowsensSimulation = null;
        log.info("{} stopped", getClass().getName());
//        appManager = null;
        log = null;
    }
    
}
