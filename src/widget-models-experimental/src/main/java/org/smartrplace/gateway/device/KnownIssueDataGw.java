package org.smartrplace.gateway.device;

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface KnownIssueDataGw extends PhysicalElement {
	/** true if alarming is active on the gateway*/
	BooleanResource alarmingActivated();
	
	@NonPersistent
	/** Total number of datapoints for which alarming is activated*/
	IntegerResource activeAlarmSupervision();
	@NonPersistent
	/** Total number of datapoints that are in alarm state*/
	IntegerResource datapointsInAlarmState();
	@NonPersistent
	/** Total number of datapoints listed on hardware installation page*/
	IntegerResource datapointsTotal();
	@NonPersistent
	/** Total number of devices listed on hardware installation page*/
	IntegerResource devicesTotal();
	
	@NonPersistent
	IntegerResource knownIssuesUnassigned();
	@NonPersistent
	IntegerResource knownIssuesAssignedBattery();
	@NonPersistent
	IntegerResource knownIssuesAssignedDevNotReacheable();
	@NonPersistent
	IntegerResource knownIssuesAssignedSignalStrength();
	@NonPersistent //other operation
	IntegerResource knownIssuesAssignedOther();
	@NonPersistent
	IntegerResource knownIssuesAssignedOperationOwn();
	@NonPersistent
	IntegerResource knownIssuesAssignedDevOwn();
	@NonPersistent
	IntegerResource knownIssuesAssignedCustomer();
	
	@NonPersistent
	IntegerResource knownIssuesOpExternal();
	@NonPersistent
	IntegerResource knownIssuesDevExternal();
	
	@NonPersistent
	IntegerResource qualityShort();
	@NonPersistent
	IntegerResource qualityLong();
	@NonPersistent
	IntegerResource qualityShortGold();
	@NonPersistent
	IntegerResource qualityLongGold();
	
	/** Number of devices without trash of several types, index:
	 * [0]: other relevant devices
	 * [1]: virtual devices
	 * [2]: devices in {@link #devicesTotal()} that are not part of any other index
	 * [3]: thermostats
	 * [4]: wall thermostats
	 * [5]: air conditioners
	 * [6]: window sensors
	 * [7]: sensor devices
	 * [8]: OnOffSwitch
	 * [9]: rooms
	 * [10]: CCUs
	 * [11]: HAPs
	 * [12]: GlinetRouter
	 * [13]: CO2-sensor
	 * [14]: lamp
	 * [15]: TOOO: other metering devices
	 * [16]: TODO: other actor devices
	 */
	IntegerArrayResource devicesByType();
	
	/** Datapoints per device type, for index see {@link #devicesByType()}*/
	IntegerArrayResource datapointsByType();
	IntegerArrayResource datapointsByTypeConfiguredForAlarm();

	/** Here we count the devices listed on KnownIssuePage, for index see {@link #devicesByType()}*/
	IntegerArrayResource devicesByTypeInIssueState();
	IntegerArrayResource devicesByTypeWithActiveAlarms();
	
	IntegerArrayResource devicesByTypeIssuesNone();
	IntegerArrayResource devicesByTypeIssuesBacklog();
	IntegerArrayResource devicesByTypeIssuesSupDevUrgent();
	IntegerArrayResource devicesByTypeIssuesSupDevStd();
	IntegerArrayResource devicesByTypeIssuesOpUrgent();
	IntegerArrayResource devicesByTypeIssuesOpStd();
	IntegerArrayResource devicesByTypeIssuesManufacturer();
	IntegerArrayResource devicesByTypeIssuesCustomer();

	IntegerResource operationActionRequestedShortTerm();
	IntegerResource appointmentRequestedShortTerm();
	IntegerResource dev1RequestedShortTerm();
	IntegerResource dev2RequestedShortTerm();

	IntegerArrayResource devicesBatteryCritical();
	IntegerArrayResource devicesBatteryWarning();
	IntegerArrayResource devicesBatteryChangeRecommended();
	IntegerArrayResource devicesBatteryEmpty();
	TimeResource lastBuildTime();
	
	/** Number of issues assigned as relevant without notification. Note that we only require one issue per device handler type per
	 * analysis assigned - type to be set, so a positive number here may not indicate a real issue.
	 */
	IntegerArrayResource devicesByTypeIssuesWithoutNotification();

	/** Number of device handlers for which at least one notification would be required, but none is set */
	IntegerResource notificationPerTypeMissing();

	IntegerArrayResource devicesByTypeIssuesNotifcation3TimesSent();
	
	/** Summary provided by gateway regarding types still assigned None */
	StringResource alarmTypeSummary();
	
	MemoryTimeseriesPST referenceForDeviceHandler();
}
