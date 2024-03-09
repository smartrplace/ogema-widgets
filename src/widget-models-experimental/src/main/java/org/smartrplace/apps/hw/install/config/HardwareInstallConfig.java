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
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.extended.alarming.DevelopmentTask;
import org.ogema.model.metering.ElectricityMeter;
import org.ogema.model.prototypes.Configuration;
import org.ogema.model.prototypes.Data;
import org.ogema.model.sensors.VolumeAccumulatedSensor;
import org.smartrplace.alarming.escalation.model.AlarmingEscalationSettings;

/** 
 * The global configuration resource type for this app.
 */
public interface HardwareInstallConfig extends Data {

	ResourceList<InstallAppDevice> knownDevices();
	ResourceList<DeviceTypeData> deviceHandlerData();
	
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
	AlarmingEscalationSettings escalation();
	
	/** Duration for which alarming events are given in the overview*/
	TimeResource basicEvalInterval();
	
	ElectricityConnection mainMeter();
	ElectricityMeter mainMeterAsElMeter();
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
	
	/** Allows to disable confirmation requests on the Hardware-Install-Expert page for some time
	 *	Options:<br>
	 *  0: not allowed (default)<br>
	 *  positive: disable confirmations until the time represented by the resource*/
	TimeResource disableConfirmationUntil();
	
	
	/** If resource is active and not empty then CO2 alarms will only be sent to a single user that is
	 * identified by the value of this resource as user name.
	 */
	StringResource singleCO2AlarmingUser();
	
	/** 0: Let page decide itself e.g. based on number of devices and properties set<br>
	 *  1: Force pages to allow ALL<br>
	 *  2: Force pages to deny selection of ALL
	 */
	IntegerResource allowAllDevicesInTablePagesMode();
	
	/** The following settings apply for all rooms that are not in manual, eco-mode or booking mode 
	 *  0: If configPending to thermostat then auto-mode shall be disabled. May be overwritten by thermostat-specific indication<br>
	 *  1: Auto-mode shall be enabled for all thermostats, a valid config has been sent. May be overwritten by thermostat-specific indication<br>
	 *  2: Auto-mode shall be disabled for all thermostats (e.g. because auto settings are unclear due to previous special settings). May be overwritten by thermostat-specific indication<br>
	 *  3: Auto-mode shall be disabled for all thermostats. No overwriting possible (to make sure e.g. for testing)
	 */
	IntegerResource autoThermostatMode();
	
	/** 0: Only perform if property is set<br>
	 *  1: off: No postpone takes place, which usually leads to weekly decalc <br>
	 *  2: weekly postpone (no decalc) <br>
	 *  3: daily decalc <br>
	 *  4: perform daily decalc from - until based on noPostponeStart/End, then perform weekly postpone to avoid further decalc
	 *  5: perform monthly postpone (may be adjusted by system)
	 */ 
	IntegerResource weeklyPostponeMode();
	TimeResource noPostponeStart();
	TimeResource noPostponeEnd();
	TimeResource blockAutoDecalcUntil();
	
	/** 0: Let thermostats and other devices send in default rate, typically 1/20
	 *  1: Force thermostats to default rate, even if thermostat has other special setting
	 *  2: Summer mode confirmed: Disable cyclic messaging / reduce to one message per day if possible
	 *  3: Force disabling cyclic messaging even if thermstat has other special setting
	 *  4: Reduced cyclic messaging for energy-saving mode during winter
	 *  5: Force reduced cyclic messaging even if thermostat has other special setting
	 */
	IntegerResource sendIntervalMode();
	
	/** If positive then all thermostats indicated for extended sending shall be set to sending with maximum interval until the
	 * absolute time indicated by this resource is reached
	 */
	TimeResource maxSendModeUntil();
	
	/** Data of devices that will connect later and will be created by the respective driver then*/
	ResourceList<PreKnownDeviceData> preKnownDevices();
	
	/** Duration of teachInMode in minutes*/
	FloatResource techInModeDuration();
	
	/** Devices marked for automated deletion after some time will be deleted at this time. This is usually the next midnight after the
	 * last marking operation. It is recommended to perform this manually when cleaning up and testing is finished.*/
	TimeResource nextTimeToDeleteMarkedDevices();
	
	/** Determines the level of escalation alarming messages (potentially also base alarming messages)
	 *  0: normal
	 *  -10: summer mode for mainly heating systems / winter mode for mainly cooling systems
	 */
	IntegerResource alarmingReductionLevel();
	
	/** History of configuration changes via GUI (for now roomcontrol only)*/
	ResourceList<StringResource> actionHistory();
	
	/** History of configuration changes in the issue settings*/
	ResourceList<StringResource> issueHistory();
	
	/** Last ID used in issue setting history*/
	IntegerResource issueHistoryID();
	
	/** Duration of last Update of Last Decalc for all thermostats */
	TimeResource lastDecalcCalculationDuration();
	
	/**0: Room assigned of devices can be fully set via API<br>
	 * 1: Room assigned via API is supported, but auto-removal via API is blocked
	 * 2: No room assignment can be changed via API
	 */
	IntegerResource deviceProtectedMode();
}
