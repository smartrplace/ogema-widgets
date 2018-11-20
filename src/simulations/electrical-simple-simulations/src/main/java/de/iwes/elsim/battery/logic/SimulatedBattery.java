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
package de.iwes.elsim.battery.logic;

import org.ogema.core.application.ApplicationManager;

import de.iwes.elsim.battery.BatteryPattern;

/**
 * Simulation code for a battery described by a {@link BatteryPattern}.
 *
 * @author Timo Fischer, Fraunhofer IWES
 */
public class SimulatedBattery {

    public static final long DEFAULT_TIMER_PERIOD = 5000l;

    public static final float KWH_TO_J = 1000.f * 60.f * 60.f;
    public static final float J_TO_KWH = 1.f/KWH_TO_J;
    public static final float EFFICIENCY = 0.81f;
    
    public final static float CAPACITY_KWH = 20.f;
    public final static float MAX_POWER = 10000f;
        
    final ApplicationManager m_appMan;
    final BatteryPattern m_battery;
    //final Timer m_timer;
    //long m_lastTime = 0;

    public SimulatedBattery(ApplicationManager appMan, BatteryPattern battery) {
        m_appMan = appMan;
        m_battery = battery;
        //m_timer = appMan.createTimer(DEFAULT_TIMER_PERIOD, timerListener);
    }

    public void start() {
        //m_lastTime = m_appMan.getFrameworkTime();
        //m_battery.stateControl.addValueListener(controlListener);
        //m_timer.resume();
    }

    public void close() {
        //m_battery.stateControl.removeValueListener(controlListener);
        //m_timer.stop();
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
    
    private float getRelativeStateControl() {
    	if(m_battery.stateControl.isActive()) {
    		return m_battery.stateControl.getValue();
    	} else {
    		float val = m_battery.stateControlPVNetz.getValue();
    		if(val > 0) {
    			return val / m_battery.maxPower.getValue();
    		} else {
    			return - val / m_battery.minPower.getValue();
    		}
    	}
    }
    
    public void updateState(long dt_milli) {
        //final long tNow = m_appMan.getFrameworkTime();
        //final float dt = 0.001f * (tNow - m_lastTime); // time in seconds (SI unit)
        //m_lastTime = tNow;
    	final float dt = 0.001f*dt_milli; // in seconds
        final float controlSetting = getRelativeStateControl(); //m_battery.stateControl.getValue();
        final float capacity = m_battery.maxE.getValue() - m_battery.minE.getValue();
        final float P = m_battery.powerReading.getValue();
        final float dE = (P>0.f) ? EFFICIENCY * P * dt : P * dt; // unit: Joule
        final float dSoc = dE / capacity;        
        final double selfDischargeEfficiency = Math.pow(m_battery.getSelfDischargeEfficiencyPerSecond(), dt); 
        
        float newSoc = (float) (m_battery.soc.getValue() * selfDischargeEfficiency + dSoc);
        float feedback = controlSetting;
        if (newSoc <= 0.f) {
            newSoc = 0.f;
            if (feedback<0.f) feedback = 0.f;
        } else if (newSoc >= 1.f) {
            newSoc = 1.f;
            if (feedback >0.f) feedback = 0.f;
        }
        
        // set the battery response
        m_battery.soc.setValue(newSoc);
        if(m_battery.stateFeedback.isActive()) {
        	m_battery.stateFeedback.setValue(feedback);
        }
        if (feedback >= 0.) {
            m_battery.powerReading.setValue(feedback * m_battery.maxPower.getValue());
            if (m_appMan.getLogger().isTraceEnabled())
            	m_appMan.getLogger().trace("Simulated battery SOC:"+newSoc+" feedbackP:"+(feedback * m_battery.maxPower.getValue()));
        } else {
            m_battery.powerReading.setValue(feedback * -m_battery.minPower.getValue());
            if (m_appMan.getLogger().isTraceEnabled())
            	m_appMan.getLogger().trace("Simulated battery SOC:"+newSoc+" feedbackP:"+(feedback * -m_battery.minPower.getValue()));
        }
    }
}
