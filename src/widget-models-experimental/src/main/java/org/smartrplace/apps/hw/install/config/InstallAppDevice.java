package org.smartrplace.apps.hw.install.config;

import org.ogema.core.model.array.TimeArrayResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;

public interface InstallAppDevice extends InstallAppDeviceBase {
	/** Description of the installation location. If the device is alread placed in correct room then
	 * the room should be represented by the resource <device>/location/room . Otherwise the description
	 * may contain room information to be used for setting up the room and moving the device later on. 
	 */
	public StringResource installationLocation();
	
	/** Any additional free text comment from installation*/
	public StringResource installationComment();
	
	/** Free status text field for current operation situation */
	public StringResource operationStatus();
	
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
	
	/** Provided for transmission to superior via heartbeat*/
	public IntegerResource dpNum();
	
	/** Only relevant for devices connected via IP to the gateway. The connection information given here is the
	 * connection that is used by the gateway. So a device or a controller may have an additional VPN connection that may not be
	 * given here if it is not used by the gateway. The following options are defined:<br>
	 * IP::<IP-Address> : device with a fixed IP address in the local network that cannot change via DHCP<br>
	 * VPN::<IP-Address> : device with a fixed IP address in the VPN that cannot change via DHCP<br>
	 * URL::<URL> cloud service that is connected via a URL or local URL access
	 * MAC::<MAC-Address> : device receives IP address via  DHCP. The device IP address can be found via a local network ping scan.<br>
	 * Sub::<Controller deviceID> : device is connected in a subnetwork of a controller. So the IP address and the MAC address visible to
	 * 		the gateway are the addresses of the respective controller. <br>
	 * Bridge::<Controller deviceID> : bridge configuration receiving a local IP address via DHCP via a controller. So the MAC address
			reported via a network scan is the MAC address of the controller.<br>
	 * MQTT::<Broker> : Device connects to MQTT broker. TODO: To discuss: Should this information provided in the ComType enum?<br>
	 */
	public StringResource networkIdentifier();
	
	/** IP address that may be available as a backup. Usually a VPN IP address*/
	public StringResource backupAddress();

	/** For devices without fixed IP address the last address detected can be given here, which can speed up debugging
	 * if the address did not change in the meantime and can help to detect changes in the address assigned via DHCP.*/
	public StringResource lastAddress();

	/** For devices with complex connection modes the MAC address should be given here 
	 * for debugging purposes
	 */
	public StringResource macAddress();
	
	/** Used for internal status supervision of some device types*/
	//public TimeResource supervisionStatus();
	
	/** For each ExternalEscalationProvider the escalation time is given here according to index.
	 *  If not used then a negative value shall be set.
	 */
	public TimeArrayResource externalEscalationProviderIds();
}
