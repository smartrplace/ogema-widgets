package de.smartrplace.ble.resources;

import org.ogema.core.model.ModelModifiers;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.GenericFloatSensor;

/**
 *
 * @author jlapp
 */
public interface BeaconInformation extends PhysicalElement {
    
    /**
     * String identifier for the beacon, structure will depend on the concrete
     * beacon type (iBeacons are for example identified by a UUID + major and
     * minor numbers).
     * 
     * @return the beacon identifier.
     */
    StringResource id();
    
    /**
     * @return beacon address (MAC)
     */
    StringResource address();
    
    /**
     * @return beacon signal strength.
     */
    GenericFloatSensor rssiSensor();
    
    /**
     * @return time of last beacon.
     */
    @ModelModifiers.NonPersistent
    TimeResource lastBeacon();
    
}
