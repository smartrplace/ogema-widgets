package org.smartrplace.model.energysavings;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.model.prototypes.Data;

public interface BuildingThermalParameters extends Data {
	TemperatureResource heatingLimitTemperature();
	/** The value represents the start of the nightly lowering in msec since start of day*/
	TimeResource nightlyLoweringStartFromDayStart();
	TimeResource nightlyLoweringEndFromDayStart();
	/**Lowering temperature difference in K or °C (which is the same for a delta temperature)*/
	FloatResource nightlyLoweringDeltaT();
	/** For weekend lowering each relevant day is lowered to the delta given by nightlyLoweringDelta.
	 * 0=no special weekend lowering, 1=only sunday/holidays, 2=saturday, sunday, holidays
	 */
	IntegerResource weekendLoweringMode();
}
