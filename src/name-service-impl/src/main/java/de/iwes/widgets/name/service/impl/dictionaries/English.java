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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.model.Resource;
import org.ogema.model.devices.buildingtechnology.ElectricLight;
import org.ogema.model.devices.generators.PVPlant;
import org.ogema.model.devices.generators.WindPlant;
import org.ogema.model.devices.whitegoods.CoolingDevice;
import org.ogema.model.devices.whitegoods.WashingMachine;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.name.service.impl.TypeDictionary;

public class English extends DictionaryBase {
	
	private Map<String,String> names = new HashMap<String, String>(); // HashMap is fine from synchronization viewpoint since we do not write after initialization 
	private Map<String,Class<? extends Resource>> classes = new HashMap<String, Class<? extends Resource>>(); // HashMap is fine from synchronization viewpoint since we do not write after initialization 
	
	public English() {
		initializeMap();
	}

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.ENGLISH;
	}

	@Override
	public String getName(Class<? extends Resource> type) {
		return names.get(type.getName());
	}
	
	@Override
	public boolean isTypeAvailable(Class<? extends Resource> type) {
		return names.containsKey(type.getName());
	}
	
	@Override
	public List<Class<? extends Resource>> getAvailableTypes() {
		return new ArrayList<Class<? extends Resource>>(classes.values());
	}
	
	private void initializeMap() {
		addClass(Resource.class, "General Resource");
		addClass(PhysicalElement.class, "Generic device");
		addClass(Sensor.class, "Sensor");
		addClass(TemperatureSensor.class, "Temperature Sensor");
		addClass(PVPlant.class, "PV Plant");
		addClass(WindPlant.class, "Wind turbine");
		addClass(WashingMachine.class, "Washing machine");
		addClass(ElectricLight.class, "Lamp");
		addClass(CoolingDevice.class, "Cooling device");
	}

	private void addClass(Class<? extends Resource> clazz, String name) {
		names.put(clazz.getName(), name);
		classes.put(clazz.getName(), clazz);
	}

	@Override
	String temperatureValue() {
		return "Temperature value";
	}

	@Override
	String humidityValue() {
		return "Humidity value";
	}

	@Override
	String sensorValue() {
		return "Sensor value";
	}

	@Override
	String switchBoxControl() {
		return "Switch box control value";
	}

	@Override
	String switchBoxFB() {
		return "Switch box state";
	}

	@Override
	String temperatureProgram() {
		return "Temperature program";
	}

	@Override
	String sensorProgram() {
		return null;
	}

	@Override
	String temperatureForecast() {
		return "Temperature forecast";
	}

	@Override
	String sensorForecast() {
		return "Value forecast";
	}

	@Override
	String temperatureHistory() {
		return "Temperature history";
	}

	@Override
	String sensorHistory() {
		return "Historical values";
	}

	@Override
	String controlProgram() {
		return "Control program";
	}

	@Override
	String valueForecast() {
		return "Value forcast";
	}




    
}
