package org.ogema.model.recplay.testing;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.array.TimeArrayResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.model.extended.alarming.AlarmConfiguration;
import org.ogema.model.extended.alarming.AlarmGroupData;

/** Also alarming group evaluation is done based on the resources created. Here we hold paths directly to the
 * supervised resources, but these are not {@link SingleValueResource}s, but complex resources
 * */
public interface RecReplayAlarmingGroupData extends RecReplayObserverData {
	/** The list shall contain an entry for each group generated. In contrast to the base alarm supervision this list
	 * does not contain a list of resources supervised, but a new entry for each alarm group created. For this reason the
	 * creation time must be recorded separatly.
	 */
	ResourceList<AlarmGroupData> alarms();
	
	TimeArrayResource creationTimes();
	
	/** These are the paths to the relevant {@link AlarmConfiguration} resources, not the IntegerResources attached
	 * to the sensors. The alarming resources can be found via {@link AlarmConfiguration#sensorVal()} and
	 * AlarmingConfigUtil#getAlarmStatus.
	 */
	StringArrayResource alarmingConigPaths();
}
