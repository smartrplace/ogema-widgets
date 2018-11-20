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
package de.iwes.widgets.html.selectiontree.samples;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ogema.model.devices.buildingtechnology.ElectricLight;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.devices.sensoractordevices.SingleSwitchBox;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.HumiditySensor;
import org.ogema.model.sensors.MotionSensor;
import org.ogema.model.sensors.OccupancySensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class BaseDeviceTypeOption extends LinkingOption {

	private final static List<DeviceTypeItem> items = Collections.unmodifiableList(Arrays.asList(
			new DeviceTypeItem(Thermostat.class),
			new DeviceTypeItem(TemperatureSensor.class),
			new DeviceTypeItem(HumiditySensor.class),
			new DeviceTypeItem(OccupancySensor.class),
			new DeviceTypeItem(MotionSensor.class),
			new DeviceTypeItem(Sensor.class),
			new DeviceTypeItem(ElectricLight.class),
			new DeviceTypeItem(SingleSwitchBox.class)));
	
	@Override
	public String id() {
		return "device_type";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Select device type";
	}

	@Override
	public LinkingOption[] dependencies() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> dependencies) {
		return (List) items;
	}

	
	public static class DeviceTypeItem implements SelectionItem {
		
		public final Class<? extends PhysicalElement> deviceType;
		
		public DeviceTypeItem(Class<? extends PhysicalElement> deviceType) {
			this.deviceType = deviceType;
		}

		@Override
		public String id() {
			return deviceType.getName();
		}

		// TODO NameService
		@Override
		public String label(OgemaLocale locale) {
			return deviceType.getSimpleName();
		}
		
	}
	
}
