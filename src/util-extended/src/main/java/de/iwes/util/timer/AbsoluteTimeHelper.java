/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.timer;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.model.alignedinterval.AlignedTimeIntervalLength;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.alignedinterval.TimeIntervalLength;

public class AbsoluteTimeHelper {
	public static long STANDARD_YEAR_DURATION = 365*24*3600000l + 6*3600000l;
	/**Get standard interval length of type according to {@link TimeIntervalLength} / {@link AbsoluteTiming}
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return interval length of a 'normal' interval (e.g. no leap year)*/
	public static long getStandardInterval(int timeIntervalLengthType) {
		switch(timeIntervalLengthType) {
			case 1:
				return STANDARD_YEAR_DURATION;
			case 2:
				return STANDARD_YEAR_DURATION/4;
			case 3:
				return STANDARD_YEAR_DURATION/12;
			case 6:
				return 7*24*3600000l;
			case 10:
				return 24*3600000l;
			case 15:
				return 4*3600000l;
			case 100:
				return 3600000l;
			case 101:
				return 60000l;
			case 102:
				return 1000l;
			case 200:
				return 30*60000l;
			case 220:
				return 15*60000l;
			case 240:
				return 10*60000l;
			case 320:
				return 5*60000l;
			case 1000:
				return 30000l;
			case 1020:
				return 10000l;
			//TODO add more options
			default:
				return -1;
		}
	}
	
	public static int getIntervalTypeFromStandardInterval(long standardInterval) {
		if(standardInterval <= 1000) return 102;
		if(standardInterval <= 10000l) return 1020;
		if(standardInterval <= 30000l) return 1000;
		if(standardInterval <= 60000l) return 101;
		if(standardInterval <= 5*60000l) return 320;
		if(standardInterval <= 10*60000l) return 240;
		if(standardInterval <= 15*60000l) return 220;
		if(standardInterval <= 30*60000l) return 200;
		if(standardInterval <= 4*3600000l) return 15;
		if(standardInterval <= 24*3600000l) return 10;
		if(standardInterval <= 7*24*3600000l) return 6;
		if(standardInterval <= STANDARD_YEAR_DURATION/12) return 3;
		if(standardInterval <= STANDARD_YEAR_DURATION/4) return 2;
		if(standardInterval <= STANDARD_YEAR_DURATION) return 1;
		return -1;
	}
	
	/** Init AlignedTimeIntervalLength resource
	 * 
	 * @param intervalInfo resource to be initialized
	 * @param useLocal if true the alignment will be set to the local time zone and to reflect daylight savings time.
	 * 			This is only done if the statAggregation does not provide this information yet
	 * @param statAggregation if null only timeZone will be set
	 * @return
	 */
	public static boolean initAlignedInterval(AlignedTimeIntervalLength intervalInfo, 
			boolean useLocal, StatisticalAggregation statAggregation) {
		if(statAggregation != null) {
			updateMinimalInterval(statAggregation);
		}
		String timeZone;
		if(intervalInfo.timeZone().exists()) {
			timeZone = intervalInfo.timeZone().getValue();
		} else {
			if(useLocal) {
				//TODO: Check whether we really get the information on DST here
				timeZone = DateTimeZone.getDefault().getID();
			} else {
				timeZone = "UTC";
			}
		}
		intervalInfo.timeZone().create();
		intervalInfo.timeZone().setValue(timeZone);
		intervalInfo.activate(true);
		return true;
	}
	
	/** Get minimal existing interval in StatisticalAggregation
	 * @param statAggregation
	 * @return type of shortest time interval existing
	 */
	public static int getMinimalInterval(StatisticalAggregation statAggregation) {
		int type = -1;
		if(statAggregation.secondValue().exists()) {
			type = 102;
		} else if(statAggregation.tenSecondValue().exists()) {
			type = 1020;
		} else if(statAggregation.halfMinuteValue().exists()) {
			type = 1000;
		} else if(statAggregation.minuteValue().exists()) {
			type = 101;
		} else if(statAggregation.fiveMinuteValue().exists()) {
			type = 320;
		} else if(statAggregation.tenMinuteValue().exists()) {
			type = 240;
		} else if(statAggregation.fifteenMinuteValue().exists()) {
			type = 220;
		} else if(statAggregation.halfHourValue().exists()) {
			type = 200;
		} else if(statAggregation.hourValue().exists()) {
			type = 100;
		} else if(statAggregation.fourHourValue().exists()) {
			type = 15;
		} else if(statAggregation.dayValue().exists()) {
			type = 10;
		} else if(statAggregation.weekValue().exists()) {
			type = 6;
		} else if(statAggregation.monthValue().exists()) {
			type = 3;
		} else if(statAggregation.quarterYearValue().exists()) {
			type = 2;
		} else if(statAggregation.yearValue().exists()) {
			type = 1;
		}
		return type;
	}
	/**Based on result elements existing in statAggregation the field
	 * {@link statAggregation.minimalInterval().timeIntervalLength().type()} will be set.
	 * @param statAggregation
	 * @return type value to which minimalInterval was set
	 */
	public static int updateMinimalInterval(StatisticalAggregation statAggregation) {
		statAggregation.minimalInterval().timeIntervalLength().type().create();
		int type = getMinimalInterval(statAggregation);
		statAggregation.minimalInterval().timeIntervalLength().type().setValue(type);
		statAggregation.minimalInterval().timeIntervalLength().type().activate(false);
		return type;
	}
	
