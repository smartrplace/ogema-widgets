package org.ogema.model.extended.alarming;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.smartrplace.apps.hw.install.config.InstallAppDevice;
import org.smartrplace.apps.hw.install.config.InstallAppDeviceBase;

/** AlarmGroupData provided to be presented on superior instance and/or to be stored for
 * statistical purposes when released
 */
public interface AlarmGroupDataMajor extends AlarmGroupData {
	/** If active and value > 0, then the known issue is released */
	TimeResource releaseTime();
	
	/** If active and positive the element has been moved to trash.
	 * It can be deleted automatically after the time set is expired
	 */
	TimeResource keepAsTrashUntil();
	
	/** TODO: Should this be a string?
	 * Code for final diagnosis
	 */
	StringResource finalDiagnosis();
	
	/**
	 * A release comment 
	 */
	StringResource finalAnalysisComment();
	
	/**
	 * Indicate if the problem arose in a dev project 
	 */
	StringResource featureUnderDevelopment();
	
	
	/** A major known fault may be related to more than one device. The deviceId of each device
	 * shall be listed here.<br>
	 * Note: As the devices are not present on superior we cannot use references here.<br>
	 * The first element shall represent the device which the issue was a child of originally
	 * TODO: Should we better use device locations here?
	 */
	StringArrayResource devicesRelated();
	
	/** Name of the first element device of {@link #devicesRelated()}*/
	StringResource firstDeviceName();
	
	/** This link shall be set for ongoing issues on the gateway to access device information like for AlarmGroupData
	 * elements that are direct children in {@link InstallAppDeviceBase#knownFault()}
	 * Note: The element shall be kept locally even if released.
	 */
	InstallAppDevice parentForOngoingIssues();
	
	/** More information on emails and phone calls shall be obtained from wiki / support email Sent folder*/
	TimeResource lastEmailSent();
	TimeResource lastPhoneCallWithCustomer();
	
	/** Status regarding blocking: This shall be present if this is an additional blocking KnownIssue,
	 * not a standard known issue<br>
	 * 0: no special blocking status (may not be an additional issue at all)
	 * 4: Waiting for onsite support
	 * 5: Waiting for onsite support (send reminders to onsite support)
	 * 8: Waiting for customer
	 * 9: Waiting for customer (send reminders to customer)
	 * 10: Waiting for customer (send official delay notification ("Verzugsmeldung")
	 * 12: waiting for onsite partner
	 * 13: waiting for onsite partner (send reminders to partner)
	 * 14: waiting for onsite partner (send official delay notification ("Verzugsmeldung")
	 * 16: waiting for internal by auto-reminder
	 * 20: waiting for supplier
	 * 21: waiting for supplier (send reminders to supplier)
	 * 22: waiting for supplier (send official delay notification ("Verzugsmeldung")
	 */
	IntegerResource blockingIssueStatus();
	
	/** Only relevant for devices blocked : List of references to alarms blocked as long as
	 * alarm state exists*/
	ResourceList<AlarmConfiguration> alarmsBlocked();
}
