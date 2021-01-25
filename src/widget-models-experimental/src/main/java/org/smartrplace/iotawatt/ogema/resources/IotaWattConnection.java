package org.smartrplace.iotawatt.ogema.resources;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;
import org.ogema.model.sensors.GenericFloatSensor;

/**
 *
 * @author jlapp
 */
public interface IotaWattConnection extends Configuration {
    
    StringResource uri();
    
    StringResource group();
    
    StringArrayResource series();
    
    StringResource updateInterval();
    
    ResourceList<GenericFloatSensor> sensors();
    
}
