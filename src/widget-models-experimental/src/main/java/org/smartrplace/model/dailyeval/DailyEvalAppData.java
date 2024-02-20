package org.smartrplace.model.dailyeval;

import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** Overall database structure of persistent data stored by the controller*/
public interface DailyEvalAppData extends Data {
	/** Last time controller was updated. The next update shall take place one day after
	 * the last update.*/
	TimeResource lastControllerUpdateTime();
}
