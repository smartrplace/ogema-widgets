package org.ogema.model.extended.alarming;

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

public interface AlarmConfiguration extends Data {

	/** Reference to the resource containing the values to be supervised by the alarming configuration.
	 * @return
	 */
	//Sensor supervisedSensor();
	SingleValueResource sensorVal();

	/** TODO: Implement this
	 * @return
	 */
	//Resource supervisedResource();
	
	FloatResource lowerLimit();
	FloatResource upperLimit();
	
	/** If the values are outside the limits specified not more than the interval time given here
	 * then no alarm will be generated (minutes)
	 */
	FloatResource maxViolationTimeWithoutAlarm();
	
	/** If the alarming logic is restarted usually all alarms are resent if the current value is still
	 * outside the limits. After sending/writing the alarm once
	 * the system shall wait for the duration specified here before sending/writing the alarm
	 * again if the value is outside the limits. If the alarmStatus is reset manually and the
	 * alarms occurs again then another alarm writing shall occur immediately.
	 * Given in minutes.
	 */
	FloatResource alarmRepetitionTime();
	
	/** If an alarm is detected and the alarmStatus resource is active then the value given
	 * here is written into the alarmStatus resource. For no-value alarms the level plus 1000 is used.<br>
	 * The alarm level is also used to determine the message priority:<br>
	 * 1: LOW<br>
	 * 2: MEDIUM<br>
	 * 3: HIGH
	 */
	IntegerResource alarmLevel();
	
	/** Maximum time between new values (minutes). A negative value indicates that no alarm shall be generated if no more values are received.*/
	FloatResource maxIntervalBetweenNewValues();
	
	/** If false or not set the alarm will not send messages. If both sendAlarm and 
	 * {@link #performAdditinalOperations()} is false then the alarm listener is not
	 * activated*/
	BooleanResource sendAlarm();
	
	/** Only relevant if the respective sensor type has additional operations such as a
	 * supervision that switches are always on
	 */
	BooleanResource performAdditinalOperations();
	
	/** Ids of AlarmingExtensions that shall be applied to the resource. Currently a separate extensions
	 * alarming object is created for each governing SingleValueResource, in the future it might be necessary to be
	 * able to configure to call the resourceChanged method of a single object from the
	 * ResourceValueListeners of multiple SingleValueResources*/
	StringArrayResource alarmingExtensions();
	
	/** Alarming app by which the configuration is sent. This determines which receivers get high, medium and low 
	 * priority alarms. The settings of this configuration generate high priority alarms.
	 */
	StringResource alarmingAppId();
	/** It is possible to provide additional settings that generate a medium priority alarm. The value limits and/or
	 * the duration must be more relaxed so that medium priority messages are sent later and less frequently than
	 * high low messages. Note that all receivers of the low priority messages will also receive the
	 * medium priority messages. Note also that in this concept "low priority" means "send to first-level support"
	 * so in this concept high priority messages indicate that the maximum level of escalation is reached.<br>
	 * If this is not available then no mediumPriorityMessages are generated.<br>
	 * Note that {@link #alarmLevel()} must be set correctly for each configuration variant
	 */
	@Deprecated //Curently not implemented
	AlarmConfiguration mediumPriorityConfig();
	/** See {@link #mediumPriorityConfig()}
	 */
	@Deprecated //Curently not implemented
	AlarmConfiguration lowPriorityConfig();
	
	/** Set a reference to the alarming configuration below a DevelopmentTask if this shall be relevant for
	 * sending messages*
	 */
	//AlarmConfiguration developmentAlarmConfig();
}
