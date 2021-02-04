package org.smartrplace.monitoring.vnstat.resources;

import org.ogema.model.prototypes.Data;
import org.ogema.model.sensors.GenericFloatSensor;

/**
 * Traffic volume on the network interface identified by {@link Data#name() }.
 * @author jlapp
 */
public interface NetworkTrafficData extends Data {
    
    GenericFloatSensor monthlyRxKiB();
    GenericFloatSensor monthlyTxKiB();
    GenericFloatSensor monthlyTotalKiB();
    
}
