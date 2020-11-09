package de.fhg.iee.ogema.modbus.server.resources;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;

public interface PublishingDataElement extends Configuration {
	   IntegerResource address();

	    SingleValueResource target();
	    
	    /**
	     * @return create as coil (read/write), default is {@code false} (discrete input)
	     */
	    BooleanResource writable();

	    /**If present then the target shall also be published via Bacnet.
	     * The object type shall be chosen based on the type of {@link #target()}<br>
	     * he object instance numbers can range from 0 to 4194302 .
	     * Note that this value must be unique among all resources of type {@link UnitConfig}
	     * on the system.*/
	    IntegerResource bacNetObjectInstanceNumber();
	    
	    /** Description to be used for bacnet property*/
	    StringResource description();
}
