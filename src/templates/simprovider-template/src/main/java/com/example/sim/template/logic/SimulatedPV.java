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
package com.example.sim.template.logic;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;

import com.example.sim.template.device.TemplatePattern;

import de.iwes.util.format.StringFormatHelper;

public class SimulatedPV {

    private static final long MAX_DEVIATION = 1000;
	private static final long CYCLE_TIME = 15000;

    private final ApplicationManager appMan;
    private final OgemaLogger logger;
    private final TemplatePattern pv;
    
    public SimulatedPV(ApplicationManager appMan, TemplatePattern pv) {
        this.appMan = appMan;
        this.logger = appMan.getLogger();
        this.pv = pv;
    }
    
    private boolean direction = true; 
    
    public void updateState(long timeStep) {
    	 // TODO replace example code by your simulation logic
 		float stepSize = MAX_DEVIATION * timeStep/CYCLE_TIME;
 		if(!direction) {
 			stepSize = -stepSize;
 		}
 		float av = pv.maxPower.getValue() / 2;
 		if(direction && (pv.powerReading.getValue() + stepSize > av + MAX_DEVIATION)) {
 			direction = !direction;
 			logger.debug("TemplateSim: Changing direction(-) for resource {}", pv.powerReading);
 		} else if((!direction)&& (pv.powerReading.getValue() + stepSize < av - MAX_DEVIATION)) {
 			direction = !direction;
 			logger.debug("TemplateSim: Changing direction(+) for resource {}", pv.powerReading);
 		} else {
 			pv.powerReading.setValue(pv.powerReading.getValue() + stepSize);
 			if (logger.isDebugEnabled()) {
 				logger.debug("TemplateSim: New simulated value " + pv.powerReading.getValue() + " time:" 
 							+ StringFormatHelper.getTimeOfDayInLocalTimeZone(appMan.getFrameworkTime()));
 			}
 		}
    }
     
    public void close() {
    	// nothing to be done, in this case
    }
}
