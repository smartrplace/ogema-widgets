package de.iwes.ogema.bacnet.models;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;

/**
 *
 * @author jlapp
 */
public interface BACnetDevice extends BACnetSubdevice {
    
    ObjectIdentifier identifier();
    
    /**
     * @return the system status.
     */    
    IntegerResource systemStatus();
    
    StringResource vendorName();
    
    IntegerResource vendorIdentifier();
    
    StringResource modelName();
    
    StringResource firmwareRevision();
    
    StringResource applicationSoftwareVersion();
    
    IntegerResource protocolVersion();
    
    IntegerResource protocolRevision();
    
    StringResource protocolServicesSupported();
    
    StringResource protocolObjectTypesSupported();
    
    IntegerResource maxApduLengthAccepted();
    
    IntegerResource segmentationSupported();
    
    IntegerResource apduTimeout();
    
    IntegerResource numberOfApduRetries();
    
    ResourceList<AddressBinding> deviceAddressBinding();
    
    IntegerResource databaseRevision();
    
    ResourceList<Resource> objects();
    
}
