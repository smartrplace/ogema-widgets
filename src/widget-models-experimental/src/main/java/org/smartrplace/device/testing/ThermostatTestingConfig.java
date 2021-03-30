package org.smartrplace.device.testing;

import org.ogema.core.model.array.FloatArrayResource;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

public interface ThermostatTestingConfig extends Data {
	/** If positive then after each interval the setpoint temperature of all thermostats is switched by 0.5K down
	 * and the value is revered to negative. If  negative then after fixed 10 minutes (or the interval if shorter) all thermostats are
	 * switched up by 0.5K and the value is reversed back to positive. If zero then no switching is performed.
	 * @return
	 */
	TimeResource testSwitchingInterval();
	/** Setpoint set by switching. If the setpoint is not found anymore after testSwitching then no switching back is performed
	 * as the thermostat most likely was set to a new setpoint by an application
	 */
	StringArrayResource testSwitchingLocation();
	FloatArrayResource testSwitchingSetpoint();

}
