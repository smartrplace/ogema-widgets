package org.smartrplace.router.model;

import org.ogema.core.model.ModelModifiers;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;
import org.ogema.model.sensors.GenericFloatSensor;

/**
 *
 * @author jlapp
 */
public interface WifiInfo extends Data {
    
    StringResource ssid();
    
    StringResource bssid();
    
    IntegerResource channel();
    
    @ModelModifiers.NonPersistent
    IntegerResource bitrate();
    
    /**
     * @return Wifi signal RSSI.
     */
    GenericFloatSensor signal();
    
}
