package org.ogema.model.action.spextended;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.action.Action;
import org.ogema.model.alignedinterval.AlignedTimeIntervalLength;

/**Model containing information on actions/programs run in the past and configuration for storage
 * of such information
 * @author dnestle
 */
public interface ProgramHistoryManagement {
	BooleanResource storeHistory();
	ResourceList<ActionExecuted> history();
	
	/**Start this action to update history list*/
	Action update();
	
	/**0: no automated house keeping, just keep entire history
	 * 1: keep everything up to a certain time distance, delete all older entries
	 * 2: throw away more entries the older they are, but keep also some quite old entries
	 */
	IntegerResource houseKeepingMode();
	/**Remove all entries that are further in the past than the time span specified here*/
	TimeResource clearAllHorizon();
	/**Only relevant for houseKeepingMode = 2: 20% of entries that are in the past according to this
	 * time span shall be saved, others shall be deleted. This is only a rough figure, if the
	 * implementation works step-based and keeps one value per week the time span specified here
	 * shall apply to this stage of housekeeping
	 */
	TimeResource keepTwentyPercent();
	/**One value per aligned interval shall be kept regardless of other housekeeping settings.
	 * This can be used to keep one value per year or per month forever.
	 */
	AlignedTimeIntervalLength keepAlways();
}
