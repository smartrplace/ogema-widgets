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
package org.ogema.apps.roomsim.service.api.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ogema.apps.roomsim.service.api.RoomInsideSimulation;
import org.ogema.apps.roomsim.service.api.RoomSimConfig;
import org.ogema.apps.roomsim.service.api.RoomSimConfigPattern;
import org.ogema.apps.roomsim.service.api.SingleRoomSimulation;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.BrightnessResource;
import org.ogema.core.model.units.ConcentrationResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.locations.Room;
import org.ogema.model.user.NaturalPerson;
import org.ogema.tools.simulation.service.api.model.SimulationConfiguration;

import de.iwes.sim.roomsimservice.device.RoomsimulationServicePattern;
import de.iwes.sim.roomsimservice.logic.HumdityCalculator;
import de.iwes.sim.roomsimservice.logic.TemperatureCalculator;

/**An instance per room has to be held. Note: The simulation is only offered for rooms that are configured
 * by a RoomSimConfig*/
public class SingleRoomSimulationImpl implements SingleRoomSimulation {
	public SingleRoomSimulationImpl(RoomsimulationServicePattern roomPattern,
			RoomSimConfigPattern configPattern, OgemaLogger logger) {
		this.roomPattern = roomPattern;
		this.configPattern = configPattern;
		this.logger = logger;
		
		calc = new HumdityCalculator(configPattern.simulatedHumidity.getValue(), configPattern.simulatedTemperature.getKelvin());
	}

	private RoomsimulationServicePattern roomPattern;
	private RoomSimConfigPattern configPattern;
	private OgemaLogger logger;
	
	private List<RoomInsideSimulationData> insideComponents = new ArrayList<>();
	private HumdityCalculator calc;
	
	public void step(long currentTime, long stepSize) {
		//Logger logger = LoggerFactory.getLogger(getClass());
		//logger.warn("Tried to start unsupported room simulation");
		//timer.destroy();
		
		for(RoomInsideSimulationData insideSim: insideComponents) {
			if(insideSim.requestBusy()) {
				try {
					insideSim.pattern.step(stepSize + insideSim.stepTimeMissed);
				} finally {
					insideSim.releaseBusy();
					insideSim.stepTimeMissed = 0;
				}
			} else {
				insideSim.stepTimeMissed += stepSize;
			}
		}
		
		//update temperature and humidity
		float newValue = getNewTemperatureValue(currentTime, currentTime-stepSize);
		
		configPattern.simulatedTemperature.setCelsius(newValue);
		logger.debug("Set simulated room temperature:"+newValue);
		newValue = calc.getNewValue(stepSize, 0, configPattern.simulatedTemperature.getKelvin());
		configPattern.simulatedHumidity.setValue(newValue);
	}
	public void close() {
		for(RoomInsideSimulationData insideSim: insideComponents) {
			if(insideSim.requestBusy()) {
				try {
					insideSim.pattern.close();
				} finally {
					insideSim.releaseBusy();
				}
			}
		}
	}

	private float getNewTemperatureValue(long currentTime, long lastUpdateTime) {
		AbsoluteSchedule logDataTemp = configPattern.simulatedTemperature.historicalData();
		logDataTemp.create().activate(false);
//		List<SampledValue> tempValues = logDataTemp.getValues(currentTime - GlobalConfigurations.RADIATOR_HYSTERESIS);

		float roomSize = 40; // m^3 // TODO parameter // 3*5*2.6m^3 
		float wallSize = 8; // m^2 // TODO parameter
		float outsideTemperature = 10; // TODO parameter
		
		float newValue = TemperatureCalculator.getInstance().getNewValue(roomSize, wallSize, configPattern.simulatedTemperature.getCelsius(), outsideTemperature ,
				currentTime, lastUpdateTime, null);
		
		logDataTemp.addValue(currentTime, new FloatValue(configPattern.simulatedTemperature.getValue()));
		return newValue;
	}
	
	/*public <P extends KnowRoomPattern> P getRoomElement(List<P> list) {
		for(P p:list) {
			if((p.getRoom() != null) && p.getRoom().equalsLocation(roomPattern.model)) {
				return p;
			}
		}
		return null;
	}*/

	@Override
	public Room getRoom() {
		return roomPattern.model;
	}

	public boolean registerInsideRoomComponent(RoomInsideSimulation<?> component) {
		insideComponents.add(new RoomInsideSimulationData(component));
		return true;			
	}
	@Override
	public boolean unregisterInsideRoomComponent(RoomInsideSimulation<?> component) {
		component.close();
		return insideComponents.remove(component);
	}

