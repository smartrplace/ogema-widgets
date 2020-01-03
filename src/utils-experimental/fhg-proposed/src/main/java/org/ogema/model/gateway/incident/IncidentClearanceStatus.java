package org.ogema.model.gateway.incident;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;
	
/** Status of incident
*/
public interface IncidentClearanceStatus extends Data {
	/** 0: unknown/not set<br>
	 *  1: not cleared / active<br>
	 *  2: re-activated<br>
	 *  5: postponed for clearance (towards status 11). Only will take effect if no
	 *  more events come in<br>
	 *  6: postponed for re-activation<br>
	 *  10: continious-cleared also for re-occuring events (not to be deleted)<br>
	 *  11: cleared and ready to be deleted<br>
	 *  20: marked for deletion
	 */
	IntegerResource status();
	/** For postponed the value defines when the status shall be switched to the follow-up status. For
	 * active / cleared the value provides the time of activation/clearance.<br>
	 */
	TimeResource statusTime();
	
	/**Only relevant if status indicated cleared: If the type is cleared, indicate true here. Both
	 * clearedType and clearedIndividually may be true if the incident was cleared on both levels. If 
	 * clearance was just done by time both shall be false.
	 */
	//BooleanResource clearedByType();
	//BooleanResource clearedIndividually();
	
	/**For repeating incidents that last occurence has to be expired by the interval given here, for
	 * others the system just waits the interval after the incident end.<br>
	 *  0: mark as cleared on generation (for incidents that shall just be logged, but not further processed)<br>
	 * -1: no time-based clearing 
	 */
	//TimeResource clearAfterOccurence();
	/**Duration to keep the event after clearance*/
	//TimeResource deleteAfterClearance();
}