package org.ogema.model.recplay.testing;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.model.extended.alarming.AlarmConfiguration;

/** The base alarming oberserver can be considered a pre-template for a general {@link SingleValueResource}
 * observer. This observer does not use {@link ResourceValueListener}s, though, but gets the information of
 * value changes directly from the application. And the resource alarmingConfigPaths does not store the
 * path of the {@link SingleValueResource} directly, but the path to the relevant configuration resource as
 * we get to the ValueResource from there easily, but not the other way round.
 * */
public interface RecReplayAlarmingBaseData extends RecReplayObserverData {
	/** The list shall contain an IntegerResource for each resource of type {@link AlarmConfiguration}
	 * in the system that is or was recorded. It shall contain the initial alarmingStatus value and shall
	 * contain the AbsoluteSchedule {@link IntegerResource#program()}. The schedule contains an entry for each
	 * value change of the resource attached to the relevant sensor.
	 */
	ResourceList<IntegerResource> alarms();
	
	/** These are the paths to the relevant {@link AlarmConfiguration} resources, not the IntegerResources attached
	 * to the sensors. The alarming resources can be found via {@link AlarmConfiguration#sensorVal()} and
	 * AlarmingConfigUtil#getAlarmStatus.
	 */
	StringArrayResource alarmingConigPaths();
}
