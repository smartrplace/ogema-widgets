package de.iwes.timeseries.aggregation.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

public class StandardIntervalTimeSeries implements ReadOnlyTimeSeries {
	
	private final ReadOnlyTimeSeries timeSeries;
	private final Class<?> type;
	// default: 1h
	private final TemporalUnit unit;
	private final long duration;
	private final Duration totalDuration;
	private final InterpolationMode mode;
	// may be null;
	private final ZoneId zoneId;
	private final boolean ignoreGaps;
	private final long minGapDuration;
	
	public StandardIntervalTimeSeries(ReadOnlyTimeSeries timeSeries, Class<?> type, TemporalUnit unit, long duration, ZoneId zoneId, InterpolationMode mode,
			boolean ignoreGaps, long minGapDuration) {
		this.timeSeries = timeSeries;
		this.zoneId = zoneId;
		this.type = type;
		this.unit = unit;
		this.duration = duration;
		this.mode = mode;
		this.totalDuration = unit.getDuration().multipliedBy(duration);
		this.ignoreGaps = ignoreGaps;
		this.minGapDuration = minGapDuration;
	}

	@Override
	public SampledValue getValue(long time) {
		SampledValue previous = getPreviousValue(time);
		SampledValue next = getNextValue(time);
		if (previous == null)
			return null;
		if (next != null && next.getTimestamp() == time)
			return next;
		Instant i = Instant.ofEpochMilli(time);
		if (i.minus(totalDuration).toEpochMilli() > previous.getTimestamp()) // FIXME overflow
			return null;
		return new SampledValue(previous.getValue(), time, previous.getQuality());
	}

	@Override
	public SampledValue getNextValue(long time) {
		final Iterator<SampledValue> it = iterator(time, Long.MAX_VALUE);
		if (!it.hasNext())
			return null;
		return it.next();
	}

	@Override
	public SampledValue getPreviousValue(long time) {
		final SampledValue next = getNextValue(time);
		if (next != null && next.getTimestamp() == time)
			return next;
		final SampledValue realValue = timeSeries.getPreviousValue(time);
		if (realValue == null)
			return null;
		final long diff = time-realValue.getTimestamp();
		final long nrIntervals = diff / totalDuration.toMillis() + 2;
		final long starttime;
		if (time > (Long.MIN_VALUE + nrIntervals*totalDuration.toMillis()))
			starttime = time-totalDuration.toMillis()* nrIntervals;
		else
			starttime = Long.MIN_VALUE;
		final Iterator<SampledValue> it = iterator(starttime, time);
		SampledValue cand = null;
		while (it.hasNext())
			cand = it.next();
		return cand;
	}

	@Override
	public List<SampledValue> getValues(long startTime) {
		return getValues(startTime, Long.MAX_VALUE);
	}

	@Override
	public List<SampledValue> getValues(long startTime, long endTime) {
		final List<SampledValue> values = new ArrayList<>();
		final Iterator<SampledValue> it = iterator(startTime, endTime);
		while (it.hasNext())
			values.add(it.next());
		return values;
	}

	@Override
	public InterpolationMode getInterpolationMode() {
		return InterpolationMode.STEPS;
	}

	@Override
	public boolean isEmpty() {
		return timeSeries.isEmpty();
	}

	// TODO consider gaps
	@Override
	public boolean isEmpty(long startTime, long endTime) {
		return timeSeries.isEmpty(startTime, endTime)
				&& (timeSeries.getPreviousValue(startTime) == null || timeSeries.getNextValue(endTime) == null);
	}

	// best guess
	@Override
	public int size() {
		return size(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Override
	public int size(long startTime, long endTime) {
		if (isEmpty(startTime, endTime))
			return 0;
		return (int) ((timeSeries.getPreviousValue(endTime).getTimestamp() - timeSeries.getNextValue(startTime).getTimestamp() +totalDuration.toMillis()-1)/totalDuration.toMillis());

	}

	@Override
	public Iterator<SampledValue> iterator() {
		return iterator(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Override
	public Iterator<SampledValue> iterator(long startTime, long endTime) {
		return new EquidistantIterator(timeSeries, mode, startTime, endTime, unit, duration, zoneId, ignoreGaps, minGapDuration);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Long getTimeOfLatestEntry() {
		return timeSeries.getTimeOfLatestEntry();
	}

}
