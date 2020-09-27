package de.iwes.ogema.bacnet.models;

import org.ogema.core.model.array.ByteArrayResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Data;

/**
 *
 * @author jlapp
 */
public interface Address extends Data {
    
    IntegerResource  networkNumber();
    
    ByteArrayResource macAddress();
    
}
