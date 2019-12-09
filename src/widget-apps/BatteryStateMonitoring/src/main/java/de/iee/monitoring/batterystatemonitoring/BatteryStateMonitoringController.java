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

package de.iee.monitoring.batterystatemonitoring;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.VoltageResource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.drivers.homematic.xmlrpc.hl.types.HmMaintenance;
import org.ogema.model.devices.storage.ElectricityStorage;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iee.monitoring.batterystatemonitoring.config.BatteryStateMonitoringConfig;
import de.iee.monitoring.batterystatemonitoring.config.BatteryStateMonitoringProgramConfig;
import de.iee.monitoring.batterystatemonitoring.pattern.ElectricityStorageSocPattern;
import de.iee.monitoring.batterystatemonitoring.pattern.ElectricityStorageVoltagePattern;
import de.iee.monitoring.batterystatemonitoring.pattern.HmMaintenancePattern;
import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class BatteryStateMonitoringController implements TimerListener {

	private static final long DEFAULT_TIMER_INTERVAL = 86400000;
	private static final long INITIAL_TIMER_INTERVAL = 600000;
    private final ApplicationManager appMan;
    private final Timer timer;
    
	private final BatteryStateMonitoringConfig appConfigData;
	private final MessagingService messaging;
	private final NameService nameService;
	
	
    public BatteryStateMonitoringController(ApplicationManager appMan, MessagingService messaging, NameService nameService) {
		this.appMan = appMan;
        this.appConfigData = initConfigurationResource();
        this.messaging = messaging;
        this.nameService = nameService;
        final long now = appMan.getFrameworkTime();
        long nextExec = appConfigData.lastSent().isActive() ? (DEFAULT_TIMER_INTERVAL - (now - appConfigData.lastSent().getValue()))
        		: INITIAL_TIMER_INTERVAL;
        if (nextExec < INITIAL_TIMER_INTERVAL)
        	nextExec = INITIAL_TIMER_INTERVAL;
        timer = appMan.createTimer(INITIAL_TIMER_INTERVAL, this);
	}
    
    @Override
    public void timerElapsed(Timer timer) {
    	final List<Resource> batteryLowDevices = appMan.getResourceAccess().getResources(BatteryStateMonitoringProgramConfig.class).stream()
        		.filter(Resource::isActive)
        		.filter(cfg -> cfg.sendMessage().getValue())
        		.map(cfg -> cfg.sensorDetected())
        		.map(BatteryStateMonitoringController::localize)
        		.filter(Objects::nonNull)
        		.filter(r -> BatteryStateMonitoringController.isBatteryLow(r,appMan))
        		.collect(Collectors.toList());
		final List<String> paths = batteryLowDevices.stream()
				.map(Resource::getPath)
				.collect(Collectors.toList());
		final Stream<Resource> devices;
		if (appConfigData.lastPaths().isActive()) {
			final String[] last = appConfigData.lastPaths().getValues();
			devices = batteryLowDevices.stream()
					.filter(r -> !Arrays.asList(last).contains(r.getPath()));
		} else
			devices = batteryLowDevices.stream();
		final String[] newValues = paths.toArray(new String[paths.size()]);
		appConfigData.lastPaths().<StringArrayResource> create().setValues(newValues);
		appConfigData.lastPaths().activate(false);
		final String msg = devices
				.map(this::getMessage)
				.filter(Objects::nonNull)
		    	.collect(Collectors.joining(", \r\n")); // FIXME separator?
		if (!msg.isEmpty()) {
			sendMessage("Battery State", "Low battery detected: \r\n" + msg);
			appConfigData.lastSent().<TimeResource> create().setValue(timer.getExecutionTime());
			appConfigData.lastSent().activate(false);
			//after first detected low battery state, messages are sent every 24 hours
			timer.setTimingInterval(DEFAULT_TIMER_INTERVAL);
		}
    }
    
    private static Resource localize(final Resource r) {
    	try {
    		return r.getLocationResource();
    	} catch (SecurityException | NullPointerException e) {
    		return null;
    	}
    }
    
    public static boolean isBatteryLow(final Resource resource,final ApplicationManager appMan) {
    	if (resource instanceof ElectricityStorage) {
    		final HmMaintenancePattern channel = BatteryStateMonitoringController.getAssociatedHmMaintenance((ElectricityStorage) resource, appMan);
    		if (channel != null)
    			return channel.isLow.getValue();
    		final VoltageResource reading = ((ElectricityStorage) resource).internalVoltage().reading();
    		if (reading.isActive())
    			return reading.getValue() < 2.1;
    		final FloatResource soc = ((ElectricityStorage) resource).chargeSensor().reading();
    		if (soc.isActive())
    			return soc.getValue() < 0.35; // TODO good value?
    	}
    	if (resource instanceof HmMaintenance) {
    		final BooleanResource low = ((HmMaintenance) resource).batteryLow();
    		return low.isActive() && low.getValue();
    	}
    	return false;
    }
    
    void enableMonitoringForAllBatteries() {
    	Stream.concat(Stream.concat(
	    	appMan.getResourcePatternAccess().getPatterns(ElectricityStorageVoltagePattern.class, AccessPriority.PRIO_LOWEST).stream(),
	    	appMan.getResourcePatternAccess().getPatterns(ElectricityStorageSocPattern.class, AccessPriority.PRIO_LOWEST).stream()
    		),
    		appMan.getResourcePatternAccess().getPatterns(HmMaintenancePattern.class, AccessPriority.PRIO_LOWEST).stream()
    	)
    	.map(p -> p.model)
    	.distinct()
    	.forEach(this::getOrCreateSendMessages);
    }
    
    
 	private void sendMessage(String subject, String body) {
 		try {
 	 		messaging.sendMessage(appMan.getAppID(), new Message() {
				
				@Override
				public String title(OgemaLocale locale) {
					return subject;
				}
				
				@Override
				public MessagePriority priority() {
					return MessagePriority.LOW;
				}
				
				@Override
				public String message(OgemaLocale locale) {
					return body;
				}
				
				@Override
				public String link() {
					// TODO Auto-generated method stub
					return null;
				}
			});

		 } catch (RejectedExecutionException e) {
			 appMan.getLogger().warn("Message could not be sent: "+ e);
		 }
 		
 	}

    private String getMessage(final Resource r0) {
    	final Resource r;
    	try {
    		r = r0.getLocationResource();
    	} catch (SecurityException e) {
    		return null;
    	}
    	String name = null;
    	try {
    		name = nameService.getName(r, OgemaLocale.ENGLISH, true, true);
    	} catch (SecurityException | NullPointerException e) {}
    	final String loc = r.getLocation();
    	final StringBuilder sb = new StringBuilder();
    	sb.append(name != null ? name : loc);
    	sb.append(' ').append('(');
    	boolean requiresComma = false;
    	if (name != null) {
    		if (requiresComma)
    			sb.append(',').append(' ');
    		sb.append("path: ")
    			.append(loc);
    		requiresComma = true;
    	}
    	final String resType = getResourceType(r);
    	if (resType != null) {
    		if (requiresComma)
    			sb.append(',').append(' ');
    		sb.append("type: ")
    			.append(resType);
    		requiresComma = true;
    	}
    	String room = null;
    	try {
    		room = getName(ResourceUtils.getDeviceLocationRoom(r).getLocationResource());
    	} catch (SecurityException | NullPointerException e) {}
    	if (room != null) {
    		if (requiresComma)
    			sb.append(',').append(' ');
    		sb.append("room: ")
    			.append(room);
    		requiresComma = true;
    	}
    	try {
    		final ElectricityStorage battery = getAssociatedBattery(r, appMan);
    		final ElectricityStorageVoltagePattern voltagePattern = new ElectricityStorageVoltagePattern(battery);
    		if (appMan.getResourcePatternAccess().isSatisfied(voltagePattern, ElectricityStorageVoltagePattern.class)) {
    			if (requiresComma)
        			sb.append(',').append(' ');
    			sb.append("battery voltage: ")
	    			.append(voltagePattern.state.getValue())
	    			.append('V');
    			requiresComma = true;	
    		}
    		final ElectricityStorageSocPattern socPattern = new ElectricityStorageSocPattern(battery);
    		if (appMan.getResourcePatternAccess().isSatisfied(socPattern, ElectricityStorageSocPattern.class)) {
    			if (requiresComma)
        			sb.append(',').append(' ');
    			sb.append("battery state: ")
	    			.append(socPattern.state.getValue() * 100)
	    			.append('%');
    			requiresComma = true;	
    		}
    	} catch (SecurityException | NullPointerException e) {}
    	sb.append(')');
    	return sb.toString();
    }
    
    private static String getResourceType(final Resource r) {
    	try {
    		Resource parent = r;
    		while (parent != null) {
    			parent = parent.getParent();
    			if (parent instanceof PhysicalElement)
    				return parent.getResourceType().getSimpleName();
    		}
    	} catch (SecurityException | NullPointerException e) {
    	}
    	return null;
    }
 	
    private static String getName(final Resource r) {
    	try {
    		return ResourceUtils.getHumanReadableName(r);
    	} catch (SecurityException e) {
    		return r.getPath();
    	}
    }
    
    /*
     * This app uses a central configuration resource, which is accessed here
     */
    private final BatteryStateMonitoringConfig initConfigurationResource() {
		BatteryStateMonitoringConfig appConfigData = appMan.getResourceAccess().getResource(BatteryStateMonitoringApp.BASE_PATH);
		if (appConfigData != null) { // resource already exists (appears in case of non-clean start)
			appMan.getLogger().debug("{} started with previously-existing config resource", getClass().getName());
		}
		else {
			appConfigData = (BatteryStateMonitoringConfig) appMan.getResourceManagement().createResource(BatteryStateMonitoringApp.BASE_PATH, BatteryStateMonitoringConfig.class);
			appConfigData.activate(true);
			appMan.getLogger().debug("{} started with new config resource", getClass().getName());
		}
		return appConfigData;
    }
    
    /*
     * register ResourcePatternDemands. The listeners will be informed about new and disappearing
     * patterns in the OGEMA resource tree
     */
	public void close() {
		timer.destroy();
    }

	private BooleanResource getOrCreateSendMessages(Resource sensorDetected) {
		return getOrCreateSendMessages(sensorDetected, true);
	}
	
	public BooleanResource getOrCreateSendMessages(Resource sensorDetected, boolean enabled) {
		return appConfigData.availableSensors().getAllElements().stream()
			.filter(s -> sensorDetected.equalsLocation(s.sensorDetected()))
			.map(BatteryStateMonitoringProgramConfig::sendMessage)
			.findAny()
			.orElseGet(() -> {
				if (!enabled)
					return null;
				final BatteryStateMonitoringProgramConfig sensor = appConfigData.availableSensors().add();
				sensor.sensorDetected().setAsReference(sensorDetected);
				sensor.sendMessage().create();
				sensor.sendMessage().setValue(enabled);
				sensor.activate(true);
				return sensor.sendMessage();
			});
	}
	
	final ResourceList<BatteryStateMonitoringProgramConfig> getConfigs() {
		return appConfigData.availableSensors();
	}
	
	public static boolean channelHasNoAssociatedBattery(final HmMaintenance channel, final ApplicationManager appMan) {
		try {
			final Resource parent = channel.getLocationResource().getParent();
			return appMan.getResourcePatternAccess()
					.getSubresources(parent, ElectricityStorageVoltagePattern.class, true, AccessPriority.PRIO_LOWEST).isEmpty() &&
				   appMan.getResourcePatternAccess()
					.getSubresources(parent, ElectricityStorageSocPattern.class, true, AccessPriority.PRIO_LOWEST).isEmpty();
		} catch (SecurityException | NullPointerException e) {
			return true;
		}
	}
	
	public static ElectricityStorage getAssociatedBattery(Resource channel, final ApplicationManager appMan) {
		try {
			channel = channel.getLocationResource();
			if (channel instanceof ElectricityStorage) {
				return (ElectricityStorage) channel;
			}
			if (channel instanceof HmMaintenance) {
				return appMan.getResourcePatternAccess().getSubresources(channel.getParent(), ElectricityStorageVoltagePattern.class, true, AccessPriority.PRIO_LOWEST).stream()
				 	.map(pattern -> pattern.model)
				 	.findAny()
				 	.orElse(appMan.getResourcePatternAccess().getSubresources(channel.getParent(), ElectricityStorageSocPattern.class, true, AccessPriority.PRIO_LOWEST).stream()
				 			.map(pattern -> pattern.model)
				 			.findAny()
				 			.orElse(null));
			}
		} catch (SecurityException | NullPointerException e) {}
		return null;
	}
	
	public static HmMaintenancePattern getAssociatedHmMaintenance(final ElectricityStorage battery, final ApplicationManager appMan) {
		try {
			final List<HmMaintenancePattern> channels = appMan.getResourcePatternAccess()
					.getSubresources(battery.getLocationResource().getParent().getParent(), HmMaintenancePattern.class, false, AccessPriority.PRIO_LOWEST);
			return channels.isEmpty() ? null : channels.get(0);
		} catch (SecurityException | NullPointerException e) {
			return null;
		}
	}
	
}
