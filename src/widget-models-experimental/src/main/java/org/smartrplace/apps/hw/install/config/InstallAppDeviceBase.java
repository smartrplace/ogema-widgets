package org.smartrplace.apps.hw.install.config;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.extended.alarming.AlarmConfiguration;
import org.ogema.model.extended.alarming.AlarmGroupData;
import org.ogema.model.extended.alarming.AlarmGroupDataMajor;
import org.ogema.model.extended.alarming.AlarmingData;
import org.ogema.model.extended.alarming.DevelopmentTask;
import org.ogema.model.prototypes.Data;
import org.ogema.model.prototypes.PhysicalElement;
import org.smartrplace.gateway.device.GatewaySuperiorData;

/** This model can represent a virtual device for a device type. A real device should be represented by
 * {@link InstallAppDevice}.
 *
 */
public interface InstallAppDeviceBase extends Data {
	/** Reference to the device, usually provided by driver*/
	public PhysicalElement device();
	/** Reference to be set if real device-like source is not a PhysicalElement*/
	public Resource realDevice();
	
	/** The resource contains the devHandlerId*/
	public StringResource devHandlerInfo();
	
	/** Single datapoint alarms for the device*/
	public ResourceList<AlarmConfiguration> alarms();
	
	/** Special alarming settings for the device if existing*/
	public DevelopmentTask devTask();
	
	/** If active and positive then each relevant {@link AlarmConfiguration#maxIntervalBetweenNewValues()} will be checked if shorter than
	 * this value. If so the effective interval will be extended to this value. Interval in minutes.
	 */
	public FloatResource minimumIntervalBetweenNewValues();
	
	/** Known fault on device. Note that in the future room and gateway fault states may be stored in
	 * {@link AlarmingData#knownSystemFaults()}. If not active of not existing then the device has no
	 * active fault.*/
	public AlarmGroupData knownFault();
	
	/** Additional known faults relevant on gateway-level or not assignable to a single device
	 * and for issues that are blocked/waiting e.g. until next
	 * onsite-maintenance or until other external actions are done. This is foreseen for device
	 * issues that do not completely block device operation and do not prevent the system from
	 * receiving relevant data. So normal alarming shall still take place, but the
	 * event shall be shown on the Installation-Page.<br>
	 * NOTE: This list shall only contain references to the respective entries in
	 * {@link GatewaySuperiorData#majorKnownIssues()}.
	 */
	ResourceList<AlarmGroupDataMajor> blockedKnownFaults();
	
	/** Devices that cannot or shall not be deleted entirely/set inactive can be marked as trash. These devices will also not be
	 * processed anymore.
	 */
	BooleanResource isTrash();
	
	/** 0 = do not delete automatically<br>
	 *  1 = delete device on next trash cleating (usually only relevant if isTrash=true)
	 */
	IntegerResource trashStatus();
	
	/** Templates are used for general configurations per device type, e.g. for alarming.
	 * Contains the DatapointGroup location for which it is template
	 * TODO: In the future a device may be used as template for several device type groups, this is not supported yet*/
	StringResource isTemplate();
}
