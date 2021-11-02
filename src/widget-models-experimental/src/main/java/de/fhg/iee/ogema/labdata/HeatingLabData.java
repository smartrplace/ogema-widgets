package de.fhg.iee.ogema.labdata;

import org.ogema.core.model.ResourceList;
import org.ogema.model.connections.ThermalConnection;
import org.ogema.model.devices.connectiondevices.ThermalValve;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.sensors.FlowSensor;
import org.ogema.model.sensors.TemperatureSensor;

public interface HeatingLabData extends PhysicalElement {
	/** put MR1_LT, MR2_LT, MR3_LT here*/
	ResourceList<TemperatureSensor> airTemperature();
	
	/** Put HK_VL in ThermalValve.connection.inputTemperature and
	 * HK_RL in outputTemperature for each radiator
	 */
	ResourceList<ThermalValve> radiators();
	
	/** Put MR1_V, MR2_V, MR3_V here*/
	ResourceList<FlowSensor> heatFlow();
	
	TemperatureSensor outSideTemperature();
	
	/** Put G_VL, G_RL here*/
	ThermalConnection mainConnection();
}
