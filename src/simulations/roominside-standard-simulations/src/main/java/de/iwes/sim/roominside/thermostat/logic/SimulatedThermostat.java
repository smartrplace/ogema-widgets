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

import java.util.ArrayList;
import java.util.List;

import org.ogema.apps.roomsim.service.api.RoomSimulationService;
import org.ogema.apps.roomsim.service.api.helpers.RoomInsideLogicBase;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.tools.resourcemanipulator.timer.CountDownDelayedExecutionTimer;
import org.ogema.tools.simulation.service.api.SimulationProvider;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

import de.iwes.sim.roominside.std.GlobalConfigurations;
import de.iwes.sim.roominside.thermostat.ThermostatConfigPattern;
import de.iwes.sim.roominside.thermostat.ThermostatPattern;
import de.iwes.util.resource.ResourceHelper;

/**
 * Simulation code for PV plant described by a {@link PVPattern}.
 *
 * @author David Nestle, Fraunhofer IWES
 */
public class SimulatedThermostat extends RoomInsideLogicBase<Thermostat> {

    private final ApplicationManager appMan;
    private final OgemaLogger logger;
    private final ThermostatPattern targetPattern;
    private final ThermostatConfigPattern configPattern;
    
    private ResourceValueListener<TemperatureResource> targetTempListener;
    public SimulatedThermostat(ApplicationManager appMan, ThermostatPattern pv,
    		ThermostatConfigPattern configPattern,
    		RoomSimulationService roomSimService, SimulationProvider<Thermostat> provider) {
    	super(roomSimService, provider, configPattern.model.target(), configPattern.roomSimulationToConnect,
    			configPattern);
        this.appMan = appMan;
        this.logger = appMan.getLogger();
        this.targetPattern = pv;
        this.configPattern = configPattern;
        
        
        targetTempListener = new ResourceValueListener<TemperatureResource>() {
			@Override
			public void resourceChanged(TemperatureResource resource) {
				if(targetPattern.setpointFB.isActive()) {
					new CountDownDelayedExecutionTimer(SimulatedThermostat.this.appMan, 1500) {
						@Override
						public void delayedExecution() {
							targetPattern.setpointFB.setCelsius(Math.round(targetPattern.targetTemperature.getCelsius()*2)*0.5f);
						}
					};
				}
			}
		};
        ResourceHelper.localizeResource(targetPattern.targetTemperature, appMan.getResourceAccess()).addValueListener(targetTempListener, true);
        //targetPattern.targetTemperature.addValueListener(targetTempListener, true);  
    }
    
	public void updateState(long stepSize) {
		// TODO add dependence on 'Vorlauftemperatur'/outside temp/radiator size
		
		long currentTime = appMan.getFrameworkTime();
		
		//calculate valve position
		fromValvePatternTimerElapsed(currentTime, currentTime-stepSize);
		
		//Calculate power flow into radiator
		fromRadiatorPatternTimerElapsed(currentTime, currentTime-stepSize);
		
		//Calculate energy added of radiator
		AbsoluteSchedule logDataPower = targetPattern.thPower.historicalData();
		logDataPower.create().activate(false);
		logDataPower.addValue(currentTime, new FloatValue(targetPattern.thPower.getValue()));
		List<SampledValue> powerValues = logDataPower.getValues(currentTime - GlobalConfigurations.RADIATOR_HYSTERESIS);
		
		float newValue = energyAddedByRadiator(currentTime, currentTime-stepSize, powerValues);
		roomSim.addThermalEnergy(newValue);
		
		//set measured temperature
		if(!targetPattern.measuredTemperature.isActive() &&  configPattern.initState.getValue() > 0) {
			targetPattern.measuredTemperature.create();
			targetPattern.measuredTemperature.activate(false);
		}
		if(targetPattern.measuredTemperature.isActive()) {
			targetPattern.measuredTemperature.setKelvin(roomSim.getTemperature().getKelvin());
		}
	}
	
