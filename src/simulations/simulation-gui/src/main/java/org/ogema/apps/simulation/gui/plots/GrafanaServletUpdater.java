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
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur F�rderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS Fraunhofer ISE Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.apps.simulation.gui.plots;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ogema.apps.simulation.gui.Utils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;
	
public class GrafanaServletUpdater implements TimerListener {
	
	private final List<SimulationProvider<?>> providers;
	@SuppressWarnings("rawtypes")
	private final Map<String,Map> panels;
	@SuppressWarnings("rawtypes")
	private final Map<String,Map> individualPanels;
	private final ResourceAccess ra;
	private final OgemaLogger logger;
	
	@SuppressWarnings("rawtypes")
	public GrafanaServletUpdater(List<SimulationProvider<?>> providers,Map<String,Map> panels, Map<String,Map> individualPanels,ApplicationManager am) {
		this.providers = providers;
		this.panels = panels;
		this.individualPanels = individualPanels;
		this.ra =am.getResourceAccess();
		this.logger = am.getLogger();
	}
	
	@Override
	public void timerElapsed(Timer timer) {
		panels.clear(); 
		synchronized(providers) {
			Iterator<SimulationProvider<?>> it = providers.iterator();
			while (it.hasNext()) {
				SimulationProvider<?> provider = it.next();
				List<? extends Resource> objects = provider.getSimulatedObjects();
				if (objects == null || objects.isEmpty()) continue;
				Iterator<? extends Resource> resIt = objects.iterator();
				while(resIt.hasNext()) {
					Resource res = resIt.next();
					String loc = res.getLocation();
					List<SimulatedQuantity> simulatedQuantities = provider.getSimulatedQuantities(loc);
					if (simulatedQuantities == null || simulatedQuantities.isEmpty()) continue;
					Iterator<SimulatedQuantity> simQIt = simulatedQuantities.iterator();
					while(simQIt.hasNext()) {
						SingleValueResource sv = simQIt.next().value();
						SingleValueResource svLoc = ra.getResource(sv.getLocation());  // logging is bound to path not to location
						if (!Utils.getInstance().isLogging(svLoc)) continue;
						String type = sv.getResourceType().getSimpleName();
						if (!panels.containsKey(type)) {
							Map<String, List<Resource>> panel = new LinkedHashMap<String, List<Resource>>();
							List<Resource> list = new LinkedList<Resource>();
							panel.put(type, list);
							panels.put(type, panel);
						}
						@SuppressWarnings("unchecked")
						Map<String, List<Resource>> panel = panels.get(type);
						List<Resource> list = panel.get(type);
						if (!list.contains(svLoc)) list.add(svLoc); 
						Map<String, List<Resource>> indpanel = new LinkedHashMap<String, List<Resource>>();
						List<Resource> indlist = new LinkedList<Resource>();
						indlist.add(svLoc);
						indpanel.put(svLoc.getLocation(), indlist);
						individualPanels.put(svLoc.getLocation(), indpanel);
					}
					
				}			
			}
		}
		logger.debug("Simulation GUI panels updated {}", panels); 
	}
	


	
}

