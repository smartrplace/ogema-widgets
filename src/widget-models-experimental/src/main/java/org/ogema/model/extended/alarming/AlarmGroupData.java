package org.ogema.model.extended.alarming;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.Data;

/** If the resource exists then an ongoing alarm is active*/
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
	
	BooleanResource isFinished();
	
	/** If negative then no new messages shall be sent. If positive then the default value for the alarm is overwritten during the
	 * known fault state. Default is -1, so default is blocking. If zero the value set for the individual datapoint is used.
	 */
	FloatResource minimumTimeBetweenAlarms();
	
	/** Reference to room for room faults*/
	Room room();
}
