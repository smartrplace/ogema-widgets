package org.smartrplace.router.model;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;
import org.smartrplace.gateway.device.InterfaceInfo;

/** Representation of communication gateway such as MBus master*/
public interface CommunicationGatewayDevice extends PhysicalElement {

	/** @return hostname as reported by ubus system board. */
    StringResource hostname();
	
	/** @return host IP address or name used to connect to the router. */
	StringResource hostAddress();
    
    /**
     * The system uptime if reported by the system
     */
    TimeResource uptime();
	
    /** Traffic counted in bytes, may be reset e.g. each day, month or year*/
    FloatResource trafficCounter();
    
	ResourceList<InterfaceInfo> networkInterfaces();
	
	/** Each time a reading/heartbeat telegram is received the number of readings shall be written to 
	 * this resource
	 */
	IntegerResource numberReadingsReceived();
}