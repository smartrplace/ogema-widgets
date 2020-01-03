package org.ogema.model.gateway.incident;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;
	
/** Definition when an incident shall be cleared/deleted automatically
*/
public interface IncidentAutoClearanceConfiguration extends Data {
	@Override
	/**Name to be displyed, should be short*/
	StringResource name();
	/**Automated clearance (status 11) is performed after the interval specified here.
	 * For repeating incidents no more occurences have to be occured for the interval given here, for
	 * others the system just waits the interval after the incident end.<br>
	 *  0: mark as cleared on generation (for incidents that shall just be logged, but not further processed)<br>
	 * -1: no time-based clearing 
	 */
	TimeResource clearAfterOccurence();
	/**Only relevant for repeating events. After the interval specified here an incident is set to
	 * continious clearance (status 10) if no action is performed. Note that this occurs even if
	 * more events come in. If also {@link #clearAfterOccurence()} is specified the incident status
	 * will be switched to 11 if no more events come in for the time specified.
	 *  0: mark as cleared on generation (for incidents that shall just be logged, but not further processed)<br>
	 * -1: no time-based clearing 
	 */
	TimeResource switchToContiniousClearance();
	/**Duration to keep the event after clearance (Status 11)*/
	TimeResource deleteAfterClearance();
}