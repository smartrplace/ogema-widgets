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
import org.ogema.model.extended.alarming.DevelopmentTask;
import org.ogema.model.prototypes.Configuration;
import org.ogema.model.prototypes.Data;
import org.ogema.model.sensors.VolumeAccumulatedSensor;

/** 
 * The global configuration resource type for this app.
 */
public interface HardwareInstallConfig extends Data {

	ResourceList<InstallAppDevice> knownDevices();
	ResourceList<DevelopmentTask> knownDevelopmentTasks();
	
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
	
	/** Note that for some changes to take effect a system restart is necessary as pages have to be added/
	 * removed at applications.
	 *  0: No extended view mode
	 *  1: master only. If master-only is not possible (as pages are added/removed at applications cannot be
	 *  	controlled on a per-user base) no extended mode is supported at such positions.
	 *  2: all users if not blocked by other permissions
	 */
	IntegerResource extendedViewMode();
	
	/**
	 * 0: Location, KNI
	 * 1: KNI, Location
	 */
	IntegerResource showModePageOrder();
	
	/** Allows to enable manipulation of {@link InstallAppDevice#deviceId()}s. This shall only be done as long as the hardware
	 * devices have not been labelled yet with the ids. Options:<br>
	 *  0: not allowed (default)<br>
	 *  positive: allow manual changes until the time represented by the resource (take care not to leave any doubles !). This also allows
	 *  auto-reset to be triggered by button.
	 */
	TimeResource deviceIdManipulationUntil();
	/** If true then button for auto-reset of deviceIds cannot be activated anymore. This resource can be set to false only
	 * in resource view as this is very dangerous. Blocking is activated after running auto-reset once and when later installation
	 * steps are detected.
	 */
	BooleanResource blockAutoResetOfDeviceIds();
}
