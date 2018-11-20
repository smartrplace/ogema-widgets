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
package de.iwes.util.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.core.timeseries.TimeSeries;
import org.ogema.tools.resource.util.LoggingUtils;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.api.MemoryTimeSeries;

public class ScheduleHelper {
	/** Copy float array into schedule assuming a constant time step
	 * 
	 * @param inData source data
	 * @param startTime time in ms since epoch UTC
	 * @param length copy only this number of elements from inData, if negative copy everything
	 * @param timeStep duration of each interval inData reflects
	 * @param schedule destination
	 */
	public static void copyFloatArrayToSched(float[] inData, long startTime, int length,
			long timeStep, Schedule schedule) {
		if(length < 0) length = inData.length;
		
		schedule.deleteValues(startTime, startTime + (length * timeStep));
		for (int i = 0; i < length; i++) {
			float value = inData[i];
			schedule.addValue(startTime + (i * timeStep), new FloatValue(value));
		}
	}
	/** schedule_value[i] = factor*(inData[i]+offset)
	 * 
	 * @param factor
	 * @param offset
	 */
	public static void copyFloatArrayToSched(float[] inData, long startTime, int length,
			long timeStep, Schedule schedule, float factor, float offset) {
		if(length < 0) length = inData.length;
		
		schedule.deleteValues(startTime, startTime + (length * timeStep));
		for (int i = 0; i < length; i++) {
			float value = factor*(inData[i]+offset);
			schedule.addValue(startTime + (i * timeStep), new FloatValue(value));
		}
	}
	
	public static RecordedData getHistoricalData(SingleValueResource resource) throws IllegalArgumentException {
		return LoggingUtils.getHistoricalData(resource);
	}
	public static AbsoluteSchedule getHistoricalDataSchedule(SingleValueResource resource) throws IllegalArgumentException {
		return LoggingUtils.getHistoricalDataSchedule(resource);
	}

	public static MemoryTimeSeries getTemperatureScheduleInCelsius(TimeSeries schedule) {
		return ValueResourceUtils.getTemperatureScheduleInCelsius(schedule);
	}
	
	/**
	 * Multiplies each value of <code>schedule</code> with <code>factor</code> and adds <code>addend</code>
	 * 
	 * @param schedule
	 * @param factor
	 * @param addend
	 * @return
	 */
	public static MemoryTimeSeries affineTransformation(ReadOnlyTimeSeries schedule, float factor, float addend) {
		return ValueResourceUtils.affineTransformation(schedule, factor, addend);
	}
	/** Get maximum value from schedule
	 * 
	 * @param values
	 * @param mode  0: just get maximum
	 *              1: get maximal absolute value
	 * @return maximum value or zero if list is empty
	 */
	public static float getMaxValue(List<SampledValue> values, int mode, boolean omitBadQuality, boolean omitInfinity) {
		if(values.isEmpty()) return 0;
		float maxValue = -Float.MAX_VALUE;
		for(SampledValue sv: values) {
			if (omitBadQuality && Quality.BAD.equals(sv.getQuality())) continue;
			float val = sv.getValue().getFloatValue();
			if(mode==1) {
				if ((!Float.isNaN(val)) && (!(Float.isInfinite(val) && omitInfinity)) && Math.abs(val) > maxValue) {
					maxValue = Math.abs(val);
				}
			} else {
				if ((!Float.isNaN(val)) && (!(Float.isInfinite(val) && omitInfinity)) && val > maxValue) {
					maxValue = val;
				}
			}
		}
		return maxValue;
	}
	
	/** Get maximum value from schedule, and the timestamp of the maximum value, in the format [ timestamp, value ] (types: [ Long, Float ])
	 * 
	 * @param values
	 * @param mode  0: just get maximum
	 *              1: get maximal absolute value
	 * @return [ timestamp, maximum value], or null if list is empty
	 */
	@Deprecated
	public static Object[] getMaxValueAndPoint(List<SampledValue> values, int mode, boolean omitBadQuality, boolean omitInfinity) {
		if(values.isEmpty()) return null;
		float maxValue = -Float.MAX_VALUE;
		long timestamp = Long.MIN_VALUE;
		for(SampledValue sv: values) {
			if (omitBadQuality && Quality.BAD.equals(sv.getQuality())) continue;
			float val = sv.getValue().getFloatValue();
			if (Float.isNaN(val) || (Float.isInfinite(val) && omitInfinity))
				continue;
			if (mode == 1)
				val = Math.abs(val);
			if (val > maxValue) {
				maxValue = val;
				timestamp = sv.getTimestamp();
			}
		}
		return new Object[]{timestamp, maxValue};
	}
	
	public static Object[] getMaxValueAndPoint(Iterator<SampledValue> values, int mode, boolean omitBadQuality, boolean omitInfinity) {
		if (!values.hasNext()) 
			return null;
		float maxValue = -Float.MAX_VALUE;
		long timestamp = Long.MIN_VALUE;
		SampledValue sv;
		while (values.hasNext()) {
			sv = values.next();
			if (omitBadQuality && Quality.BAD.equals(sv.getQuality())) continue;
			float val = sv.getValue().getFloatValue();
			if (Float.isNaN(val) || (Float.isInfinite(val) && omitInfinity))
				continue;
			if (mode == 1)
				val = Math.abs(val);
			if (val > maxValue) {
				maxValue = val;
				timestamp = sv.getTimestamp();
			}
		}
		return new Object[]{timestamp, maxValue};
	}
	
