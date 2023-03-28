package org.smartrplace.model.sync.mqtt;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.extended.alarming.AlarmGroupData;
import org.ogema.model.prototypes.Data;

/** Resource model to be created as top-level resource on superior instances for MQTT resource synching.
 * The name shall be _<gatewayId>.*/
public interface GatewaySyncData extends Data {
	/** If the value of this resource changes then an update of the resource structure shall be triggered*/
	IntegerResource requestResourceUpdate();
	
	/** When the communication to the gateway is lost for a time longer than {@link #connectionLostInterval()}
	 * the this shall be set to true. If communication is back then it is set to false. This can be used to
	 * switch to local control if the cloud connection is lost.*/
	BooleanResource communicationDisturbed();
	
	/** Maximum interval in milliseconds for which MQTT connection may be lost before
	 * {@link #communicationDisturbed()} is set to true
	 */
	TimeResource connectionLostInterval();
	
	/** Locations of top-level resources to be included into the synchronization from subgateway perspective.
	 * The resource gatewaySyncData of type GatawaySyncData shall be added by the superior instance. Each entry shall have the following format:<br>
	 * <listname> : <gwId> : <resourcepath> [, <resourcepath>]* : <targetpath>
	 * with the following elements:
	 *     * listname : A unique, but arbitrary name for each entry
	 *     * gwId : Id of gateway to which connection is made (must be ID of subgateaway if applied there)
	 *     * resourcepath: Path of resource on subgateway
	 *     * targetpath: usually is gw<gatewayId>, but could be applied as a subresource on the collecting gateway
	 */
	StringArrayResource toplevelResourcesToBeSynchronized();
	
	/*************************
	 * Commands as draft
	 *************************/
	/** Several commands may be given in a comma-separated list. If the command contains a comma it must be surrounded by
	 * quotes. Quotes may be escaped. After each command an entry containing a time-stamp must be given that is used
	 * to identify the response. Entries is this list are deleted only by the gateway that writes the
	 * entries.
	 */
	StringArrayResource commandsGwToServer();
	
	/** Responses of the server to the requests sent by the gateway in commandsGwToServer.
	 * Also after each entry a timestamp shall be given that indicates the response. Entries must
	 * also be cleaned up the gateway, the resource is only written by the server.*/
	StringArrayResource responseOnGwToServer();
	
	/** Counterpart to commandsGwToServer for writing commands from the server that are
	 * executed on the gateway console.
	 */
	StringArrayResource commandsServerToGw();

	/** Counterpart to responseOnGwToServer for writing commands from the server that are
	 * executed on the gateway console.
	 */
	StringArrayResource responseOnServerToGw();

	/*************************************************
	 * Only relevant for synchronization to superior
	 *************************************************/
	
	/** KnownIssues to be synchronized to be superior.
	 * TODO: Model needs to be extended a littel bit to make link to the base-known issues in knownDevices()
	 */
	ResourceList<AlarmGroupData> superiorIssues();
	
	/** Each entry shall hold a device room location and a room name or location separated by a comma
	 * The device room location shall be the location gateway side
	 * TODO: Methods using this set deprecated as not really used and tested yet.
	 */
	StringArrayResource deviceNames();
	
	/** This list usually has a decorator {@link GatewaySyncElementControl#syncMode()}=2
	 */
	//ResourceList<Room> rooms();
	
	/** This list usually has a decorator {@link GatewaySyncElementControl#syncMode()}=1 .
	 *  So it contains the global device configuration data.
	 */	
	//HardwareInstallConfig hardwareInstallConfig();
	
	/** This list usually has a decorator {@link GatewaySyncElementControl#syncMode()}=2 .
	 * Contains the relevant device configuration data. On the subgateway the toplevel
	 * resource created by this shall be linked into hardwareInstallConfig.
	 */
	//ResourceList<InstallAppDevice> hardwareInstallConfig_knownDevices();
}
