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