	private static MutableDateTime getCurrentDateTime(long baseInstant, AlignedTimeIntervalLength intervalInfo) {
		DateTimeZone dtz;
		try{
			dtz = DateTimeZone.forID(intervalInfo.timeZone().getValue());
		} catch(IllegalArgumentException e) {
			dtz = DateTimeZone.getDefault();
		}
		MutableDateTime utc = new MutableDateTime(baseInstant, dtz);
		//return utc.toMutableDateTime(dtz);		
		return utc;
	}
	public static MutableDateTime getCurrentDateTime(long baseInstant, String timeZone) {
		DateTimeZone dtz;
		try{
			dtz = DateTimeZone.forID(timeZone);
		} catch(IllegalArgumentException e) {
			dtz = DateTimeZone.getDefault();
		}
		MutableDateTime utc = new MutableDateTime(baseInstant, dtz);
		//return utc.toMutableDateTime(dtz);
		return utc;
	}
	
	public static long getUTCMillis(MutableDateTime current) {
		//MutableDateTime utc = current.toMutableDateTime(DateTimeZone.UTC);
		return current.getMillis();
	}

	/** Get aligned interval start
	 * @param baseInstant time that is in interval of which the start is returned
	 * @param intervalInfo alignment information (e.g. type of interval)
	 * @return interval start time
	 */
	public static long getIntervalStart(long baseInstant, AlignedTimeIntervalLength intervalInfo) {
		//TimeZone tz = TimeZone.getTimeZone(intervalInfo.timeZone().getValue());
		int timeIntervalLengthType = intervalInfo.timeIntervalLength().type().getValue();
		MutableDateTime current = getCurrentDateTime(baseInstant, intervalInfo);
		setToBeginningOfCurrentInterval(current, timeIntervalLengthType);
		return getUTCMillis(current);		
	}

	/** Get aligned interval end
	 * 
	 * @param baseInstant time that is in interval of which the end is returned
	 * @param intervalInfo alignment information (e.g. type of interval)
	 * @return interval end time (which is the same as the beginning of the next interval)
	 */
	public static long getNextStepTime(long baseInstant, AlignedTimeIntervalLength intervalInfo) {
		//TimeZone tz = TimeZone.getTimeZone(intervalInfo.timeZone().getValue());
		int timeIntervalLengthType = intervalInfo.timeIntervalLength().type().getValue();
		MutableDateTime current = getCurrentDateTime(baseInstant, intervalInfo);
//System.out.println("Next step Input time: "+current.getMinuteOfHour()+":"+current.getSecondOfMinute());
		setToBeginningOfCurrentInterval(current, timeIntervalLengthType);
		addSteps(current, 1, timeIntervalLengthType);
//System.out.println("Next step Return time: "+current.getMinuteOfHour()+":"+current.getSecondOfMinute());
		return getUTCMillis(current);
	}

	/** Get aligned interval end based on local default time zone
	 * 
	 * @param baseInstant time that is in interval of which the end is returned
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return interval end time (which is the same as the beginning of the next interval)
	 */
	public static long getNextStepTime(long baseInstant, int timeIntervalLengthType) {
		return getNextStepTime(baseInstant, DateTimeZone.getDefault().getID(), timeIntervalLengthType);
	}
	/** Get aligned interval end
	 * 
	 * @param baseInstant time that is in interval of which the end is returned
	 * @param timeZone that shall be used to determine beginning/end of intervals (e.g. days)
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return interval end time (which is the same as the beginning of the next interval)
	 */
	public static long getNextStepTime(long baseInstant, String timeZone, int timeIntervalLengthType) {
		MutableDateTime current = getCurrentDateTime(baseInstant, timeZone);
		setToBeginningOfCurrentInterval(current, timeIntervalLengthType);
		addSteps(current, 1, timeIntervalLengthType);
//System.out.println("Next step Return time: "+current.getMinuteOfHour()+":"+current.getSecondOfMinute());
		return getUTCMillis(current);
	}
	
