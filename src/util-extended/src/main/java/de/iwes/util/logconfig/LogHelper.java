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
package de.iwes.util.logconfig;

import java.util.List;

import org.joda.time.chrono.IslamicChronology;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.recordeddata.RecordedDataConfiguration;
import org.ogema.core.recordeddata.RecordedDataConfiguration.StorageType;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.LoggingUtils;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.ogema.tools.resourcemanipulator.timer.CountDownDelayedExecutionTimer;
import org.smartrplace.gateway.device.GatewayDevice;

import de.iwes.util.resource.ResourceHelper;
import de.iwes.util.resource.ValueResourceHelper;

public class LogHelper {
	public static final long MINUTE_MILLIS = 60000;
	public static final long HOUR_MILLIS = 60*60000;
	public static final long DAY_MILLIS = 24*HOUR_MILLIS;
	public static final long YEAR_MILLIS = (long)(365.25*DAY_MILLIS);

	//public static final String generalScheduleViewerConfigName = "ScheduleViewerConfigGeneral";
	/**Short string that can be added to a single value resource information to indicate to the
	 * user whether and how the resource is logged
	 * @return (<Fixed Interval>), (C) oder (U)
	 */
	public static String getLogInfo (FloatResource res) {
		if(res.getHistoricalData().getConfiguration() == null) return "";
		switch(res.getHistoricalData().getConfiguration().getStorageType()) {
		case FIXED_INTERVAL:
			return "("+res.getHistoricalData().getConfiguration().getFixedInterval()/1000+")";
		case ON_VALUE_CHANGED:
			return "(C)";
		case ON_VALUE_UPDATE:
			return "(U)";
		default:
			return "";
		}
	}
	

    /**Get last four digits of homematic device id to be added to a log label*/
    public static String getHmDeviceId(ResourcePattern<?> homeMaticPattern) {
		return getDeviceId(homeMaticPattern.model);
    }
    /**Get last four digits of homematic device id to be added to a log label*/
	public static String getDeviceId(Resource hmDevice) {
		Resource hmParent = ResourceHelper.getFirstParentOfType(hmDevice, "HmDevice");
		String name;
		if(hmParent != null) {
			name = hmParent.getName();
		} else {
			name = ResourceHelper.getToplevelResource(hmDevice).getName();
		}
		String deviceId = name.substring(name.length()-4);
		return deviceId;
    }
	
	/** Get resource of device to be used as primary device for the input resource
	 * for user interaction etc.
	 * @param subResource resource for which the primary device resource shall be returned
	 * @param locationRelevant if true only device resources will be returned with an active subresource
	 * 		location e.g. indicating the room where the device is placed. It is not considered if the
	 * 		subResource has an element of org.ogema.drivers.homematic.xmlrpc.hl.types.HmMaintenance in
	 * 		its parent path. Default is true.
	 * @param useHighest if true the highest PhysicalElement above the input subResource is returned, otherwise
	 * 		the lowest fitting. Default is true.
	 * @return device resource or null if no suitable resource was found
	 */
	public static PhysicalElement getDeviceResource(Resource subResource, boolean locationRelevant) {
		return getDeviceResource(subResource, locationRelevant, true);
	}
	public static PhysicalElement getDeviceResource(Resource subResource, boolean locationRelevant, boolean
			useHighest) {
		Resource hmCheck = ResourceHelper.getFirstParentOfType(subResource, "org.ogema.drivers.homematic.xmlrpc.hl.types.HmMaintenance");
		if(hmCheck != null) {
			Resource parent = hmCheck.getParent();
			if(parent == null) return null;
			List<PhysicalElement> devices = parent.getSubResources(PhysicalElement.class, false);
			if(devices.isEmpty()) return null;
			if(devices.size() > 1) return null; //throw new IllegalStateException("HmDevice should have maximum 1 PhysicalElement as child, "+parent.getLocation()+" has "+devices.size());
			return devices.get(0);
		}
		PhysicalElement device = ResourceHelper.getFirstParentOfType(subResource, PhysicalElement.class);
		if(device == null) {
			if(subResource instanceof PhysicalElement)
				return (PhysicalElement) subResource;
			return null;
		}
		PhysicalElement highestWithLocation = ((!locationRelevant) || device.location().isActive())?device:null;
		Resource parent = device.getParent();
		while(true) {
			if((!useHighest) && (highestWithLocation != null)) return highestWithLocation;
			if(parent != null && parent instanceof PhysicalElement) {
				device = (PhysicalElement) parent;
				if((!locationRelevant) || device.location().isActive()) highestWithLocation = device;
				parent = device.getParent();
			} else {
				if(parent != null) {
					parent = ResourceHelper.getFirstParentOfType(parent, PhysicalElement.class);
					if(parent == null) return highestWithLocation;
					else continue;
				}
				if(highestWithLocation != null) return highestWithLocation;
				return device;
			}
		}
	}
	
