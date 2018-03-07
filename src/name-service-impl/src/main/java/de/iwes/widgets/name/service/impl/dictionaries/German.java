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

public class German extends DictionaryBase {
	
	
	private Map<String,String> names = new HashMap<String, String>(); // HashMap is fine from synchronization viewpoint since we do not write after initialization 
	private Map<String,Class<? extends Resource>> classes = new HashMap<String, Class<? extends Resource>>(); // HashMap is fine from synchronization viewpoint since we do not write after initialization 
	
	public German() {
		initializeMap();
	}

	@Override
	public OgemaLocale getLocale() {
		return OgemaLocale.GERMAN;
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
		addClass(Resource.class, "Allgemeine Ressource");
		addClass(PhysicalElement.class, "Allgemeines Ger�t");
		addClass(Sensor.class, "Sensor");
		addClass(TemperatureSensor.class, "Temperatursensor");
		addClass(PVPlant.class, "PV-Anlage");
		addClass(WindPlant.class, "Wind-Turbine");
		addClass(WashingMachine.class, "Waschmaschine");
		addClass(ElectricLight.class, "Lampe");
		addClass(CoolingDevice.class, "K�hlschrank/Gefrierger�t");
	}

	private void addClass(Class<? extends Resource> clazz, String name) {
		names.put(clazz.getName(), name);
		classes.put(clazz.getName(), clazz);
	}

	@Override
	String temperatureValue() {
		return "Temperaturwert";
	}

	@Override
	String humidityValue() {
		return "Luftfeuchtigkeitswert";
	}

	@Override
	String sensorValue() {
		return "Sensorwert";
	}

	@Override
	String switchBoxControl() {
		return "Schaltboxvorgabe";
	}

	@Override
	String switchBoxFB() {
		return "Schaltbox Messwert";
	}

	@Override
	String temperatureProgram() {
		return "Temperaturvorgabe";
	}

	@Override
	String sensorProgram() {
		return "Sensorvorgabe";
	}

	@Override
	String temperatureForecast() {
		return "Temperaturvorhersage";
	}

	@Override
	String sensorForecast() {
		return "Vorhersage";
	}

	@Override
	String temperatureHistory() {
		return "Historische Temperaturwerte";
	}

	@Override
	String sensorHistory() {
		return "Historische Daten";
	}

	@Override
	String controlProgram() {
		return "Programm";
	}

	@Override
	String valueForecast() {
		return "Vorhersage";
	}


    
}
