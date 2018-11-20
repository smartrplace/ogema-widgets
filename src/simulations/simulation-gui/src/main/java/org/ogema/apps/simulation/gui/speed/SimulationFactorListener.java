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
package org.ogema.apps.simulation.gui.speed;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ogema.core.administration.AdministrationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.core.resourcemanager.pattern.PatternListener;

public class SimulationFactorListener implements PatternListener<SimulationFactorPattern> {
	
	private final AdministrationManager admin;
	private final OgemaLogger logger;
	private final List<SimulationFactorPattern> resources = new LinkedList<SimulationFactorPattern>();
	private final ResourceValueListener<FloatResource> listener = new ResourceValueListener<FloatResource>() {

		@Override
		public void resourceChanged(FloatResource resource) {
			float factor = resource.getValue();
			if (factor > 0) { 
				admin.getFrameworkClock().setSimulationFactor(factor);
				logger.info("Simulation speed changed: " + factor);
			}
			else logger.warn("Could not change simulation speed; value not allowed: " + factor);
		}
	};

	public SimulationFactorListener(AdministrationManager admin, OgemaLogger logger) {
		this.admin = admin;
		this.logger = logger;
	}
	
	@Override
	public void patternAvailable(SimulationFactorPattern resource) {
		resources.add(resource);
		resource.factor.addValueListener(listener);
		listener.resourceChanged(resource.factor);
	}

	@Override
	public void patternUnavailable(SimulationFactorPattern resource) {
		resources.remove(resource);
		resource.factor.removeValueListener(listener);
	}
	
	public void destroy() {
		Iterator<SimulationFactorPattern> it = resources.iterator();
		while(it.hasNext()) {
			SimulationFactorPattern res = it.next();
			res.factor.removeValueListener(listener);
		}
		resources.clear();
	}

}
