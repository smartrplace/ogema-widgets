package org.smartrplace.apps.hw.install.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.extended.alarming.AlarmConfiguration;
import org.ogema.model.extended.alarming.AlarmGroupData;
import org.ogema.model.extended.alarming.AlarmingData;
import org.ogema.model.prototypes.Data;
import org.ogema.model.prototypes.PhysicalElement;

public interface InstallAppDevice extends Data {
	/** Reference to the device, usually provided by driver*/
	public PhysicalElement device();
	
	/** The resource contains the devHandlerId*/
	public StringResource devHandlerInfo();
	
	/** Description of the installation location. If the device is alread placed in correct room then
	 * the room should be represented by the resource <device>/location/room . Otherwise the description
	 * may contain room information to be used for setting up the room and moving the device later on. 
	 */
	public StringResource installationLocation();
	
	/** Any additional free text comment from installation*/
	public StringResource installationComment();
	
	/** 0: "Unknown": nothing done / unknown<br>
	 *  1: "SerialNumberRecorded": Teach-in process finished by operation, serial number was copied to the installation spreadsheet<br>
	 *  3: "PackedForShipping": Confirmation that device was packed into parcel for customer<br>
	 *  5: "Shipping": Parcel handed over to delivery service or to installation partner bringing the hardware to the customer<br>
	 *  7: "AtCustomerSite": Delivery confirmed by delivery service, installation partner or customer<br>
	 * 10: "Installed": device installed physically<br>
	 * 20: "InstallationTested": Physical installation done including all on-site tests<br>
	 * 30: "Operational": All configuration finished, device is in full operation<br>
	 * -10: Error in physical installation and/or testing (more details in comment)<br>
	 * -20: Error in configuration, device cannot be used/requires action for real usage
	 * 		(more details in comment)<br>
	 */
	public IntegerResource installationStatus();
	
	/** A support status different from zero indicates that the device does not operate as expected.
	 * This can be used to indicate problems that still allow normal operation of the device, but
	 * indicate to the technical support/development that the device should be checked when possible.
	 * 0: OK
	 * 1: Please check device (more details in comment)
	 */
	//public IntegerResource supportStatus();

	/**
	 * Device ID unique to local installation.
	 * Consists of device type prefix and a local serial number, starting with 1.
	 */
	public StringResource deviceId();
	
	/** Single datapoint alarms for the device*/
	public ResourceList<AlarmConfiguration> alarms();
	
	/** Known fault on device. Note that in the future room and gateway fault states may be stored in
	 * {@link AlarmingData#knownSystemFaults()}. If not active of not existing then the device has no
	 * active fault.*/
	public AlarmGroupData knownFault();
	//public ResourceList<KnownFault> knownFaults();
	
	/** Devices that cannot or shall not be deleted entirely/set inactive can be marked as trash. These devices will also not be
	 * processed anymore.
	 */
	BooleanResource isTrash();
	
	/** Templates are used for general configurations per device type, e.g. for alarming.
	 * Contains the DatapointGroup location for which it is template
	 * TODO: In the future a device may be used as template for several device type groups, this is not supported yet*/
	StringResource isTemplate();

	/** Provided for transmission to superior via heartbeat*/
	public IntegerResource dpNum();
	
	/** Used for internal status supervision of some device types*/
	//public TimeResource supervisionStatus();
}
