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
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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

