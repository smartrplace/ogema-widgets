package org.smartrplace.external.accessadmin.config;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

public interface OnsiteVisitData extends Data {
	/** Planned date (if in future) or real date */
	TimeResource date();
	
	/** Information, which parts of the building / site are visited etc.*/
	StringResource remarks();
	
	/** Link to task in task management system etc. */
	StringResource link();
	
	/** If active then automated decalcification shall be blocked
	 * if no very urgent valve errors are found*/
	FloatResource blockAutoDecalcForDays();
	
	/** If active then all thermostats shall perform decalcification the number of days
	 * given here before the event. This will only take effect if the event
	 * is in the future for more than the number of days given. This is also only relevant
	 * if decalcification is not blocked entirely normally.
	 */
	FloatResource performDecalcOfAllThermostatsDaysBeforeEvent();
}
