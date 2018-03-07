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

import org.ogema.core.model.Resource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.devices.generators.PVPlant;
import org.ogema.model.sensors.PowerSensor;

public class TemplatePattern extends ResourcePattern<PVPlant> { 
	
    @Existence(required=CreateMode.OPTIONAL)
    public PowerResource maxPower = model.ratedPower().upperLimit();
    
	@Existence(required = CreateMode.OPTIONAL)
	public final ElectricityConnection electricityConnection = model.electricityConnection();
	
    @Existence(required=CreateMode.OPTIONAL)
    public final PowerSensor powerSensor = electricityConnection.powerSensor();
	
    @Existence(required=CreateMode.OPTIONAL)
    public final PowerResource powerReading = powerSensor.reading();

	public TemplatePattern(Resource device) {
		super(device);
	}
	
	@Override
	public boolean accept() {
		// no custom condition, we accept all pattern matches
		return true; 
	}
}
