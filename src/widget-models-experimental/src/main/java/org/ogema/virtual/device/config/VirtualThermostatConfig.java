package org.ogema.virtual.device.config;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Data;

public interface VirtualThermostatConfig extends Data {
	//Reference to room temperature measurement
	//TemperatureSensor roomSensor();
	
	/** Reference to RoomTemperatureSetting#heatingCoolingMode() if relevant*/
	IntegerResource heatingCoolingMode();
	
	/** If non-zero then the state of the OnOffSwitches may be forced
	 * positve: force on
	 * negative: force off
	 */
	IntegerResource forceOnOff();
	
	/** State determined and set for all relevant switches in the room */
	BooleanResource roomStateControl();
}
