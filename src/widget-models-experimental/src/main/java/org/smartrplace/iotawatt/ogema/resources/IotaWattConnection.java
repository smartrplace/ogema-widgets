package org.smartrplace.iotawatt.ogema.resources;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.GenericFloatSensor;

/** This configuration just writes the 
 *
 * @author jlapp
 */
public interface IotaWattConnection extends PhysicalElement {
    
    StringResource uri();
    
    /** determines the interval on which the average is calculated and written into the energy and power
     * destination resources*/
    StringResource group();
    
    StringArrayResource series();
    
    /** interval after which a new group of values is requested and the average is written into the destination resource*/
    StringResource updateInterval();
    
    ResourceList<GenericFloatSensor> sensors();
    
}
