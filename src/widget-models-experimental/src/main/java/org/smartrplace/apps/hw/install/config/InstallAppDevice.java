package org.smartrplace.apps.hw.install.config;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;
import org.ogema.model.prototypes.PhysicalElement;

public interface InstallAppDevice extends Data {
	/** Reference to the device, usually provided by driver*/
	public PhysicalElement device();
	
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
}
