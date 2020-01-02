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
package de.iwes.sim.roominside.thermostat.logic;

import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.logging.OgemaLogger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import de.iwes.sim.roominside.std.GlobalConfigurations;
import de.iwes.sim.roomsimservice.logic.GenericCalculations;

public class RadiatorCalculator {

	private static RadiatorCalculator instance = null;
	//private Logger logger = LoggerFactory.getLogger(getClass());
	final private OgemaLogger logger;
	
	private RadiatorCalculator(OgemaLogger log) {
		this.logger = log;
	}
	
	public static RadiatorCalculator getInstance(OgemaLogger log) {
		if (instance == null) {
			instance = new RadiatorCalculator(log);
		}
		return instance;
	}
	
	public float getNewValue( long lastUpdateTime, long currentTime, float maximumPower, float roomSize, float currentValveSetting, List<SampledValue> historicalValveSettings, List<SampledValue> historicalPowerFlows) {	
		if (lastUpdateTime == 0) {  // just starting the simulation
			return 0;
		}
		long updateTime = currentTime - lastUpdateTime;
		if (historicalValveSettings.size() < 2 || historicalPowerFlows.size() < 2) {	// in this case it is not possible to determine a gradient or recent valve settign
			return maximumPower * roomSize * currentValveSetting * 0.01F; // just starting the simulation
		}
		
		SampledValue svTemp1 = historicalValveSettings.get(0);
		SampledValue svTemp2 = historicalValveSettings.get(historicalValveSettings.size()-1);
		float totalValveGradient = GenericCalculations.getInstance().getGradient(svTemp1,svTemp2);
		float averageValveSetting = GenericCalculations.getInstance().getAverage(historicalValveSettings);
		
		SampledValue svPower1 = historicalPowerFlows.get(0);
		SampledValue svPower2 = historicalPowerFlows.get(historicalPowerFlows.size()-1); // TODO
		float lastPower = svPower2.getValue().getFloatValue();
		long lastPowerTime = svPower2.getTimestamp();
		float totalPowerGradient = GenericCalculations.getInstance().getGradient(svPower1,svPower2);
		float averagePower = GenericCalculations.getInstance().getAverage(historicalPowerFlows);

		float maxPower = maximumPower * roomSize;
		
		if (averageValveSetting > 1 || averageValveSetting< 0) { 
			logger.warn("ValveSettingCalculator: inconsistent valve average calculated: " + averageValveSetting + ". Should lie in [0,1].");
			averageValveSetting = (averageValveSetting < 0) ? 0: 1;
		}
		if (averagePower > maxPower || averagePower< 0) { 
			logger.warn("ValveSettingCalculator: inconsistent valve power calculated: " + averagePower );
			averagePower = (averagePower < 0) ? 0: maxPower;
		}
		float target = maxPower * currentValveSetting;	// TODO maximum power typically also depends on outside temperature
		// actual value should approximate target if valve setting is constant
		float delta = target - lastPower;
		if (updateTime > GlobalConfigurations.RADIATOR_HYSTERESIS) updateTime = GlobalConfigurations.RADIATOR_HYSTERESIS;
		logger.debug("  New radiator heat flow. Last power " + lastPower + ", fraction " + updateTime/GlobalConfigurations.RADIATOR_HYSTERESIS + ", delta: " + delta + ", target: " + target);
		return lastPower + delta * updateTime/GlobalConfigurations.RADIATOR_HYSTERESIS; // some factor depending on update time!
	}
	
}
