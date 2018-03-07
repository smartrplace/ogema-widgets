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
package com.example.sim.template.device;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.simulation.service.api.model.SimulatedQuantity;

public class GeneratedPowerValue implements SimulatedQuantity {

	private final TemplatePattern pattern;
	
	public GeneratedPowerValue(TemplatePattern pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public String getId() {
		return "Current PV power generation";
	}

	@Override
	public String getDescription() {
		return "Power in W";
	}

	@Override
	public SingleValueResource value() {
		return pattern.powerReading;
	}

}
