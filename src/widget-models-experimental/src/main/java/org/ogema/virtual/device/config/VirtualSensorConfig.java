package org.ogema.virtual.device.config;

import org.ogema.model.prototypes.Data;
import org.ogema.model.prototypes.PhysicalElement;

public interface VirtualSensorConfig extends Data {
	/** Available for sensors replicating one sensor, usually into another room. Reference to the
	 * source sensor, which also gives access to the source room
	 */
	PhysicalElement sourceSensor();
}
