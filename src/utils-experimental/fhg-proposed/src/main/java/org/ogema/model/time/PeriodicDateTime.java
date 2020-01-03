package org.ogema.model.time;

import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/**
 * A (quasi-)periodic set of dates, whose periodicity can be specified e.g. in terms of years, months, days,
 * hours, minutes, seconds or milliseconds {@see Period}. The set can be infinite or finite,
 * in the latter case {@link #end()} determines the end time. This interface can be used to describe recurrent events,
 * taking place for instance every other Saturday at 11:00h local time.<br>
 * 
 * If periodicity is specified in terms
 * of hours, minutes, seconds or milliseconds, then the intervals between adjacent timestamps actually all have the same length.
 * If it is specified in years, months, weeks, or days then this is not exactly the case, since for instance
 * the length of a month depends on the number of days it has, and the length of a day depends on whether
 * it is the first or last day of daylight savings time, or neither of both. <br>
 * 
 * The periodicity is specified by the {@link #period()} subresource, and an optional offset can be defined by {@link #offset()}. 
 * Usage examples:
 *  <ul>
 *    <li>An event that occurs every three hours at quarter past the full hour is modeled as: period = 3h, offset = 15min
 *    <li>An event that occurs every day at 12:00h is modeled as: period = 1d, offset = 12h
 *    <li>An event that occurs every second day at 11:30h is modeled as: period = 2d, offset = 11h + 30min
 *    <li>An event that takes place every other Saturday at 11:00h is modeled as: period = 2w, offset = 6d + 11h
 *  </ul>
 *  Start and end time can be specified optionally by the {@link #start()} and {@link #end()} resources.
 */
public interface PeriodicDateTime extends Data {

	/**
	 * Start time for the periodic date. The first event will not take place at this timestamp,
	 * but rather at the beginning of the next configured period unit (@see {@link #period()}; 
	 * for instance, if period is specified in days, 
	 * this would be at 00:00h of the day following the one defined by {@link #start()}, 
	 * if period is specified in months, it would be at the beginning of the next month), plus 
	 * the {@link #offset()}, if any. <br>
	 *
	 * If this is inactive at the first start, the current framework time may be assumed
	 * as start time (if no start time is specified and the periodicity is a multiple of the base unit, e.g. 2 weeks, then 
	 * it is not clear whether the event shall take place this week or the next week, hence a base must be defined). 
	 * It is recommended to write the assumed start time stamp to the 
	 * resource and activate it, in this case.
	 * @return 
	 */
	TimeResource start();

	/**
	 * If active, only a finite number of events is modeled. 
	 * @return
	 */
	TimeResource end();
	
	/**
	 * Determines the periodicity of the dates. E.g. if an event occurs every two days,
	 * the {@link Period#days()} value will be 2. Note that only one of the 
	 * optional subresources of period may be active for this to be well-defined.
	 * @return
	 */
	Period period();
	
	/**
	 * Offset for the periodic event with respect to the period unit. For instance, if {@link #period()} is 2 weeks,
	 * and the offset is 3 days and 11 hours, then the event will take place every second Wednesday at 11:00h.
	 * @return
	 */
	Period offset(); 
	
	/**
	 * Time zone identifier. See (TODO find suitable link...)
	 * If this is not specified (inactive), local time zone is assumed.
	 * <br>
	 * Note: The time zone id contains the information whether and when to use daylight savings time
	 * @return
	 */
	StringResource timeZone();
	
}
