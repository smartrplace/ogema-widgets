package de.iwes.ogema.bacnet.models;

import org.ogema.model.prototypes.Data;

/**
 *
 * @author jlapp
 */
public interface AddressBinding extends Data {
    
    ObjectIdentifier deviceObjectIdentifier();
    
    Address deviceAddress();
    
}
