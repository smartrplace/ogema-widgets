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
package de.iwes.elsim;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.tools.simulation.service.api.SimulationService;

import de.iwes.elsim.battery.BatterySimulation;
import de.iwes.elsim.meter.bpunit.MeterBPUnitSimulation;
import de.iwes.elsim.meter.elconbox.MeterElConBoxSimulation;
import de.iwes.elsim.pv.PVSimulation;

/**
 * Template OGEMA simulation provider app
 */
@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class ElectricalSimpleSimulationApp implements Application {
	public static final String urlPath = "org/smartrplace/external/electricalsimplesimulationv2";

    private OgemaLogger log;
//    private ApplicationManager appManager;
    private BatterySimulation firstSimulation;
    private PVSimulation pvSimulation;
    private MeterBPUnitSimulation meterSimulationBPropUnit;
    private MeterElConBoxSimulation meterSimulationElConBox;

    @Reference
    private SimulationService simulationService;
    
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
        firstSimulation = new BatterySimulation(appManager);
        simulationService.registerSimulationProvider(firstSimulation);
        pvSimulation = new PVSimulation(appManager);
        simulationService.registerSimulationProvider(pvSimulation);
        meterSimulationBPropUnit = new MeterBPUnitSimulation(appManager);
        simulationService.registerSimulationProvider(meterSimulationBPropUnit);
        meterSimulationElConBox = new MeterElConBoxSimulation(appManager);
        simulationService.registerSimulationProvider(meterSimulationElConBox);
     }

     /*
     * Callback called when the application is going to be stopped.
     */
    @SuppressWarnings("unchecked")
	@Override
    public void stop(AppStopReason reason) {
    	if (firstSimulation != null)
    		simulationService.unregisterSimulationProvider(firstSimulation);
    	firstSimulation = null;
    	if (pvSimulation != null)
    		simulationService.unregisterSimulationProvider(pvSimulation);
    	pvSimulation = null;
    	if (meterSimulationBPropUnit != null)
    		simulationService.unregisterSimulationProvider(meterSimulationBPropUnit);
    	meterSimulationBPropUnit = null;
    	if (meterSimulationElConBox != null)
    		simulationService.unregisterSimulationProvider(meterSimulationElConBox);
    	meterSimulationElConBox = null;
        log.info("{} stopped", getClass().getName());
//        appManager = null;
        log = null;
    }
    
}
