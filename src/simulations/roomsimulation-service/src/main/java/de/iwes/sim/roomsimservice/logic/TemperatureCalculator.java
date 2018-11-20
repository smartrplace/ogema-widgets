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
package de.iwes.sim.roomsimservice.logic;

import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemperatureCalculator {

	private static TemperatureCalculator instance = null;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/** Energy added by various sources in the last step*/
	private float energyToAdd = 0;
	
	private TemperatureCalculator() {
	}
	
	public static TemperatureCalculator getInstance() {
		if (instance == null) {
			instance = new TemperatureCalculator();
		}
		return instance;
	}
	
	/**
	 * calculate new temperature value, depending on recent radiator heat flow, outside temperature, room size, ...
	 * @param roomSize in m^3
	 */
	public float getNewValue(float roomSize, float wallSize, float currentTemperature, float outsideTemperature, long currentTime, long lastUpdateTime,
			List<SampledValue> historicalPowerFlows) {
		if (lastUpdateTime == 0) { // initializing
			return currentTemperature;
		}
		float energyAdded;
		if (historicalPowerFlows != null && historicalPowerFlows.size() > 1) {
			if (historicalPowerFlows.get(0).getTimestamp() > lastUpdateTime) {
				energyAdded = historicalPowerFlows.get(historicalPowerFlows.size()-1).getValue().getFloatValue() * (currentTime - lastUpdateTime)/1000; 
			}
			else {
				FloatTimeSeries fl = new FloatTreeTimeSeries();
				fl.addValues(historicalPowerFlows);
				fl.setInterpolationMode(InterpolationMode.LINEAR);
				energyAdded = fl.integrate(lastUpdateTime, currentTime) / 1000; // time in ms -> energy in J
			}
		}
		else if (historicalPowerFlows != null && historicalPowerFlows.size() == 1) {
			energyAdded = historicalPowerFlows.get(0).getValue().getFloatValue() * (currentTime - lastUpdateTime)/1000; 
		}
		else {
			energyAdded = 0;
		}
		energyAdded += energyToAdd;
		energyToAdd = 0;
		 // TODO make the below parameters configurable, as well as room size and wall size
		float lossCoefficient = 0.4F; // typical variation between (0.2 - 1) W/m^2/K  
		float windowLossCoefficient = 2F; // typical variation between (1 - 5) W/m^2/K (from triple-glaze to single glaze)
		float windowShareOfWallArea = 0.3F; // TODO make this configurable
		float combinedLossCoefficient = lossCoefficient * (1-windowShareOfWallArea) + windowLossCoefficient * windowShareOfWallArea;
		float lossParameter = combinedLossCoefficient * wallSize;

		float energyLoss = (currentTemperature - outsideTemperature) * (currentTime - lastUpdateTime)/1000 * lossParameter;
		float energySum = energyAdded - energyLoss;
		float theta  = 1000; // theta in J/K/m^3  // approximately valid at 20°C
		float kelvinAdded =  energySum / theta / roomSize ;
		float newValue = currentTemperature + kelvinAdded;
		
		if (kelvinAdded < 0 && newValue < outsideTemperature) {	// should not happen, but could be the case if updateInterval is chosen to big
			newValue = outsideTemperature;   
		}
		//logger.debug("  Calculated new temperature value. time diff "  + (currentTime - lastUpdateTime)/1000 + "s, current temp : " + currentTemperature + ", outsideTemp: " + outsideTemperature + 
		//		", radiator energy input: " + energyAdded + ", energy loss: " + energyLoss + ", Kelvins added: " + kelvinAdded );
		return newValue; 
	}
	
	public void addEnergy(float joule) {
		energyToAdd += joule;
	}
	
}
