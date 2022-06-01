package org.smartrplace.model.sync.mqtt;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.Data;

public interface GatewaySyncData extends Data {
	/** If the value of this resource changes then an update of the resource structure shall be triggered*/
	IntegerResource requestResourceUpdate();
	
	/** When the communication to the gateway is lost for a time longer than {@link #maxConnectionLost()}
	 * the this shall be set to true. If communication is back then it is set to false. This can be used to
	 * switch to local control if the cloud connection is lost.*/
	BooleanResource communicationDisturbed();
	
	/** Maximum interval in milliseconds for which MQTT connection may be lost before
	 * {@link #communicationDisturbed()} is set to true
	 */
	TimeResource maxConnectionLost();
	
	/** Locations of top-level resources to be included into the synchronization from subgateway perspective.
	 * The resource gatewaySyncData of type GatawaySyncData shall be added by the superior instance
	 */
	StringArrayResource toplevelResourcesToBeSynchronized();
	
	/** This list usually has a decorator {@link GatewaySyncElementControl#syncMode()}=2
	 */
	ResourceList<Room> rooms();
}
