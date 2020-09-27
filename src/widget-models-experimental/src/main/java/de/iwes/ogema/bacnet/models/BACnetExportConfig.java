package de.iwes.ogema.bacnet.models;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;

/**
 *
 * @author jlapp
 */
public interface BACnetExportConfig extends Configuration {
    
    BACnetTransportConfig transportConfig();
    
    ObjectIdentifier objectIdentifier();
    
    /**
     * (optional) Name of the exported object, if the OGEMA path or location should not be used.
     * @return BACnet object name.
     */
    StringResource objectName();
    
    /**
     * (optional) Resource to export, unless the configuration resource is used
     * as a decorator on the exported resource.
     * @return Resource to export.
     */
    Resource target();
    
    StringResource description();
    
    StringResource statusFlags();
    
    IntegerResource eventState();
    
    BooleanResource outOfService();
    
}
