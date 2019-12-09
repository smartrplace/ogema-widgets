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

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.recordeddata.RecordedDataConfiguration;
import org.ogema.core.recordeddata.RecordedDataConfiguration.StorageType;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.tools.resource.util.LoggingUtils;

import de.iwes.util.resource.ResourceHelper;

public class LogHelper {
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
		if(device == null) return null;
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
}
