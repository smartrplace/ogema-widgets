package org.smartrplace.apps.hw.install.dpres;

import org.ogema.core.model.ResourceList;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.GenericFloatSensor;

public interface SensorDeviceDpRes extends PhysicalElement {
	ResourceList<GenericFloatSensor> sensors();
}