	@Override
	public TemperatureResource getTemperature() {
		return configPattern.simulatedTemperature;
		//HumSensePattern hsp = getRoomElement(rcsapp.humSim.getSimulationPatterns());
		//if(hsp == null || hsp.ownFindings.temperature == null) return null;
		//else return hsp.ownFindings.temperature;
	}

	@Override
	public FloatResource getRelativeHumidity() {
		return configPattern.simulatedHumidity;
		//HumSensePattern hsp = getRoomElement(rcsapp.humSim.getSimulationPatterns());
		//if(hsp == null) return null;
		//else return hsp.reading;
	}

	@Override
	public float getAbsoluteHumidity() {
		return calc.getAbsoluteVapor()* roomPattern.volume.getValue();
		//HumSensePattern hsp = getRoomElement(rcsapp.humSim.getSimulationPatterns());
		//if(hsp == null) return 40.0f*volume.getValue();
		//else {
		//float relativeHumdity = humidity.getValue();
			//float relativeHumdity = hsp.reading.getValue();
		//float temperature = this.temperature.getKelvin();
			//float temperature = hsp.ownFindings.temperature.getKelvin();
		//float absoluteVaporPR = HumidityHelper.getAbsoluteHumidity(temperature, relativeHumdity);
		//return absoluteVaporPR * volume.getValue();
		//}
	}

	@Override
	public void addThermalEnergy(float joule) {
		if(Float.isNaN(joule)) return;
		if(Float.isInfinite(joule)) return;
		TemperatureCalculator.getInstance().addEnergy(joule);
	}

	@Override
	public void addHumidity(float gH2O) {
		//HumSensePattern hsp = getRoomElement(rcsapp.humSim.getSimulationPatterns());
		//if(hsp != null) hsp.addVapor(gH2O / volume.getValue());
		calc.addVapor(gH2O / roomPattern.volume.getValue());
	}

	@Override
	public ConcentrationResource getCO2() {
		throw new UnsupportedOperationException("CO2 concentration not yet supported");
	}

	@Override
	public void addCO2(float mgCO2) {
		throw new UnsupportedOperationException("CO2 concentration not yet supported");
	}

	@Override
	public BrightnessResource getLight() {
		throw new UnsupportedOperationException("Light not yet supported");
	}

	@Override
	public void addLight(float lumen) {
		throw new UnsupportedOperationException("Light not yet supported");
	}

	@Override
	public boolean isPersonMoving() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addPersonMoving() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getVolume() {
		return roomPattern.volume.getValue();
	}
	@Override
	public RoomSimConfig getConfigResource() {
		return configPattern.model;
	}
	
	int unknownPersonNum = 0;
	Set<NaturalPerson> knownUsers = new HashSet<>();
	
	@Override
	public void addUnknownPerson(int personNum) {
		unknownPersonNum += personNum;
		configPattern.personInRoomNonPersistent.setValue(getTotalRoomOccupancy());
	}

	@Override
	public boolean removeUnknownPerson(int personNum) {
		unknownPersonNum -= personNum;
		if(unknownPersonNum < 0) {
			unknownPersonNum = 0;
			configPattern.personInRoomNonPersistent.setValue(getTotalRoomOccupancy());
			return false;
		}
		configPattern.personInRoomNonPersistent.setValue(getTotalRoomOccupancy());
		return true;
	}

	@Override
	public boolean addPerson(NaturalPerson user) {
		boolean result = knownUsers.add(user);
		configPattern.personInRoomNonPersistent.setValue(getTotalRoomOccupancy());
		return result;
	}

	@Override
	public boolean removePerson(NaturalPerson user) {
		boolean result = knownUsers.remove(user);
		configPattern.personInRoomNonPersistent.setValue(getTotalRoomOccupancy());
		return result;
	}

	@Override
	public int getTotalRoomOccupancy() {
		return unknownPersonNum + knownUsers.size();
	}

	@Override
	public Set<NaturalPerson> getKnownUsersInRoom() {
		return knownUsers;
	}
	@Override
	public List<RoomInsideSimulation<?>> getConnectedSimulations() {
		List<RoomInsideSimulation<?>> res = new ArrayList<>();
		for(RoomInsideSimulationData ridata: insideComponents) {
			res.add(ridata.pattern);
		}
		return res;
	}
	@Override
	public SimulationConfiguration getConfigurationById(String simulationConfiguration) {
		for(RoomInsideSimulation<?> ris: getConnectedSimulations()) {
			String devLoc = ris.getSimulationPattern().model.getLocation();
			for(SimulationConfiguration sc: ris.getProvider().getConfigurations(devLoc)) {
				if(sc.getId().equals(simulationConfiguration)) {
					return sc; 
				}
			}
		}
		return null;
	}
}
