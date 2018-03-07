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
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.iwes.widgets.name.service.impl.dictionaries;


import org.ogema.core.model.Resource;
import org.ogema.model.devices.sensoractordevices.SingleSwitchBox;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;

import de.iwes.widgets.name.service.impl.TypeDictionary;

public abstract class DictionaryBase implements TypeDictionary {

	public DictionaryBase() {
	}
	
	@Override
	public String getName(String relativePath, Class<? extends Resource> parentType) {
		// heuristics
		int level = relativePath.length() - relativePath.replaceAll("/", "").length();
		if (relativePath.equals("reading"))  {
			if (TemperatureSensor.class.isAssignableFrom(parentType)) return temperatureValue();
			else if (HumiditySensor.class.isAssignableFrom(parentType)) return humidityValue();
			else if (Sensor.class.isAssignableFrom(parentType)) return sensorValue();
		}
		if (relativePath.equals("onOffSwitch/stateControl")) {
			if (SingleSwitchBox.class.isAssignableFrom(parentType)) return switchBoxControl();
		}
		if (relativePath.equals("onOffSwitch/stateFeedback")) {
			if (SingleSwitchBox.class.isAssignableFrom(parentType)) return switchBoxFB();
		}
		if (relativePath.equals("reading/program")) {
			if (TemperatureSensor.class.isAssignableFrom(parentType)) return temperatureProgram();
			else if (Sensor.class.isAssignableFrom(parentType)) return sensorProgram();
		}
		if (relativePath.equals("reading/forecast")) {
			if (TemperatureSensor.class.isAssignableFrom(parentType)) return temperatureForecast();
			else if (Sensor.class.isAssignableFrom(parentType)) return sensorForecast();
		}
		if (relativePath.equals("reading/historicalData")) {
			if (TemperatureSensor.class.isAssignableFrom(parentType)) return temperatureHistory();
			else if (Sensor.class.isAssignableFrom(parentType)) return sensorHistory();
		}
		if (relativePath.endsWith("stateControl/program")) {
			return controlProgram();
		}
		if (relativePath.endsWith("stateFeedback/forecast")) {
			return valueForecast();
		}
		
		return null;
	}

	abstract String temperatureValue();
	abstract String humidityValue();
	abstract String sensorValue();
	abstract String switchBoxControl();
	abstract String switchBoxFB();
	abstract String temperatureProgram();
	abstract String sensorProgram();
	abstract String temperatureForecast();
	abstract String sensorForecast();
	abstract String temperatureHistory();
	abstract String sensorHistory();
	abstract String controlProgram();
	abstract String valueForecast();

}
