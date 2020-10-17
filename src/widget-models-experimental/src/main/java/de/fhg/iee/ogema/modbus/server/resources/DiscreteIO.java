package de.fhg.iee.ogema.modbus.server.resources;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;

/**
 * Adds a coil or discrete input to the unit
 *
 * @author jan.lapp@smartrplace.de
 */
public interface DiscreteIO extends PublishingDataElement {

    IntegerResource address();

    BooleanResource target();
    
    /**
     * @return create as coil (read/write), default is {@code false} (discrete input)
     */
    BooleanResource writable();
    
}
