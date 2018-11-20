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
package de.iwes.timeseries.aggregation.api;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.aggregation.impl.StandardIntervalTimeSeries;

/**
 * Wrap a time series into another one which has points at equal distances.
 * The resulting time series can provide exactly one data point per hour, for instance,
 * whereas the data points in the underlying time series may be at completely 
 * random timestamps. The value of the resulting time series is obtained by 
 * integrating the underlying one over the relevant integral and dividing by the
 * interval length. It is hence important that the underlying time series specifies 
 * the correct interpolation mode (must not be {@link InterpolationMode#NONE}). 
 * Alternatively, it is possible to specify the interpolation mode to be used explicitly,
 * via {@link #setInterpolationMode(InterpolationMode)}.
 *  
 * @author cnoelle
 *
 * @param <N>
 */
public class StandardIntervalTimeseriesBuilder {
	
	private final ReadOnlyTimeSeries timeSeries;
	// default: 1h
	private TemporalUnit unit = ChronoUnit.HOURS;
	private long duration = 1;
//	private Duration totalDuration = unit.getDuration().multipliedBy(duration);
	private ZoneId timeZone = ZoneId.systemDefault();
	private InterpolationMode mode;
	private boolean ignoreGaps;
	private Long minGapDuration;

	/**
	 * @param timeSeries
	 * 		The underlying time series
	 * @param type
	 * 		The type of the time series data points. Must be one of the following:
	 * 		<ul>
	 * 			<li><code>Float.class</code>
	 * 			<li><code>Boolean.class</code>
	 * 			<li><code>Integer.class</code>
	 * 			<li><code>Long.class</code>
	 * 		</ul>
	 */
	private StandardIntervalTimeseriesBuilder(ReadOnlyTimeSeries timeSeries) {
		this.timeSeries = Objects.requireNonNull(timeSeries);
//		this.type = Objects.requireNonNull(type);
//		if (type != Float.class && type != Integer.class && type != Long.class && type != Boolean.class)
//			throw new IllegalArgumentException("Illegal type, must be either Float.class, Boolean.class, Integer.class or Long.class, got " + type);
	}
	
	/**
	 * Create a new builder instance.
	 * @param timeSeries
	 * 		The underlying time series
	 * @return
	 */
	public static StandardIntervalTimeseriesBuilder newBuilder(ReadOnlyTimeSeries timeSeries) {
		return new StandardIntervalTimeseriesBuilder(timeSeries);
	}
	
	public ReadOnlyTimeSeries build() {
		return new StandardIntervalTimeSeries(timeSeries, Float.class, unit, duration, timeZone, mode, ignoreGaps, 
				minGapDuration != null ? minGapDuration : (2L * unit.getDuration().multipliedBy(duration).toMillis()));
	}
	
	public StandardIntervalTimeseriesBuilder setInterval(long duration, TemporalUnit unit) {
		if (duration <= 0)
			throw new IllegalArgumentException("Duration non-positive: " + duration);
		this.duration = duration;
		this.unit = Objects.requireNonNull(unit);
		return this;
	}
	
	public StandardIntervalTimeseriesBuilder setTimeZone(ZoneId zoneId) {
		this.timeZone = Objects.requireNonNull(zoneId);
		return this;
	}
	
	public StandardIntervalTimeseriesBuilder setInterpolationMode(InterpolationMode mode) {
		this.mode = mode;
		return this;
	}
	
	/**
	 * If set to true, gaps in the underlying time series are ignored. Default: false. 
	 * @param ignoreGaps
	 * @return
	 */
	public StandardIntervalTimeseriesBuilder setIgnoreGaps(boolean ignoreGaps) {
		this.ignoreGaps = ignoreGaps;
		return this;
	}

	/**
	 * If the time difference between two data points in the underlying time series is larger
	 * than the minimum gap duration, then bad quality values will be inserted into the 
	 * resulting time series, indicating a gap. This only takes effect if 
	 * {@link #setIgnoreGaps(boolean)} is not set to true.
	 * <br>
	 * Default value: 2 times the total interval duration (see {@link #setInterval(long, TemporalUnit)}).
	 * @param duration
	 * 		duration in ms.
	 * @return
	 */
	public StandardIntervalTimeseriesBuilder setMinimumGapDuration(long duration) {
		this.minGapDuration = duration;
		return this;
	}
	
	
}
