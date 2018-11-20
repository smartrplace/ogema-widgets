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
package de.iwes.widgets.reswidget.scheduleplot.api;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;

import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public class MaxValBuffer {

	private final long windowSize;
	final SchedulePresentationData schedule;
	// keys: interval start time
	// contains intervals of different sizes
	private final NavigableMap<Long, Interval<Long>> intervals = new TreeMap<>(); 
	
	public MaxValBuffer(SchedulePresentationData schedule, long windowSize) {
		if (windowSize <= 0)
			throw new IllegalArgumentException();
		Objects.requireNonNull(schedule);
		this.schedule = schedule;
		this.windowSize = windowSize;
	}
	
	public Float getMaxValue(long min, long max) {
		if (min > max)
			return null;
		if (max-min < windowSize) {
			Object[] result = getMaxValue(schedule.iterator(min, max));
			if (result == null)
				return null;
			return (Float) result[1];
		}
		Float maxValue = null;
		synchronized (this) {
			SortedMap<Long, Interval<Long>> submap = intervals.subMap(Long.MIN_VALUE, min+windowSize);
			for (Interval<Long> bufferedInterval: submap.values()) {
				if (bufferedInterval.max < max - windowSize || bufferedInterval.min > min + windowSize)
					continue;
				maxValue = getMaxValue(min, max, bufferedInterval);
				if (maxValue == null)
					continue;
				if (bufferedInterval.min > min) {
					Object[] remainder = getMaxValue(schedule.iterator(min, bufferedInterval.min));
					if (remainder != null) {
						float subMax = (float) remainder[1];
						if (subMax > maxValue)
							maxValue = subMax;
					}
				}
				if (bufferedInterval.max < max) {
					Object[] remainder = getMaxValue(schedule.iterator(bufferedInterval.max, max));
					if (remainder != null) {
						float subMax = (float) remainder[1];
						if (subMax > maxValue)
							maxValue = subMax;
					}
				}
				break;
			}
			if (maxValue == null) {
				Object[] maxPair = getMaxValue(schedule.iterator(min, max));
				if (maxPair != null) {
					Interval<Long> newIntv = new Interval<Long>(min, max, (long) maxPair[0], (float) maxPair[1]);
					intervals.put(min, newIntv);
				}
				
			}
		}
		return maxValue;
	}
	
	private static Object[] getMaxValue(Iterator<SampledValue> values) {
		Float val = null;
		Long t = null;
		float localVal;
		while (values.hasNext()) {
			SampledValue sv = values.next();
			if (sv.getQuality() == Quality.BAD)
				continue;
			localVal =  sv.getValue().getFloatValue();
			if (val == null || localVal > val) {
				val = localVal;
				t = sv.getTimestamp();
			}
		}
		if (val == null)
			return null;
		return new Object[] { t, val };
	}

	/**
	 * Returns null if the maximum value cannot be determined from the interval 
	 * @param min
	 * @param max
	 * @param interval
	 * @return
	 */
	private static Float getMaxValue(long min, long max, Interval<Long> interval) {
		if (interval.maxValuePoint < min || interval.maxValuePoint > max) {
			return null;
		}
		return interval.maxValue;
	}
	
	
	public static class Interval<T extends Comparable<T>> implements Cloneable {
		
		private final T min;
		private final T max;
		private final T maxValuePoint;
		private final float maxValue;
		
		public Interval(T min, T max, T maxValuePoint, float maxValue) {
			Objects.requireNonNull(min);
			Objects.requireNonNull(max);
			Objects.requireNonNull(maxValuePoint);
			if (min.compareTo(max) > 0 || min.compareTo(maxValuePoint) > 0 || max.compareTo(maxValuePoint) < 0) 
				throw new IllegalArgumentException("min > max, or max value point out of range; min = " + min + ", max = " + max + ", max value point: " + maxValuePoint);
			this.min = min;
			this.max = max;
			this.maxValuePoint = maxValuePoint;
			this.maxValue = maxValue;
		}
		
		public boolean isContainedIn(Interval<T> other) {
			return min.compareTo(other.min) >= 0 && max.compareTo(other.max) <= 0;
		}
		
		public boolean contains(T t) {
			return min.compareTo(t) <= 0 && max.compareTo(t) >= 0; 
		}
		
		@Override
		protected Interval<T> clone() {
			return new Interval<T>(min, max, maxValuePoint, maxValue);
		}
		
	}
	
}