	/**Activate standard logging (ON_VALUE_UPDATE)
	 * 
	 * @param target resource for which standard logging shall be activated
	 * @deprecated use {@link LoggingUtils#activateLogging(SingleValueResource, long)}
	 */
	@Deprecated
	public static void activateLogging(SingleValueResource target) {
		RecordedDataConfiguration powerConf = new RecordedDataConfiguration();
		powerConf.setStorageType(StorageType.ON_VALUE_UPDATE);
		if(target instanceof FloatResource) {
			((FloatResource)target).getHistoricalData().setConfiguration(powerConf);
		} else if(target instanceof IntegerResource) {
			((IntegerResource)target).getHistoricalData().setConfiguration(powerConf);
		} else if(target instanceof BooleanResource) {
			((BooleanResource)target).getHistoricalData().setConfiguration(powerConf);
		} else if(target instanceof TimeResource) {
			((TimeResource)target).getHistoricalData().setConfiguration(powerConf);
		}
	}
	
	public static SampledValue getLastSampledValue(SingleValueResource resource) throws IllegalArgumentException {
		RecordedData rd = LoggingUtils.getHistoricalData(resource);
		if(rd == null)
			return null;
		return rd.getPreviousValue(Long.MAX_VALUE);
	}
	public static Long getLastWriteTime(SingleValueResource resource) throws IllegalArgumentException {
		SampledValue sv = getLastSampledValue(resource);
		if(sv == null)
			return null;
		return sv.getTimestamp();
	}
	public static Float getLastValue(SingleValueResource resource) throws IllegalArgumentException {
		SampledValue sv = getLastSampledValue(resource);
		if(sv == null)
			return null;
		return sv.getValue().getFloatValue();
	}
	
	/** Only writes to non-persistent resources that are NaN or zero.<br> 
	 * This method stops logging before writing to avoid writing a datapoint when resetting the nonpersistent resource to
	 * the previous value. This usually
	 * is not intended if e.g. no data is received from the device
	 * @param resource
	 * @return
	 */
	public static Float setNonpersistentResourceWithLastLog(SingleValueResource resource) {
		if(!resource.isNonpersistent())
			return null;
		float curVal = ValueResourceUtils.getFloatValue(resource);
		if(!(Float.isNaN(curVal) || (curVal == 0)))
			return curVal;
		Float lastVal = getLastValue(resource);
		if(lastVal != null && (lastVal != 0)) {
			RecordedData rec = LoggingUtils.getHistoricalData(resource);
			RecordedDataConfiguration config = rec.getConfiguration();
			LoggingUtils.deactivateLogging(resource);
			ValueResourceUtils.setValue(resource, (float)lastVal);
			if(config != null)
				rec.setConfiguration(config);
		}
		return lastVal;
	}
	
	public static float getValueOrLastValue(SingleValueResource resource) {
		float curVal = ValueResourceUtils.getFloatValue(resource);
		if(!resource.isNonpersistent())
			return curVal;
		if(!(Float.isNaN(curVal) || (curVal == 0)))
			return curVal;
		Float lastVal = getLastValue(resource);
		if(lastVal != null)
			return lastVal;
		return curVal;
	}
	
	/** Log startup of a certain app
	 * 
	 * @param startupAppId must be a power of 2
	 * @param appManager
	 */
	private static boolean loggedStartup = false;
	public static void logStartup(int startupAppId, ApplicationManager appManager) {
        GatewayDevice gw = ResourceHelper.getLocalDevice(appManager);
        if(!gw.systemRestart().exists()) {
        	gw.systemRestart().create();
        	gw.systemRestart().getAndAdd(startupAppId);
        	gw.systemRestart().activate(false);
        } else {
        	int curVal = gw.systemRestart().getValue();
        	if((curVal & startupAppId) != 0) {
        		appManager.getLogger().warn("Startup ID "+startupAppId+" re-notification!");
        		return;
        	}
        	gw.systemRestart().getAndAdd(startupAppId);
        }
        if(!loggedStartup) {
        	if(!gw.systemRestartCounterLastHours().exists()) {
        		ValueResourceHelper.setCreate(gw.systemRestartCounterLastHours(), 1);
        	} else
        		gw.systemRestartCounterLastHours().getAndAdd(1);
        	new CountDownDelayedExecutionTimer(appManager, 2*HOUR_MILLIS) {
				
				@Override
				public void delayedExecution() {
					gw.systemRestartCounterLastHours().setValue(0);
				}
			};
        	loggedStartup = true;
        }
	}
	
	public static void resetStartup(ApplicationManager appManager) {
        GatewayDevice gw = ResourceHelper.getLocalDevice(appManager);
        ValueResourceHelper.setCreate(gw.systemRestart(), 0);
	}

}