	/** Get aligned interval start
	 * 
	 * @param baseInstant time that is in interval of which the start is returned
	 * @param timeZone that shall be used to determine beginning/end of intervals (e.g. days)
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return interval start time
	 */
	public static long getIntervalStart(long baseInstant, String timeZone, int timeIntervalLengthType) {
		MutableDateTime current = getCurrentDateTime(baseInstant, timeZone);
//System.out.println("Input time: "+current.getMinuteOfHour()+":"+current.getSecondOfMinute());
		setToBeginningOfCurrentInterval(current, timeIntervalLengthType);
//System.out.println("Return time: "+current.getMinuteOfHour()+":"+current.getSecondOfMinute());
		return getUTCMillis(current);
	}
	/** Get aligned interval start based on local default time zone
	 * 
	 * @param baseInstant time that is in interval of which the start is returned
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return interval start time
	 */
	public static long getIntervalStart(long baseInstant, int timeIntervalLengthType) {
		return getIntervalStart(baseInstant, DateTimeZone.getDefault().getID(), timeIntervalLengthType);
	}
	
	/** Get aligned interval duration. In contrast to {@link getStandardInterval} the real interval duration of
	 * a certain interval is determined, e.g. the duration of the current month
	 * 
	 * @param baseInstant time that is in interval of which the duration is returned
	 * @param timeZone that shall be used to determine beginning/end of intervals (e.g. days)
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return interval duration
	 */
	public static long getIntervalDuration(long baseInstant, String timeZone, int timeIntervalLengthType) {
		MutableDateTime current = getCurrentDateTime(baseInstant, timeZone);
		setToBeginningOfCurrentInterval(current, timeIntervalLengthType);
		long start = getUTCMillis(current);
		addSteps(current, 1, timeIntervalLengthType);
		return getUTCMillis(current)-start;
	}
	/** Get aligned interval duration based on local default time zone. In contrast to {@link getStandardInterval} the real interval duration of
	 * a certain interval is determined, e.g. the duration of the current month
	 * 
	 * @param baseInstant time that is in interval of which the duration is returned
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return interval duration
	 */
	public static long getIntervalDuration(long baseInstant, int timeIntervalLengthType) {
		return getIntervalDuration(baseInstant, DateTimeZone.getDefault().getID(), timeIntervalLengthType);
	}
	
	/** Step a number of intervals into future/past based on a given time
	 * 
	 * @param baseAlignedTime beginning of a an aligned interval. The result is undefined if this value is not
	 * 		aligned with the interval type
	 * @param stepsToAdd positive: step into future; negative: go back into past relative to baseAlignedTime
	 * @param timeZone that shall be used to determine beginning/end of intervals (e.g. days)
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return beginning of aligned interval calculated
	 */
	public static long addIntervalsFromAlignedTime(long baseAlignedTime, int stepsToAdd,
			String timeZone, int timeIntervalLengthType) {
		MutableDateTime current = getCurrentDateTime(baseAlignedTime, timeZone);
		addSteps(current, stepsToAdd, timeIntervalLengthType);
		return getUTCMillis(current);
	}
	/** Step a number of intervals into future/past based on a given time and based on the local default time zone
	 * 
	 * @param baseAlignedTime beginning of a an aligned interval. The result is undefined if this value is not
	 * 		aligned with the interval type
	 * @param stepsToAdd positive: step into future; negative: go back into past relative to baseAlignedTime
	 * @param timeIntervalLengthType use option provided by {@link AbsoluteTiming}, e.g. AbsoluteTiming.DAY
	 * @return beginning of aligned interval calculated
	 */
	public static long addIntervalsFromAlignedTime(long baseAlignedTime, int stepsToAdd, int timeIntervalLengthType) {
		return addIntervalsFromAlignedTime(baseAlignedTime, stepsToAdd, DateTimeZone.getDefault().getID(), timeIntervalLengthType);
	}
	
