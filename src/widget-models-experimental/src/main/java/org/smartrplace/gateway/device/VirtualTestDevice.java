package org.smartrplace.gateway.device;

import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.GenericFloatSensor;

/** VirtualDevice for testing purposes*/
public interface VirtualTestDevice extends PhysicalElement {
	/** Standard test sensor e.g. for alarming _SF*/
	GenericFloatSensor sensor_SF();
	GenericFloatSensor sensor_CF();
	GenericFloatSensor sensor_BT();
}
