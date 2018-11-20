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
package de.iwes.elsim.hmswitchbox;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.tools.resourcemanipulator.timer.CountDownDelayedExecutionTimer;

import de.iwes.elsim.battery.BatteryPattern;

/**
 * Simulation code for a battery described by a {@link BatteryPattern}.
 *
 * @author Timo Fischer, Fraunhofer IWES
 */
public class SimulatedSwitchboxHM {

    public static final long DEFAULT_TIMER_PERIOD = 5000l;

    final ApplicationManager m_appMan;
    final HMSwitchboxPattern targetPattern;
    final HMSwitchboxConfigPattern configPattern;
    private ResourceValueListener<BooleanResource> targetTempListener;

    public SimulatedSwitchboxHM(ApplicationManager appMan, HMSwitchboxPattern switchbox,
    		HMSwitchboxConfigPattern configPattern) {
        m_appMan = appMan;
        this.targetPattern = switchbox;
        this.configPattern = configPattern;
        
        targetTempListener = new ResourceValueListener<BooleanResource>() {
			@Override
			public void resourceChanged(BooleanResource resource) {
				if(targetPattern.stateFeedback.isActive()) {
					new CountDownDelayedExecutionTimer(SimulatedSwitchboxHM.this.m_appMan, 10000) {
						@Override
						public void delayedExecution() {
							targetPattern.stateFeedback.setValue(targetPattern.stateControl.getValue());
						}
					};
				}
			}
		};
        targetPattern.stateControl.addValueListener(targetTempListener, true);  

    }

    public void start() {
     }

    public void close() {
     }

    /*final ResourceValueListener<FloatResource> controlListener = new ResourceValueListener<FloatResource>() {

        @Override
        public void resourceChanged(FloatResource resource) {
            updateState();
        }
    };*/

    /**final TimerListener timerListener = new TimerListener() {

        @Override
        public void timerElapsed(Timer timer) {
            updateState();
        }
    };*/
    
     public void updateState(long dt_milli) {
        //final long tNow = m_appMan.getFrameworkTime();
        //final float dt = 0.001f * (tNow - m_lastTime); // time in seconds (SI unit)
        //m_lastTime = tNow;
    	//final float dt = 0.001f*dt_milli;
        
        // set the power
    	if((targetPattern.stateFeedback.getValue() && configPattern.connectedDeviceSwitchedOn.getValue())) {
    		targetPattern.mmxPower.setValue(configPattern.connectedDevicePower.getValue());
    	} else {
    		targetPattern.mmxPower.setValue(0);
        }
    	//TODO: Add frequency, voltage, current
    }
}
