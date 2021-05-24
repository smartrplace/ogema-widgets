package org.ogema.model.extended.alarming;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.Data;

/** If the resource exists then an ongoing alarm is active*/
public interface AlarmGroupData extends Data {
	//public final static String[] USER_ROLES = {"Other", "Operation", "Development", "Customer", "Backlog"};
	
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
	
	/** User that accepted the alarm. May be a StringArrayResource in the future*/
	@Deprecated
	StringResource acceptedByUser();
	
	/** The following types are currently defined:<br>
	 * 0: not set
	 * 1: requires more analysis
	 * 10: no contact: Device not on site, out of radio signal or battery empty
	 * 11: no contact: Device not on site
	 * 12: no contact: Device out of radio signal
	 * 13: no contact: Battery empty
	 * 20: insufficient signal strength: requires additional repeater, controller or HAP
	 * 21: insufficient signal strength: wrong controller association
	 * 22: insufficient signal strength: other reason
	 * 30: Thermostat is not properly installed (valve / adaption error)
	 * 40: Thermostat requires wall thermostat or other room temperature measurement (own temperature measurement not sufficient)
	 * 50: Battery low
	 */
	//@Deprecated // currently not used
	IntegerResource diagnosis();
	
	/** See AlarmingConfigUtil#ASSIGNEMENT_ROLES*/
	IntegerResource assigned();
}
