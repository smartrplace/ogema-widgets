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
package de.iwes.sim.roomsimservice;

import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.apps.roomsim.service.api.RoomInsideSimulation;
import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.apps.roomsim.service.api.SingleRoomSimulation;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.model.locations.Room;
import org.ogema.tools.simulation.service.api.SimulationService;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.iwes.sim.roomsimservice.device.RoomsimulationServiceSimulation;

/**
 * Template OGEMA simulation provider app
 */
// FIXME does not shut down properly
@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class RoomsimulationServiceApp implements Application, RoomSimulationService {
	public static final String urlPath = "org/smartrplace/external/roomsimulationservicev2";

    private OgemaLogger log;
//    private ApplicationManager appManager;
    private RoomsimulationServiceSimulation firstSimulation;

    @Reference
    private SimulationService simulationService;

    @Reference
    SimulationServiceAdmin simulationServiceAdmin;    
    
   	private BundleContext bc;
   	protected ServiceRegistration<RoomSimulationService> sr = null;

   	@Activate
    void activate(BundleContext bc) {
    	this.bc = bc;
    }
    
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
        firstSimulation = new RoomsimulationServiceSimulation(appManager, simulationServiceAdmin);
        simulationService.registerSimulationProvider(firstSimulation);
        
		sr = bc.registerService(RoomSimulationService.class, this, null);
     }

     /*
     * Callback called when the application is going to be stopped.
     */
    @SuppressWarnings("unchecked")
	@Override
    public void stop(AppStopReason reason) {
    	final ServiceRegistration<?> sr = this.sr;
   		if (sr!=null) {
   			// unregister may block in stop method
   			new Thread(new Runnable() {
				
				@Override
				public void run() {
					sr.unregister();
				}
			}).start();
   		}
    	if (firstSimulation != null && simulationService != null) {
    		firstSimulation.stop();
    		simulationService.unregisterSimulationProvider(firstSimulation);
    	}
    	firstSimulation = null;
        log.info("{} stopped", getClass().getName());
//        appManager = null;
        log = null;
        this.sr = null;
    }

	@Override
	public SingleRoomSimulation registerRoomSimulation(Room room, RoomInsideSimulation<?> listener) {
		return firstSimulation.registerRoomSimulation(room, listener);
	}

	@Override
	public boolean unregisterRoomSimulation(Room room, RoomInsideSimulation<?> listener) {
		return firstSimulation.unregisterRoomSimulation(room, listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Room> getAvailableRooms() {
		return (List<Room>)((List<?>)firstSimulation.getSimulatedObjects());
	}

	//@Override
	//public List<SingleRoomSimulation> getAvailableRoomSimulations() {
	//	return firstSimulation.getAvailableRoomSimulations();
	//}
}
