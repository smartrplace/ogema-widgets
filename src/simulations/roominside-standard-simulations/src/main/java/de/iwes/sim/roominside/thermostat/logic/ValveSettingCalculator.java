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

public class ValveSettingCalculator {

	private static ValveSettingCalculator instance = null;
	//private Logger logger = LoggerFactory.getLogger(getClass());
	final private OgemaLogger logger;
	
	private ValveSettingCalculator(OgemaLogger logger) {
		this.logger = logger;
	}
	
	public static ValveSettingCalculator getInstance(OgemaLogger logger) {
		if (instance == null) {
			instance = new ValveSettingCalculator(logger);
		}
		return instance;
	}
	
	public float getNewValue(float currentTemperature, float targetTemperature, 
			List<SampledValue> historicalTemperatureData, List<SampledValue> historicalValveSettings) {	
		if (historicalTemperatureData.size() < 2 || historicalValveSettings.size() < 2) {	// in this case it is not possible to determine a gradient or recent valve settign
			if (currentTemperature >= targetTemperature - 1) {
				return 0;
			}
			else if (currentTemperature >= targetTemperature - 2) {
				return 0.3F;
			}
			else if (currentTemperature >= targetTemperature - 3) {
				return 0.5F;
			}
			else if (currentTemperature >= targetTemperature - 4) {
				return 0.8F;
			}
			else {
				return 1;
			}			
		}
		
		SampledValue svTemp1 = historicalTemperatureData.get(0);
		SampledValue svTemp2 = historicalTemperatureData.get(historicalTemperatureData.size()-1);
		float totalGradient = GenericCalculations.getInstance().getGradient(svTemp1,svTemp2);
		float averageValveSetting = GenericCalculations.getInstance().getAverage(historicalValveSettings);
		if (averageValveSetting > 1 || averageValveSetting< 0) { 
			logger.warn("ValveSettingCalculator: inconsistent valve average calculated: " + averageValveSetting + ". Should lie in [0,1].");
			averageValveSetting = (averageValveSetting < 0) ? 0: 1;
		}
		float target = currentTemperature + totalGradient * GlobalConfigurations.VALVE_TEMP_ADJUSTMENT_TIME;
		float diff = targetTemperature - target;
		// TODO improve control strategy
		logger.debug("New valve setting. currentTemperature: " + currentTemperature + ", targetTemperature " + target + ", last gradient: " + totalGradient);
		if (averageValveSetting < 0.1 && diff < 0.5)    // we haven't heated a lot recently, still the temperature is ok 
			return 0;    						
		else if ( diff > 5)								// it is very cold -> go for full power
			return 1;					
		else if (diff < -5) 							// it is hot -> switch off the heater
			return 0;
		
		float result;
		if (diff > 0.5) 
			result = diff / 3 * averageValveSetting;
		else if (diff < -0.5)
			result = - averageValveSetting / diff;
		else 
			result = averageValveSetting; 
		if (result > 1) result = 1;
		return result;	
	}
	
}
