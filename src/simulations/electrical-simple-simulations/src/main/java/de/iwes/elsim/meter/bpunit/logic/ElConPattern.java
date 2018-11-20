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
package de.iwes.elsim.meter.bpunit.logic;

import org.ogema.core.model.Resource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.devices.connectiondevices.ElectricityConnectionBox;

import de.iwes.pattern.management2.backup.copy.ManagedResourcePattern;
import de.iwes.util.resource.ResourceHelper;

/**
 * Creation pattern for the battery simulated in this application.
 *
 * @author Timo Fischer, Fraunhofer IWES
 */
public class ElConPattern extends ManagedResourcePattern<ElectricityConnection, Object> {

	public PowerResource reading = model.powerSensor().reading();

	//private MeterSimulation c;
    
    /**
     * Default constructor required by OGEMA. Do not change.
     *
     * @param root root resource of the pattern; stored in this.model.
     */
    public ElConPattern(Resource root) {
        super(root);
    }

	@Override
	public boolean accept() {
		//sort out own meter and building property meters
		if(ResourceHelper.hasParentAboveType(model, ElectricityConnectionBox.class) >= 1) {
			return false;
		}
		/*List<MeterPatternI> ml = context.getSimulatedDevicesI();
		for(MeterPatternI mp: ml) {
			if(mp.getConsumptionPower().equalsLocation(reading)) {
				return false;
			}
		}
		if(ResourceHelper.hasParentLevelsAboveType(model, BuildingPropertyUnit.class, 2)) {
			return false;
		}*/
		return true;
	}
 }
