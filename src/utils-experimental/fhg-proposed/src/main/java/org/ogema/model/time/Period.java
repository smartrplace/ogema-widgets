package org.ogema.model.time;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Data;

/**
 * This class models a quantity or amount of time in terms of e.g. years, months, weeks, days, hours, minutes, seconds and milliseconds, 
 * or quarter of hours, 10 minute intervals, etc.<br>
 * A period should be thought of as an "aligned time interval". In conjunction with a time zone specification, a period type, such as a year,
 * divides the time line into intervals of roughly equal length, with a new interval starting on the first of January every year for this example. 
 * <br>
 * The actual duration of a period defined in terms of years, months, weeks or days is variable, depending on
 * the current local time and time zone. For example, the duration of one day depends on whether it is the first or last
 * day of daylight savings time, or neither of both, cf. the Java 8 Period class: 
 * <a href="https://docs.oracle.com/javase/tutorial/datetime/iso/period.html">https://docs.oracle.com/javase/tutorial/datetime/iso/period.html</a><br>
 * 
 * Usage examples:
 * <ul>
 *   <li>A period of one day is modeled in terms of a single active subresource {@link #days()}, with value 1.
 *   <li>A period of 24 hours is modeled by the {@link #hours()}-subresource, with value 24. Whereas a one day period always starts at 00:00h
 *   	at night, a 24h period can start at every full hour. If a 24h period shall be modeled that can start at an abitrary full minute instead,
 *   	this can be accomplished by using a single subresource {@link #minutes()} with value = 24 * 60. Since the number of hours in a day is 
 *   	variable (due to daylight saving time), the duration of 24h period need not be the same as that of a one day period, either. On the other hand,
 *      since every hour has exactly 60 minutes, the duration of a 1h period is the same as that of a 60 minutes period.
 *   <li>The model can also be used to represent time offsets, in which case it makes sense to use more than one subresource. For instance,
 *      an event that takes place every second Saturday at 11am could be modeled by a period (weeks = 2) and an offset (days = 6 & hours = 11). 
 *      See {@link PeriodicDateTime}.
 * </ul>
 * 
 * In order to specify a period of 2 days, create and activate only the {@link #days()} subresource, 
 * and leave the other ones virtual and inactive.
 * Only positive values are admissible, applications that encounter zero or negative values in an active subresource
 * are encouraged to consider the period resource as a whole as invalid, and should not try to deduce a value nevertheless.
 * <br>
 * The resource type may be extended through custom decorators, if other periods than the ones declared in this interface are needed. 
 * The general naming convention of the subresources should be followed. For instance:
 * <ul>
 *   <li>10 second intervals: subresource <tt>tenSeconds()</tt>
 *   <li>half minute intervals: subresource <tt>halfMinutes()</tt>
 *   <li>12 minute intervals: subresource <tt>twelveMinutes()</tt>
 *   <li>20 minute intervals: subresource <tt>twentyMinutes()</tt>
 * </ul>
 * Note that only intervals that divide the next higher basic period unit are sensible. Since 7 minutes do not divide an hour,
 * it does not make sense to define a period type <tt>sevenMinutes()</tt>.
 */
public interface Period extends Data {

	IntegerResource years();
	IntegerResource months();
	IntegerResource weeks();
	IntegerResource days();
	IntegerResource hours();
	IntegerResource minutes();
	IntegerResource seconds();
	IntegerResource millis();

	/*
	 * Special aligned intervals
	 */
	/**
	 * For modeling timestamps in a basis of a quarter of a year.
	 * If only the absolute duration is relevant, it is recommended to use {@link #months()} instead, and 
	 * set its value appropriately. If the alignment of the timestamps relative to the full year is important, 
	 * however, i.e. the four sets (Jan - Mar, Apr - Jun, Jul - Sep, Oct - Dec) shall be modeled, this resource should be prefered.
	 * @return
	 */
	IntegerResource quarterYears();

	IntegerResource fiveMinutes();
	IntegerResource tenMinutes();
	IntegerResource quarterHours();
	IntegerResource halfHours();
	
}
