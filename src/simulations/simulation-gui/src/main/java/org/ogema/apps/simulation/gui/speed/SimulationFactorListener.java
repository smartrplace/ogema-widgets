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
