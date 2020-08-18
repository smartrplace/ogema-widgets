package org.ogema.model.extended.alarming;

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.model.prototypes.Data;

public interface AlarmConfiguration extends Data {

	/** TODO: Change this to SingleValueResource in the future! Currently no alarming is possible e.g.
	 * for input from drivers not providing as sensors like JMBUS
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
	
	/** If the all is restarted usually all alarms are resent if the current value is still
	 * outside the limits. After sending/writing the alarm once
	 * the system shall wait for the duration specified here before sending/writing the alarm
	 * again if the value is outside the limits. If the alarmStatus is reset manually and the
	 * alarms occurs again then another alarm writing shall occur immediately.
	 * Given in minutes.
	 */
	FloatResource alarmRepetitionTime();
	
	/** If an alarm is detected and the alarmStatus resource is active then the value given
	 * here is written into the alarmStatus resource.<br>
	 * For no-value alarms the level plus 1000 is used.
	 */
	IntegerResource alarmLevel();
	
	/** Maximum time between new values (minutes)*/
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
}
