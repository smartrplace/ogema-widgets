/**
 * ﻿Copyright 2018 Smartrplace UG
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
package org.smartrplace.apps.hw.install.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.prototypes.Configuration;
import org.ogema.model.prototypes.Data;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.VolumeAccumulatedSensor;

/** 
 * The global configuration resource type for this app.
 */
public interface HardwareInstallConfig extends Data {

	ResourceList<InstallAppDevice> knownDevices();
	
	/** If true then listeners are active. If false then listeners are not active for performance reasons*/
	BooleanResource isInstallationActive();
	
	/** 0: do not activate logging automatically
	 *  1: activate logging for all datapoints of new devices
	 *  2: activate all logging configured for all devices found on each startup: Means that for existing devices
	 *  	logging is checked for all datapoints and that datapoints that have been added for existing devices via
	 *  	software updates are also activated. 
	 * @return
	 */
	IntegerResource autoLoggingActivation();
	/** If true for all data logged also data transfer is activated*/
	BooleanResource autoTransferActivation();
	
	/** Indication of the room selected that shall be displayed or special String indicating that
	 * devices from all rooms shall be displayed. See {@link RoomSelectorDropdown} for details. Note that
	 * we save this information persistently for all users/sessions as this is most efficient during installation in
	 * many cases.
	 */
	StringResource room();

	/**
	 * Currently selected filter for device installation.
	 * See {@room} and {@link InstallationStatusFilterDropdown}.
	 */
	StringResource installationStatusFilter();
	
	/** If a template exists for a device type it shall be applied to new devices automatically
	 * if this is true
	 */
	BooleanResource autoConfigureNewDevicesBasedOnTemplate();
	
	StringResource initDoneStatus();

	/** If true the relation of devices to DeviceHandlerProviders is not determined via the PatternsFound, but
	 * via the ResourceTypes. This may lead to showing devices in more than one DeviceHandlerProvider
	 */
	@Deprecated //not supported anymore
	BooleanResource includeInactiveDevices();
	
	/** If true alarming is started, otherwise no alarm detection is performed*/
	BooleanResource isAlarmingActive();
	/** Maximum alarming messages to be sent within {@link #bulkMessageIntervalDuration()} before
	 * messages are aggregated into a single bulk message*/
	IntegerResource maxMessageNumBeforeBulk();
	/** Duration of bulk message aggregation*/
	TimeResource bulkMessageIntervalDuration();
	
	ResourceList<Configuration> alarmingConfig();
	/** Duration for which alarming events are given in the overview*/
	TimeResource basicEvalInterval();
	
	ElectricityConnection mainMeter();
	VolumeAccumulatedSensor mainGasMeter();
}
