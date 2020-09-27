package de.iwes.ogema.bacnet.models;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;

/**
 * A configuration resource for a BACnet/IP transport.
 * @author jlapp
 */
public interface BACnetTransportConfig extends Configuration {
    
    BACnetDevice device();
    
    /**
     * The base IP of the OGEMA machine. Use either this value or {@link #networkInterface() },
     * which will also work in case local IP address changes.
     * @return Base IP address of the BACnet server (e.g. 192.168.0.10)
     */
    StringResource baseUrl();
    
    StringResource networkInterface();
    
    IntegerResource port();
    
    ResourceList<BACnetDevice> remoteDevices();
    
}
