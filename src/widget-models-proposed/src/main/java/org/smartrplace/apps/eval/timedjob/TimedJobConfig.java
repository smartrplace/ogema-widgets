package org.smartrplace.apps.eval.timedjob;

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** Simplified version aof RepeatingOperationConfiguration suitable for direct GUI-based configuration*/
public interface TimedJobConfig extends Data {
	/** ID of the timed job provider*/
	@Override
	StringResource name();
	
	/** The persistent index is used for start logging*/
	IntegerResource persistentIndex();
	
	/** See AbsoluteTiming for values. If positive and active then the job is executed at each aligned interval plus the
	 * the time defined by interval()*/
	IntegerResource alignedInterval();
	
	/** If alignedInterval is inactive or zero/negative then the job is executed after each interval without alignment
	 * provided in MINUTES. If the value is shorter than MINIMUM_MINUTES_FOR_TIMER_START then timer cannot be activated,
	 * but call on startup will be executed if configured.*/
	FloatResource interval();
	
	/** If active and zero or positive the operation shall be executed once after startup with a delay of milliseconds
	 * indicated by this element. The delay can be used to give other components time to start up.<br>
	 * Provided in MINUTES.
	 */
	FloatResource performOperationOnStartUpWithDelay();
	
	/** For absolute timer if aligned execution is activated*/
	TimeResource lastStartStorage();
	
	/**The configuration may be temporarily disabled to avoid further operations*/
	BooleanResource disable();
	
	@NonPersistent
	/** Write here to trigger a start a TimedJob with a delay below 1000ms. A listener will then trigger the job*/
	IntegerResource triggerTimedJobStartsWithoutDelay();
	
	/** Store recalc times done and potentially more*/
	StringResource initDoneStatus();
}