	public static void copySchedule(Schedule source, Schedule destination, long startTime, long endTime) {
		List<SampledValue> values = source.getValues(startTime, endTime);
		destination.deleteValues(startTime, endTime);
		destination.addValues(values);
	}
	
	/** Add values of schedule into list with fixed timeStep
	 * 
	 * @param def
	 * @param defaultValue
	 * @param start
	 * @param end
	 * @param resultValues
	 */
	/*public static void aggregateScheduleIntoList(Schedule def, float defaultValue,
				DateTime start, DateTime end,  List<Float> resultValues, long timeStep,
				float factor, float offset) {
		 
		Interval interval = new Interval(start, end);
		int j = 0;
		for (long i = interval.getStartMillis(); i < interval
				.getEndMillis(); i = i + timeStep) {

			SampledValue sv = def.getValue(i);
			if (j > resultValues.size() - 1) {

				if (sv != null) {
					resultValues.add(sv.getValue().getFloatValue());
				} else {
					resultValues.add(defaultValue);
				}
			} else {
				Float newValue;

				if (sv != null) {
					newValue = -1
							* def.getValue(i)
									.getValue().getFloatValue();
				} else {
					newValue = defaultValue;
				}

				resultValues.add(j, newValue + resultValues.get(j));
			}
			j++;
		}
	 }*/
	
	/** Note: Initial implementation assuming steps, integrated over seconds
	 * @deprecated use {@link org.ogema.tools.resource.util.ValueResourceUtils#integrate(ReadOnlyTimeSeries,long, long)} 
	 * or {@link FloatTimeSeries#integrate(org.ogema.tools.timeseries.api.TimeInterval)} (or one of its variants) instead
	 */
	@Deprecated
	public static float getIntegral(Schedule sched, long startTime, long endTime) {
		List<SampledValue> values = sched.getValues(startTime, endTime);
		float sum = 0;
		for(int i=0; i<values.size()-1; i++) {
			SampledValue sv = values.get(i);
			SampledValue svnext = values.get(i+1);
			sum += sv.getValue().getFloatValue()*(svnext.getTimestamp() - sv.getTimestamp());
		}
		return (sum * 0.001f);
	}
	
	public static class ScheduleIntervalEvalResult {
		public float average;
		public long badValueTime = 0;
		public long goodValueTime = 0;
		public int badIntevalNum = 0;
	}
	/** Calculate average omitting intervals longer than maxOKInterval without a valid value. Note
	 * that calculation could be improved by many details. Currently only makes sense when the interval
	 * is long enough that the gap between the first value and the start of interval is not relevant.
	 * 
	 * @param sched
	 * @param startTime
	 * @param endTime
	 * @param maxOKInterval
	 * @param acceptBadValues if true values marked as bad will still be accepted and considered,
	 * 		otherwise they are considered not existing
	 * @param useSign: if positive only positive values are considered, if negative only negative values are considered,
	 * if zero all values are considered
	 * @param registerMode if true the values in the schedule are expected to be from a meter register (e.g. energy counted) whereas 
	 * 		avering has to be done on power values calculated as differences per second from the register values
	 * @return
	 */
	public static ScheduleIntervalEvalResult getAverageFromGoodValues(ReadOnlyTimeSeries sched, long startTime, long endTime,
			long maxOKInterval, boolean acceptBadValues, int useSign, boolean registerMode) {
		ScheduleIntervalEvalResult ret = new ScheduleIntervalEvalResult();
		List<SampledValue> values = sched.getValues(startTime, endTime);
		if(!acceptBadValues) {
			List<SampledValue> removeValues = new ArrayList<>();
			for(SampledValue sv: values) {
				if(sv == null) {
					System.out.println("Nullpointer value in schedule!");
					removeValues.add(sv);
					continue;
				}
				Quality q = sv.getQuality();
				if(q == null) {
					System.out.println("Nullpointer quality in schedule!");
					removeValues.add(sv);
					continue;					
				}
				if(q.equals(Quality.BAD)) {
					removeValues.add(sv);
				}
			}
			values.removeAll(removeValues);
		}
		if(values.size() == 0) {
			ret.average = Float.NaN;
			ret.badIntevalNum = 1;
			ret.badValueTime = endTime - startTime;
			return ret;
		}
		float sum = 0;
		for(int i=0; i<values.size()-1; i++) {
			SampledValue sv = values.get(i);
			SampledValue svnext = values.get(i+1);
			long gap = svnext.getTimestamp() - sv.getTimestamp();
			if(gap > maxOKInterval) {
				ret.badValueTime += gap;
				ret.badIntevalNum++;
				gap = maxOKInterval;
				if(registerMode) continue;
			}
			float val;
			if(registerMode) {
				val = (svnext.getValue().getFloatValue() - sv.getValue().getFloatValue())*1000/gap;
			} else {
				val =  sv.getValue().getFloatValue();				
			}
			if((useSign == 0) || (useSign > 0) && (val >0) || (useSign < 0) && (val <0)) {
				sum += val*gap;
			}
			ret.goodValueTime += gap;
		}
		ret.average = sum / ret.goodValueTime;
		return ret;
	}
}
