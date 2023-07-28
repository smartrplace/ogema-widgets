package org.ogema.model.extended.alarming;

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
	
	/** This link shall be set for ongoing issues on the gateway to access device information like for AlarmGroupData
	 * elements that are direct children in {@link InstallAppDeviceBase#knownFault()}
	 * Note: The element shall be kept locally even if released.
	 */
	InstallAppDevice parentForOngoingIssues();
}
