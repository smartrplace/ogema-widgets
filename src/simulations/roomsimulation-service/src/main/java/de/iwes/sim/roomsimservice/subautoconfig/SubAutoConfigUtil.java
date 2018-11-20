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
package de.iwes.sim.roomsimservice.subautoconfig;

import java.util.List;

import org.ogema.apps.roomsim.service.api.helpers.RoomInsideConfigStd;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.model.locations.Location;
import org.ogema.model.locations.Room;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.SimulationServiceAdmin;
import org.ogema.tools.simulation.service.apiplus.SimulationConfigurationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.sim.roomsimservice.RoomsimulationServiceApp;

public class SubAutoConfigUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(RoomsimulationServiceApp.class);
	
	public static void addRooms(SimulationProvider<Room> roomSimProvider, ApplicationManager appMan) {
		List<Room> roomList = appMan.getResourceAccess().getToplevelResources(Room.class);
		for(Room room: roomList) {
			if(!room.name().exists()) continue;
			String name = room.name().getValue().toLowerCase();
			if(name.contains("removed")) continue;
			ResourceList<SimulationConfigurationModel> simConfigGlobal = appMan.getResourceAccess().getResource("OGEMASimulationConfiguration");
			//List<Room> existing = roomSimProvider.getSimulatedObjects();
			boolean found = false;
			if(simConfigGlobal != null) for(SimulationConfigurationModel exr: simConfigGlobal.getAllElements()) {
				if(exr.target().equalsLocation(room)) {
					found = true;
					break;
				}
			}
			if(found) continue;
			roomSimProvider.createSimulatedObject(room.getLocation());
			logger.debug("CreateSimulatedObject(Room): "+room.getLocation());
		}
	}
	/*@SuppressWarnings("unchecked")
	public static boolean configureHMThermostat(Room room, SimulationServiceAdmin admin, ApplicationManager appMan) {
		SimulationProvider<Thermostat> thermostatProvider = null;
		for(SimulationProvider<? extends Resource> provider: admin.getAllSimulationProviders()) {
			if(provider.getProviderId().equals("Thermostat simulation")) {
				thermostatProvider = (SimulationProvider<Thermostat>)provider;
				break;
			}
		}
		if(thermostatProvider == null) return false;
		boolean found = false;
		List<Location> refs = room.getReferencingResources(Location.class);
		for(Location loc: refs) {
			if(loc.getParent() instanceof Thermostat) {
				Thermostat th = loc.getParent();
				Resource res = thermostatProvider.createSimulatedObject(th.getLocation());
				System.out.println("CreateSimulatedObject(Thermostat): "+th.getLocation());
				if (res != null) {
					found = true;
				}
			}
		}
		return found;
	}*/

	@SuppressWarnings("unchecked")
	/**
	 * @param room
	 * @param admin
	 * @param appMan
	 * @param resourceType
	 * @param providerId
	 * @param subClassRequired may be null, if set an element of such a class in the sub-tree
	 * is required to fit
	 * @return
	 */
	public static <T extends Resource> boolean configureHMDeviceSimulation(Room room, SimulationServiceAdmin admin, ApplicationManager appMan,
			Class<T> resourceType, String providerId, Class<? extends Resource> subClassRequired) {
		SimulationProvider<T> simProvider = null;
		for(SimulationProvider<? extends Resource> provider: admin.getAllSimulationProviders()) {
			if(provider.getProviderId().equals(providerId)) {
				simProvider = (SimulationProvider<T>)provider;
				break;
			}
		}
		if(simProvider == null) {
			appMan.getLogger().warn("Simulation provider for Auto-configuration of in-room-sim not found: "+providerId);
			return false;
		}
		boolean found = false;
		List<Location> refs = room.getReferencingResources(Location.class);
		for(Location loc: refs) {
			if(resourceType.isAssignableFrom(loc.getParent().getClass())) {
				T th = loc.getParent();
				if(th == null) continue;
				if((subClassRequired != null)&&th.getSubResources(subClassRequired, true).isEmpty()) {
					continue;
				}
				Resource res = simProvider.createSimulatedObject(th.getLocation());
				logger.debug("CreateSimulatedObject: "+th.getLocation());
				if (res != null) {
					RoomInsideConfigStd simConfig = getSimConfigResource(res, appMan.getResourceAccess(),
							RoomInsideConfigStd.class);
					if(simConfig != null) {
						simConfig.roomSimulationToConnect().setAsReference(room);
						simProvider.startSimulation(th.getLocation());
						logger.debug("Set Room for "+res.getLocation());
						found = true;
					} else {
						logger.warn("Room could not be set for "+res.getLocation());
					}
				}
			}
		}
		return found;
	}
	
	public static <T extends SimulationConfigurationModel> T getSimConfigResource(Resource target, ResourceAccess resAcc,
			Class<T> simClass) {
		List<T> foundList = resAcc.getResources(simClass);
		for(T found: foundList) {
			if(found.target().equalsLocation(target)) {
				return found;
			}
		}
		return null;
	}
}