	private void fromValvePatternTimerElapsed(long currentTime, long lastUpdateTime) {
		List<SampledValue> tempValues;
		float currentTemp;
		if(targetPattern.measuredTemperature.isActive()) {
			AbsoluteSchedule logDataTemp =  targetPattern.measuredTemperature.historicalData();
			logDataTemp.create().activate(false);
			logDataTemp.addValue(currentTime, new FloatValue(targetPattern.measuredTemperature.getValue()));
			logDataTemp.replaceValues(Long.MIN_VALUE, currentTime - GlobalConfigurations.HISTORICAL_DATA_PRESERVATION_TIME, null);
			currentTemp = targetPattern.measuredTemperature.getCelsius();
			tempValues = logDataTemp.getValues(currentTime - GlobalConfigurations.HISTORICAL_DATA_PRESERVATION_TIME);
		} else {
			tempValues = new ArrayList<SampledValue>();
			currentTemp = 20.0f+273.15f;
		}
		AbsoluteSchedule logDataValve = targetPattern.valveStatus.historicalData();
		logDataValve.create().activate(false);
		// delete old values
		logDataValve.replaceValues(Long.MIN_VALUE, currentTime - GlobalConfigurations.HISTORICAL_DATA_PRESERVATION_TIME, null);
		
		//long updateTime = updateInterval.getValue();
		float targetTemp;
		if(targetPattern.setpointFB.isActive()) {
			targetTemp = targetPattern.setpointFB.getCelsius();			
		} else {
			targetTemp = targetPattern.targetTemperature.getCelsius();
		}
		List<SampledValue> valveValues = logDataValve.getValues(currentTime - GlobalConfigurations.HISTORICAL_DATA_PRESERVATION_TIME);
		
		float newValue = ValveSettingCalculator.getInstance(logger).getNewValue(currentTemp, targetTemp, tempValues, valveValues);
		logger.debug("New valve setting " + newValue);
		targetPattern.valveStateControl.setValue(newValue);
		targetPattern.valveStatus.setValue(newValue);
		//try {
		//	Thread.sleep(500);	// wait until stateFeedback has been written
		//} catch (InterruptedException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//} 
		logDataValve.addValue(currentTime, new FloatValue(targetPattern.valveStatus.getValue()));
	}
	
	private void fromRadiatorPatternTimerElapsed(long currentTime, long lastUpdateTime) {
		AbsoluteSchedule logDataValve = targetPattern.valveStatus.historicalData();
		logDataValve.create().activate(false);
		logDataValve.addValue(currentTime, new FloatValue(targetPattern.valveStatus.getValue()));
		AbsoluteSchedule logDataPower = targetPattern.thPower.historicalData();
		logDataPower.create().activate(false);
		// delete old values
		logDataValve.replaceValues(Long.MIN_VALUE, currentTime - GlobalConfigurations.HISTORICAL_DATA_PRESERVATION_TIME, null);
		List<SampledValue> valveValues = logDataValve.getValues(currentTime - GlobalConfigurations.RADIATOR_HYSTERESIS);
		logDataPower.replaceValues(Long.MIN_VALUE, currentTime - GlobalConfigurations.HISTORICAL_DATA_PRESERVATION_TIME, null);
		List<SampledValue> powerValues = logDataPower.getValues(currentTime - GlobalConfigurations.RADIATOR_HYSTERESIS);

		float roomSize = 15; // in m^2
		float newValue = RadiatorCalculator.getInstance(logger).getNewValue(lastUpdateTime, currentTime, targetPattern.maximumPower, roomSize,
				targetPattern.valveStatus.getValue(), valveValues, powerValues);
		logger.debug("Writing new radiator heat flow value: " + newValue);
		targetPattern.thPower.setValue(newValue);
		logDataPower.addValue(currentTime, new FloatValue(targetPattern.thPower.getValue()));
	}
	
	private float energyAddedByRadiator(long currentTime, long lastUpdateTime, List<SampledValue> historicalPowerFlows) {
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
		return energyAdded;
	}

	@Override
	public void step(long stepSize) {
		updateState(stepSize);	
	}

	@Override
	public void close() {
		if(targetTempListener != null) {
			targetPattern.targetTemperature.removeValueListener(targetTempListener);
		}
	}
}
