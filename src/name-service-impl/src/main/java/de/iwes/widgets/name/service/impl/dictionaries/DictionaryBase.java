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
