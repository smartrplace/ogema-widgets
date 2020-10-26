package org.ogema.model.extended.alarming;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

public interface AlarmGroupData extends Data {
	/** User that accepted the alarm. May be a StringArrayResource in the future*/
	StringResource acceptedByUser();
	
	@Override
	/** ID of the AlarmOngoingGroup to which the data belongs. If no such group is available anymore
	 * the resource shall be deleted
	 */
	StringResource name();
	
	TimeResource ongoingAlarmStartTime();
	
	StringResource comment();
	
	/** Link to bug/task tracking tool where alarm is processed by support*/
	StringResource linkToTaskTracking();
}
