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
package de.iwes.util.resourcelist;

import java.util.List;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;

import de.iwes.util.resource.THSensorAccess;

/** Class providing static functions to extract temperature and humidity sensor resources
 * from a resource list and deal with them
 */
public class SensorResourceListHelper {
	/** Extract up to one temperature and humidity sensor from a resource list. Return
	 * true only if both sensors could be detected*/
	public static boolean getTHSensorsFromList(ResourceList<Sensor> sensors,
			THSensorAccess thSensorAccess) {
		return getTempSensorFromList(sensors, thSensorAccess) & 
				getHumiditySensorFromList(sensors, thSensorAccess);
	}
	/** Extract up to one temperature and humidity sensor from a resource list. Return
	 * true if at least one sensor could be detected*/
	public static boolean getTHSensorsFromListAtLeastOne(ResourceList<Sensor> sensors,
			THSensorAccess thSensorAccess) {
		return getTempSensorFromList(sensors, thSensorAccess) | 
				getHumiditySensorFromList(sensors, thSensorAccess);
	}
	
	public static boolean getTempSensorFromList(ResourceList<Sensor> sensors, THSensorAccess thSensorAccess) {
		final List<TemperatureSensor> list = sensors.getSubResources(TemperatureSensor.class, false);
		if (list.isEmpty()) 
			return false;
		TemperatureSensor sensor = null;
		for (TemperatureSensor ts : list) {
			if (ts.reading().exists()) {
				sensor = ts;
				break;
			}
		}
		if (sensor == null)
			return false;
		thSensorAccess.temperatureReading = sensor.reading();
		return true;
	}
	
	public static boolean getHumiditySensorFromList(ResourceList<Sensor> sensors, THSensorAccess thSensorAccess) {
		final List<HumiditySensor> list = sensors.getSubResources(HumiditySensor.class, false);
		if (list.isEmpty()) 
			return false;
		HumiditySensor sensor = null;
		for (HumiditySensor ts : list) {
			if (ts.reading().exists()) {
				sensor = ts;
				break;
			}
		}
		if (sensor == null)
			return false;
		thSensorAccess.humiditySens = sensor.reading();
		return true;
	}
	
	/** Provide structured/fail-safe string representing the value of a temperature resource*/
	public static String printTempVal(TemperatureResource res) {
		if(res != null && res.exists()) {
			return printTempVal(res.getValue());
		} else {
			return "n/a";
		}	
	}
	public static String printTempVal(float tempVal) {
		float val = tempVal - 273.15f;
		if(val > -200) {
			return String.format("%.1f °C", val);				
			//return String.format("%.1f &deg;C", val);				
		} else {
			return "waiting"; // XXX waiting?
		}
	}
	public static String printTempValStd(float tempVal) {
		float val = tempVal - 273.15f;
		if(val > -200) {
			return String.format("%.1f", val);
		} else {
			return "waiting"; // XXX waiting?
		}
	}
	public static String printRelativeTempVal(float tempVal) {
		float val = tempVal;
		return String.format("%.1f K", val);				
	}
	
	/** Provide structured/fail-safe string representing the value of a humidity resource*/
	public static String printHumidityVal(FloatResource humidityRes) {
		if(humidityRes != null && humidityRes.exists()) {
			return String.format("%.0f %%", humidityRes.getValue()*100);
		} else {
			return "n/a";
		}
	}
}
