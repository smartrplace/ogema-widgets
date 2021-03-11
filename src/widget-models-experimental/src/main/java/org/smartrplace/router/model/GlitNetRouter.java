package org.smartrplace.router.model;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;

public interface GlitNetRouter extends PhysicalElement {

    @Override
	StringResource name();
    
    /** @return hostname as reported by ubus system board. */
    StringResource hostname();
    
    /** @return system release as reported by ubus system board. */
    StringResource releaseDescription();
    
    /**
     * The system uptime as reported by ubus system info.
     * @return system uptime in seconds
     */
    TimeResource uptime();
	
	/** Number of IPv4 addresses found*/
	IntegerResource numberIP4AddressesHM();
	IntegerResource numberIP4AddressesSSH();
	
	/** Number of non-IPv4 addresses found*/
	IntegerResource numberIP6PlusAddressesHM();
	IntegerResource numberIP6PlusAddressesSSH();
	
	/** StringResources containing an IP address that shall be equal to the IPv4 address of the router*/
	ResourceList<StringResource> ipv4ViaMdnsTargets();
	/** When an update occurs the number of StringResources changed shall be written here for logging and alarming*/
	IntegerResource ipv4viaMndsAddressUpdates();
	/** The last address processed is stored here to check when real changes occur*/
	StringResource ipv4lastAddress();
}
