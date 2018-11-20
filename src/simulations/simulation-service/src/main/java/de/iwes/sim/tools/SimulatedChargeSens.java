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
package de.iwes.sim.tools;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.VoltageResource;

/**
 * Simulation code for PV plant described by a PVPattern.
 *
 * @author David Nestle, Fraunhofer IWES
 */
// FIXME does not belong in this bundle, move to appropriate place
@Deprecated
public class SimulatedChargeSens {
	public static final long BATTERY_LIFETIME = (1000*3600*24*30)*24; //last value is number of months
	public static final float VOLTAGE_RANGE_UPPER = 2.6f;
	public static final float VOLTAGE_RANGE_LOWER = 2.2f;
	
    //private final ApplicationManager appMan;
    //private final OgemaLogger logger;
    private final FloatResource batteryStatus;
    private final VoltageResource batteryInternalVoltage;
    private final FloatResource internalChargeState;
    
    private double internalLossPerMilliSecond;
    private double internalChargeStateDouble;
    
    public SimulatedChargeSens(ApplicationManager appMan, FloatResource batteryStatus,
    		VoltageResource batteryInternalVoltage, FloatResource internalChargeState) {
        //this.appMan = appMan;
        //this.logger = appMan.getLogger();
        this.internalChargeState = internalChargeState;
        if(!internalChargeState.exists()) {
        	internalChargeState.<FloatResource> create().setValue(0.950001f);
        }
        this.batteryInternalVoltage = batteryInternalVoltage;
        this.batteryStatus = batteryStatus;
        internalChargeStateDouble = internalChargeState.getValue();
    }

	public void step(long stepSize) {
		internalChargeStateDouble = internalChargeStateDouble - stepSize*internalLossPerMilliSecond;
		internalChargeState.setValue((float) internalChargeStateDouble);
		if((batteryInternalVoltage != null)&&(batteryStatus.isActive())) {
			float internalState = (float) (VOLTAGE_RANGE_LOWER + internalChargeStateDouble*(VOLTAGE_RANGE_UPPER - VOLTAGE_RANGE_LOWER));
			float voltageVal = SimUtils.roundToDec(internalState, 1);
			batteryInternalVoltage.setValue(voltageVal);
		}
		if((batteryStatus != null)&&(batteryStatus.isActive())) {
			float stateVal = SimUtils.roundToDec((float) internalChargeStateDouble, 2);
			batteryStatus.setValue(stateVal);
		}
	}
}
