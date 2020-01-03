package org.ogema.model.time;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.timeseries.InterpolationMode;

/**
 * A (quasi-)periodic schedule that contains only one data point per period. New values are always added at the 
 * first timestamp of the period their specified timestamp belongs to.   
 * Adding a new value for a period whose value is already defined, will replace the existing value.
 */
public interface CalendarBasedTimeSeries extends Schedule {
	
	/**
	 * Determines the admissible times for points in this schedule. 
	 * @return
	 */
	PeriodicDateTime periodicDateTime();

	/**
	 * Get the value for the period associated to <tt>time</tt>. If none is defined, but <tt>time</tt> 
	 * lies within the range of this schedule, specified by {@link #start()} and {@link #end()},
	 * then a bad quality value is returned. If <tt>time</tt> is outside the schedule range, null
	 * is returned.<br>
	 * Note that this is a draft data model and it has to be discussed whether this behaviour shall be
	 * implemented by all using applications or whether a framework-extension should be provided ot
	 * ensure this behaviour which is a bit different to the overwritten method
	 * {@link {Schedule#getValue(long)}
	 * 
	 * @param time
	 * @return 
	 */
	@Override
	SampledValue getValue(long time);
	
	/**
	 * @return
	 * 		{@link InterpolationMode#STEPS}
	 */
	@Override
	InterpolationMode getInterpolationMode();
	

}
