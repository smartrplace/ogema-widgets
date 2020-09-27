package de.iwes.ogema.bacnet.models;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Data;

/**
 *
 * @author jlapp
 */
public interface ObjectIdentifier extends Data {
    
    IntegerResource type();
    
    IntegerResource instanceNumber();
    
}
