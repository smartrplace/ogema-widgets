package org.ogema.model.recplay.testing;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** Each observer collects recreplay data for several input resources such as OGEMA database resources,
 * other database elements, alarms, HTTP message communication etc.<br>
 * Each resourceof type {@link RecReplayData} is designed to hold the data for one input configuration as
 * each started OGEMA instance tests exactly one input configuration. If this shall be changed in the
 * future then the OGEMA system would need to process several resources of type {@link RecReplayData}.
 */
public interface RecReplayObserverData extends Data {
	
	@Override
	/** ObserverID as we do not need a human readable name here*/
	StringResource name();
	
	/** TODO: NOT USED ANYMORE 
	 * Overwrite this with a more specific element type for each observer
	 * Note: This does work like this. Each observer must contain this list, though*/
	//ResourceList<Data> recordedElements();
	
	/** For checking and potentail adjustment the real observerStartTime is recorded and the deviation is
	 * checked on startup
	 */
	TimeResource observerStartTime();
	
	/** The following elements are only created and used in replay-testing mode, not in
	 * recording mode. More details of the testing results may be reported via a special service
	 * in the future, but for now we provide resources for this.
	 */
	ResourceList<RecReplayDeviation> deviations();
	TimeResource replayStartTimeDeviation();
	
	IntegerResource numberOfAlarmsProcessed();
	IntegerResource numberOfElementsFinished();
	IntegerResource numberOfSuccess();
	TimeResource averageTimeDeviation();
	TimeResource maximumTimeDeviation();
}