	public static void setToBeginningOfCurrentInterval(MutableDateTime current, int timeIntervalLengthType) {
		switch(timeIntervalLengthType) {
		case 1:
			current.setMonthOfYear(1);
		case 2:
			current.setMonthOfYear((current.getMonthOfYear()/3)*3+1);
		case 3:
			current.setDayOfMonth(1);
		case 6:
			if(timeIntervalLengthType==6) {
				current.setDayOfWeek(DateTimeConstants.MONDAY);
			}
		case 10:
			current.setHourOfDay(0);
		case 15:
			current.setHourOfDay((current.getHourOfDay()/4)*4);
		case 100:
			current.setMinuteOfHour(0);
		case 200:
			if(timeIntervalLengthType==200)
				current.setMinuteOfHour((current.getMinuteOfHour()/30)*30);
		case 220:
			if(timeIntervalLengthType==220)
				current.setMinuteOfHour((current.getMinuteOfHour()/15)*15);
		case 240:
			if(timeIntervalLengthType==240)
				current.setMinuteOfHour((current.getMinuteOfHour()/10)*10);
		case 320:
			if(timeIntervalLengthType==320)
				current.setMinuteOfHour((current.getMinuteOfHour()/5)*5);
		case 101:
			current.setSecondOfMinute(0);
		case 1000:
			current.setSecondOfMinute((current.getSecondOfMinute()/30)*30);
		case 1020:	
			current.setSecondOfMinute((current.getSecondOfMinute()/10)*10);
		case 102:
			current.setMillisOfSecond(0);
			break;
		default:
			throw new UnsupportedOperationException("interval type:"+timeIntervalLengthType+" not supported");
		}
		
	}
	public static void addSteps(MutableDateTime current, int steps, int timeIntervalLengthType) {
		switch(timeIntervalLengthType) {
		case 102:
			current.addSeconds(steps);
			break;
		case 1020:
			current.addSeconds(10*steps);
			break;
		case 1000:
			current.addSeconds(30*steps);
			break;
		case 101:
			current.addMinutes(steps);
			break;
		case 320:
			current.addMinutes(5*steps);
			break;
		case 240:
			current.addMinutes(10*steps);
			break;
		case 220:
			current.addMinutes(15*steps);
			break;
		case 200:
			current.addMinutes(30*steps);
			break;
		case 100:
			current.addHours(steps);
			break;
		case 15:
			current.addHours(4*steps);
			break;
		case 10:
			current.addDays(steps);
			break;
		case 6:
			current.addWeeks(steps);
			break;
		case 3:
			current.addMonths(steps);
			break;
		case 2:
			current.addMonths(3*steps);
			break;
		case 1:
			current.addYears(steps);
			break;
		default:
			throw new UnsupportedOperationException("interval type:"+timeIntervalLengthType+" not supported");
		}
	}

	/** Gets the millisecond offset to add to UTC to get local time.
	 * Note: It is not documented whether the timestamp is interpreted to be in UTC or in the local
	 * time zone. It only differs shortly when daylight savings time switches*/
	public static long getLocalTimeShiftRelativeToUTC(long timestamp) {
		DateTimeZone dtz = DateTimeZone.getDefault();
		return dtz.getOffset(timestamp);
	}
	
	/**Helper to convert a resource list of element type {@link TimeIntervalLength} into a Java int array*/
	public static int[] getTypeArray(ResourceList<TimeIntervalLength> resList) {
		int[] result = new int[resList.size()];
		for(int i=0; i<resList.size(); i++) {
			result[i] = resList.getAllElements().get(i).type().getValue();
		}
		return result;
	}
	
	public static int getUpperNextIntervalType(int intervalType) {
		switch(intervalType) {
		case 1:
			return 0;
		case 2:
			return 1;
		case 3:
			return 2;
		case 6:
			return 3;
		case 10:
			return 6;
		case 15:
			return 10;
		case 100:
			return 15;
		case 101:
			return 100;
		case 1000:
			return 101;
		case 1020:
			return 1000;
		default:
			throw new UnsupportedOperationException("Interval type "+intervalType+" not supported!");
		}
	}
	
	public static FloatResource getIntervalTypeStatistics(int intervalType, StatisticalAggregation sAgg) {
		switch(intervalType) {
		case 1:
			return sAgg.yearValue();
		case 2:
			return sAgg.quarterYearValue();
		case 3:
			return sAgg.monthValue();
		case 6:
			return sAgg.weekValue();
		case 10:
			return sAgg.dayValue();
		case 15:
			return sAgg.fourHourValue();
		case 100:
			return sAgg.hourValue();
		case 101:
			return sAgg.minuteValue();
		case 102:
			return sAgg.secondValue();
		case 220:
			return sAgg.fifteenMinuteValue();
		case 240:
			return sAgg.tenMinuteValue();
		case 320:
			return sAgg.fiveMinuteValue();
		case 1000:
			return sAgg.halfMinuteValue();
		case 1020:
			return sAgg.tenSecondValue();
		default:
			throw new UnsupportedOperationException("Interval type "+intervalType+" not supported!");
		}	
	}
}
