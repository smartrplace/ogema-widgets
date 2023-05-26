package org.ogema.model.extended.alarming;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.locations.Room;
import org.ogema.model.prototypes.Data;
import org.ogema.model.user.NaturalPerson;

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
	
	@Deprecated // always false
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
	
	/** If 1=releaseProposed then the known fault is marked for release. E.g. because a device marked for "not reacheable" is sending
	 * data again or a device that is off due to battery is sending data again and battery voltage indicates a
	 * battery change.<br>
	 * TODO: The following status unknown is not implemented yet: If the device is not sending data again then the forRelease status shall be set to 2=unknown.
	 * Note that the entire device is set to forRelease when new data is obtained and it is not checked if all missing data
	 * is returned. TODO: This may be the next step.
	 * 
	 *   - 1: releaseProposed
	 *   - 2: unknown (see above)
	 *   - 11: release proposed by installer
	 * */
	IntegerResource forRelease();
	
	/** Last email messages sent. Can be used for escalation.*/
	StringResource lastMessage();
	
	/**
	 * Replaced by #processingOrder()
	 * 0: lowest priority
	 * >0: higher priority
	 * @return
	 */
	@Deprecated
	IntegerResource priority();
	
	/**
	 * Typical values: 10, 20, 30, 40, ...
	 * @return
	 */
	FloatResource processingOrder();
	
	
	/** Escalation data could be stored per AlarmGroupData and provider or just per provider.
	 * We may not use this in the first step. This can be used directly by the provider, is not
	 * used by the framework.*/
	ResourceList<EscalationData> escalationData();
	
	/** Person/Email contact responsible for next step. This is the email address/name of the UserData. Note that we cannot
	 * set a reference here as this information is synchronized with superior.*/
	StringResource responsibility();
	
	/** When this time is reached then a reminder to the email in the assignment shall be sent*/
	TimeResource dueDateForResponsibility();

	/** Gateway-wide unique ID of all known issues ever occured.
	 * TODO: Make this integer?*/
	StringResource knownIssueId();
	
	/** If active and non-empty the issue shall be assigned "Dependent"
	 * *In this case the issue is released and analysed together with its parent.
	 * Note that we cannot set a reference here as this is synchronized with superior.*/
	StringResource knownIssueParent();
}
