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
 * Copyright 2014 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package com.example.sim.template;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.tools.simulation.service.api.SimulationService;
import com.example.sim.template.device.TemplateSimulation;

@Component(specVersion = "1.2")
@Service(Application.class)
public class TemplateApp implements Application {

    private OgemaLogger log;
//    private ApplicationManager appManager;
    private TemplateSimulation firstSimulation;

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
        firstSimulation = new TemplateSimulation(appManager);
        simulationService.registerSimulationProvider(firstSimulation);
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
        log.info("{} stopped", getClass().getName());
//        appManager = null;
        log = null;
    }
    
}
