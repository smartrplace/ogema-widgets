package org.smartrplace.model.sync.mqtt;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.Data;
import org.smartrplace.apps.hw.install.config.HardwareInstallConfig;
import org.smartrplace.apps.hw.install.config.InstallAppDevice;

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
	 * The resource gatewaySyncData of type GatawaySyncData shall be added by the superior instance
	 */
	StringArrayResource toplevelResourcesToBeSynchronized();
	
	
	/***********************
	 * Default Payload data: Application-specific models may be added as decorators
	 * e.g. for room control.
	 ***********************/
	
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