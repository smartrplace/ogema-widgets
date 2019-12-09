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

package de.iee.monitoring.batterystatemonitoring.gui;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.VoltageResource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.drivers.homematic.xmlrpc.hl.types.HmDevice;
import org.ogema.drivers.homematic.xmlrpc.hl.types.HmMaintenance;
import org.ogema.model.devices.storage.ElectricityStorage;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iee.monitoring.batterystatemonitoring.BatteryStateMonitoringController;
import de.iee.monitoring.batterystatemonitoring.config.BatteryStateMonitoringProgramConfig;
import de.iee.monitoring.batterystatemonitoring.pattern.ElectricityStorageSocPattern;
import de.iee.monitoring.batterystatemonitoring.pattern.ElectricityStorageVoltagePattern;
import de.iee.monitoring.batterystatemonitoring.pattern.HmMaintenancePattern;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;

public class MainPageCopy {

	private final WidgetPage<?> page;
	private final Header header;
	private final DynamicTable<Resource> devices;
	
	@SuppressWarnings("serial")
	public MainPageCopy(final WidgetPage<?> page, final ApplicationManager appMan, final ResourceList<BatteryStateMonitoringProgramConfig> configs) {
		this.page = page;
		this.header = new Header(page, "header", "Battery State Monitoring");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_LEFT);
		this.devices = new DynamicTable<Resource>(page, "devices") {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				updateRows(Stream.concat(Stream.concat(
				    	appMan.getResourcePatternAccess().getPatterns(ElectricityStorageVoltagePattern.class, AccessPriority.PRIO_LOWEST).stream(),
				    	appMan.getResourcePatternAccess().getPatterns(ElectricityStorageSocPattern.class, AccessPriority.PRIO_LOWEST).stream()
						),
						appMan.getResourcePatternAccess().getPatterns(HmMaintenancePattern.class, AccessPriority.PRIO_LOWEST).stream()
							.filter(channel -> BatteryStateMonitoringController.channelHasNoAssociatedBattery(channel.model, appMan)) // avoid duplicates
					)
			    	.map(p -> p.model)
			    	.distinct()
			    	.collect(Collectors.toList()), req);
			}
			
		};
		devices.setRowTemplate(new RowTemplate<Resource>() {
			
			final Map<String,Object> header;
			
			{
				final Map<String,Object> headerLocal = new LinkedHashMap<>();
				headerLocal.put("loc", "Location");
				headerLocal.put("dev", "Device name/path");
				headerLocal.put("stat", "Charge state");
				headerLocal.put("send", "Send message if battery low");
				header = Collections.unmodifiableMap(headerLocal);
			}
			
			@Override
			public String getLineId(Resource object) {
				return ResourceUtils.getValidResourceName(object.getPath());
			}
			
			@Override
			public Map<String, Object> getHeader() {
				return header;
			}
			
			@Override
			public Row addRow(Resource object, OgemaHttpRequest req) {
				final Row row = new Row();
				final String lineId = getLineId(object);
				row.addCell("loc", getDeviceLocationRoom(object));
				row.addCell("dev", new DeviceNameLabel(devices, lineId + "_devices", req, object));
				row.addCell("stat", getState(object, appMan));
				row.addCell("send", new DoSendCheckbox(devices, lineId + "_send",  req, object, configs));
				return row;
			}
		});
		
		
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(devices);
	}
	
	private final void setDependencies() {
	}

	@SuppressWarnings("serial")
	private static class DoSendCheckbox extends Checkbox2 {
		
		private final Resource device;
		private final ResourceList<BatteryStateMonitoringProgramConfig> configs;

		public DoSendCheckbox(OgemaWidget parent, String id, OgemaHttpRequest req, Resource device, ResourceList<BatteryStateMonitoringProgramConfig> configs) {
			super(parent, id, req);
			this.device = Objects.requireNonNull(device);
			this.configs = Objects.requireNonNull(configs);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final boolean doSend = configs.getAllElements().stream()
					.filter(cfg -> device.equalsLocation(cfg.sensorDetected()))
					.map(BatteryStateMonitoringProgramConfig::sendMessage)
					.map(BooleanResource::getValue)
					.findAny()
					.orElse(false);
			setCheckboxList(Collections.singletonList(new DefaultCheckboxEntry("", "", doSend)), req);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final boolean doSend = isChecked("", req);
			final BooleanResource br = configs.getAllElements().stream()
					.filter(cfg -> device.equalsLocation(cfg.sensorDetected()))
					.map(BatteryStateMonitoringProgramConfig::sendMessage)
					.findAny()
					.orElse(null);
			if (br != null) {
				br.setValue(doSend);
			} else if (doSend) {
				final BatteryStateMonitoringProgramConfig cfg = configs.add();
				cfg.sensorDetected().setAsReference(device);
				cfg.sendMessage().<BooleanResource> create().setValue(true);
				cfg.activate(true);
			}
		}
		
	}
	
	@SuppressWarnings("serial")
	private static class DeviceNameLabel extends Label {

		private final Resource device;
		
		public DeviceNameLabel(OgemaWidget parent, String id, OgemaHttpRequest req, Resource device) {
			super(parent, id, req);
			this.device = Objects.requireNonNull(device);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final String dev = getDeviceNameIfPresent(device, getNameService(), req.getLocale());
			if (dev != null) {
				setText(dev, req);
				setToolTip("path: " + device.getPath(), req);
			} else {
				setText(device.getPath(), req);
			}
		}
		
	}
	
	private static String getState(final Resource r, final ApplicationManager appMan) {
		final boolean  isLow = BatteryStateMonitoringController.isBatteryLow(r, appMan);
		if (r instanceof ElectricityStorage) {
			final VoltageResource vr = ((ElectricityStorage) r).internalVoltage().reading();
			if (vr.isActive()) {
				final float v = vr.getValue();
				return v + "V / " + (isLow ? "LOW" : "OK");
			} else {
				final FloatResource soc = ((ElectricityStorage) r).chargeSensor().reading();
				if (soc.isActive()) {
					final float perc = soc.getValue() * 100;
					return perc + "% / " + (isLow ? "LOW" : "OK");
				}
			}
		} 
		return isLow ? "LOW" : "OK";
	}
	
	private static final String getDeviceLocationRoom(Resource e) {
		try {
			if (e instanceof HmMaintenance) {
				final Resource other = e.getParent().getSubResources(PhysicalElement.class, false).stream()
					.filter(device -> !(device instanceof HmDevice))
					.findAny()
					.orElse(null);
				if (other != null)
					e = other;
			}
			return ResourceUtils.getHumanReadableName(ResourceUtils.getDeviceLocationRoom(e));
		} catch (SecurityException | NullPointerException ee) {
			return "";
		}
	}
	
	private static final String getDeviceNameIfPresent(final Resource e, final NameService nameService, final OgemaLocale locale) {
		try {
			return nameService.getName(e, locale, true, true);
		} catch (SecurityException | NullPointerException  ee) {
			return null;
		}
	}
	
	private static final String getDeviceName(final Resource e) {
		try {
			return ResourceUtils.getHumanReadableName(e);
		} catch (SecurityException | NullPointerException  ee) {
			return "";
		}
	}	
}
