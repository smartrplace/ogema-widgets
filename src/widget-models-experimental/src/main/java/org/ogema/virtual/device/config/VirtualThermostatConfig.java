package org.ogema.virtual.device.config;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Data;

public interface VirtualThermostatConfig extends Data {
	//Reference to room temperature measurement
	//TemperatureSensor roomSensor();
	
	/** Reference to RoomTemperatureSetting#heatingCoolingMode() if relevant*/
	IntegerResource heatingCoolingMode();
}
