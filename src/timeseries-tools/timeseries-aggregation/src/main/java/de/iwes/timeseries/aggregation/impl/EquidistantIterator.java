package de.iwes.timeseries.aggregation.impl;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

/**
 * Basic implementation, to be improved (access every point of the underlying time series only once, do not copy data) 
 */
public class EquidistantIterator implements Iterator<SampledValue> {

	private final ReadOnlyTimeSeries input;
	private final InterpolationMode mode;
	private final long startTime;
	private final long endTime;
	// default: 1h
	private final TemporalUnit unit;
	private final long duration;
	private final ZoneId zoneId;
	// TODO implement
	private final boolean ignoreGaps;
	private final long minimumGapInterval;
	
	// states
	private SampledValue current;
	private final Queue<SampledValue> next = new ArrayDeque<>();
	
	// 
	private final Iterator<SampledValue> underlyingIterator;
	// after initialization this one will always be the last data point <= next.getLast().getTimestamp(); or currentInternal.getTimestamp (if next is empty)
	private SampledValue currentInternal = null;
	// after initialization this one will always be the first data point > next.getLast().getTimestamp(); or currentInternal.getTimestamp (if next is empty)
	private SampledValue nextInternal = null;
//	private float currentIntegral = 0;
//	private long currentLength = 0;
	
	public EquidistantIterator(ReadOnlyTimeSeries input, InterpolationMode mode, long startTime, long endTime, TemporalUnit unit, long duration, ZoneId timeZone,
			boolean ignoreGaps, long minGapDuration) {
		this.input = input;
		this.unit = unit;
		this.duration = duration;
		this.ignoreGaps = ignoreGaps;
		this.minimumGapInterval = minGapDuration;
		this.mode = mode != null ? mode : input.getInterpolationMode();
		if (this.mode == null || this.mode == InterpolationMode.NONE)
			throw new IllegalArgumentException("Interpolation mode " + this.mode + " not admissible.");
		if (timeZone == null)
			timeZone = ZoneId.systemDefault();
		this.zoneId = timeZone;
		final SampledValue last = input.getPreviousValue(Long.MAX_VALUE);
		if (last == null) {
			this.endTime = Long.MIN_VALUE;
			this.startTime = Long.MAX_VALUE;
			this.underlyingIterator = null;
			return;
		}
		this.endTime = Math.min(last.getTimestamp(), endTime);
		this.startTime = getStartTime(Math.max(startTime, input.getNextValue(Long.MIN_VALUE).getTimestamp()), unit, input, timeZone);
		if (this.startTime < this.endTime) {
			SampledValue previous = input.getPreviousValue(this.startTime);
			if (previous == null)
				previous = new SampledValue(FloatValue.ZERO, this.startTime, Quality.BAD);
			currentInternal = interpolate(this.startTime, previous, input.getNextValue(this.startTime), this.mode);
			if (currentInternal !=null) {
				this.underlyingIterator = input.iterator(this.startTime+1, this.endTime);
				if (underlyingIterator.hasNext())
					nextInternal = underlyingIterator.next();
			} else
				this.underlyingIterator = null;
		} else
			this.underlyingIterator = null;
	}
	
	
	private static long getNext(long time, TemporalUnit unit, long multiplier, ZoneId zoneId) {
		final long end;
		try {
			if (!unit.isDateBased())
				end = time + unit.getDuration().multipliedBy(multiplier).toMillis();
			else
				end = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), zoneId).plus(multiplier, unit).toInstant().toEpochMilli();
		} catch (ArithmeticException e) {
			return multiplier > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
		}
		if (multiplier > 0 && end < time)
			return Long.MAX_VALUE;
		else if (multiplier < 0 && end > time)
			return Long.MIN_VALUE;
		return end;
	}

	/*
	 * Assuming next != null
	 */
	private static final boolean nextIsNewer(final SampledValue current, final SampledValue next) {
		return current == null || next.compareTo(current) > 0;
	}
	
	@Override
	public boolean hasNext() {
		if (!next.isEmpty())
			return true;
		if (nextInternal == null)
			return false;
		advance();
		return !next.isEmpty();
	}
	
	@Override
	public SampledValue next() {
		if (!hasNext())
			throw new NoSuchElementException("No further element");
		current = next.poll();
		return current;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private void advance() {
		if (current == null && currentInternal == null) {
			throw new IllegalStateException("Advance called with currentInternal state = null; this should not happen");
		}
		long start = current != null ? getNext(current.getTimestamp(), unit, duration, zoneId) : currentInternal.getTimestamp(); // current aligned timestamp -> calculate integral for the coming interval
		if (start >= endTime || nextInternal == null)
			return;
		long end = getNext(start, unit, duration, zoneId);
		if (end < nextInternal.getTimestamp()) {
			start = bridgeGap(start, nextInternal.getTimestamp(), currentInternal, nextInternal);
			end = getNext(start, unit, duration, zoneId);
		}
		if (start >= endTime) {
			nextInternal = null;
			return;
		}
		float currentIntegral = 0;
		long currentLength = 0;
		long intStart = start;
		while (intStart < endTime && nextInternal.getTimestamp() <= end) {
			final float val = integrate(currentInternal, intStart, nextInternal.getTimestamp(), nextInternal, mode);	
			if (!Float.isNaN(val)) {
				currentIntegral += val;
				currentLength += nextInternal.getTimestamp() - intStart;
			}
			intStart = nextInternal.getTimestamp();
			currentInternal = nextInternal;
			if (underlyingIterator.hasNext()) 
				nextInternal = underlyingIterator.next();
			else {
				nextInternal = null;
				break;
			}
			// TODO gaps
		}
		if (nextInternal == null) {
			if (currentInternal.getTimestamp() < end) {
				final SampledValue next = input.getNextValue(end);
				if (next != null) {
					final SampledValue endValue = interpolate(end, currentInternal, next, mode);
					final float val = integrate(currentInternal, currentInternal.getTimestamp(), end, endValue, mode);
					if (!Float.isNaN(val)) {
						currentIntegral += val;
						currentLength += nextInternal.getTimestamp() - intStart;
					}
				}
			}
			if (currentLength > 0 && ignoreGaps) {
				this.next.add(new SampledValue(new FloatValue(currentIntegral/currentLength), start, Float.isNaN(currentIntegral) ? Quality.BAD : Quality.GOOD));
			} else {
				this.next.add(new SampledValue(FloatValue.NAN, start, Quality.BAD));
			}
			return;
		}
		// first complete pending interval
		if (intStart < end) { // could be equal as well, in which case we did not need to complete
			final float val = integrate(currentInternal, intStart, end, nextInternal, mode);
			if (!Float.isNaN(val)) {
				currentIntegral += val;
				currentLength += end - intStart;
			}
		}
		final boolean isNaN= Float.isNaN(currentIntegral) || currentLength == 0;
		this.next.add(new SampledValue(isNaN ? FloatValue.NAN : new FloatValue(currentIntegral/currentLength), start, isNaN ? Quality.BAD : Quality.GOOD));
		long nextNext = getNext(end, unit, duration, zoneId);
		if (nextInternal.getTimestamp() >= nextNext) {
			long newStart = bridgeGap(end, nextInternal.getTimestamp(), currentInternal, nextInternal);
			if (nextInternal.getTimestamp() == newStart) {
				currentInternal = nextInternal;
				if (underlyingIterator.hasNext())  
					nextInternal = underlyingIterator.next();
				else
					nextInternal = null;
			}
		} 
		
	}
	
	private long bridgeGap(final long from, final long to, final SampledValue previous, final SampledValue next) {
		long start = from;
		long end = getNext(start, unit, duration, zoneId);
		while (end <= to && start < endTime) {
			final float val = integrate(previous, start, end, next, mode);
			final boolean nan = Float.isNaN(val);
			final Quality q;
			if (nan) {
				q = Quality.BAD;
			} else {
				if (currentInternal.getQuality() == Quality.GOOD && nextInternal.getQuality() == Quality.GOOD)
					q = Quality.GOOD;
				else if (currentInternal.getQuality() == Quality.GOOD) {
					if (mode == InterpolationMode.STEPS) 
						q = Quality.GOOD;
					else if (mode == InterpolationMode.NEAREST && (nextInternal.getTimestamp()-end >= end - nextInternal.getTimestamp()))
						q = Quality.GOOD;
					else 
						q = Quality.BAD;
				}
				else if (nextInternal.getQuality() == Quality.GOOD) {
					if (mode == InterpolationMode.NEAREST && (nextInternal.getTimestamp()-end <= end - nextInternal.getTimestamp()))
						q = Quality.GOOD;
					else
						q = Quality.BAD;
				}
				else
					q =Quality.BAD;
			}
			this.next.add(new SampledValue(nan ? FloatValue.NAN : new FloatValue(val/(end-start)), start, q));
			start = end;
			end = getNext(start, unit, duration, zoneId);
		}
		return start;
	}
	
	private static final long getStartTime(final long startTime, final TemporalUnit unit, final ReadOnlyTimeSeries input, final ZoneId zoneId) {
		final long lastStart = getLastStandardIntervalStart(startTime, unit, zoneId);
		final long nextStart = getNext(lastStart, unit, 1, zoneId);
		if (!input.isEmpty(Long.MIN_VALUE, nextStart))
			return lastStart;
		final SampledValue next = input.getNextValue(startTime);
		if (next == null)
			return Long.MAX_VALUE;
		return getLastStandardIntervalStart(next.getTimestamp(), unit, zoneId);
	}
	
	private static long getLastStandardIntervalStart(final long start, final TemporalUnit unit, final ZoneId zoneId) {
		final long last;
		try {
			if (unit.isDateBased()) {
				ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(start), zoneId).truncatedTo(ChronoUnit.DAYS);
				if (unit == ChronoUnit.MONTHS)
					zdt = zdt.with(TemporalAdjusters.firstDayOfMonth());
				else if (unit == ChronoUnit.YEARS)
					zdt = zdt.with(TemporalAdjusters.firstDayOfYear());
				else if (unit == ChronoUnit.WEEKS)
					zdt = zdt.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
				last = zdt.toInstant().toEpochMilli();
			} else {
				last = Instant.ofEpochMilli(start).truncatedTo(unit).toEpochMilli();
			}
		} catch (ArithmeticException e) {
			return Long.MIN_VALUE;
		}
		return last;
	}
	
	// copied and adapted from TimeSeriesMultiIteratorImpl
	protected static final float integrate(final SampledValue previous, final long startT, final long endT, final SampledValue next, final InterpolationMode mode) {
		if (startT == endT)
			return 0;
		if (endT < startT)
			throw new IllegalArgumentException("Interval boundaries interchanged");
		final float p;
		final float n;
		if (mode == null)
			throw new NullPointerException("Interpolation mode is null, integration not possible");
		switch (mode) {
		case STEPS:
			return previous.getValue().getFloatValue() * (endT-startT);
		case LINEAR:
			p = previous.getValue().getFloatValue();
			n = next.getValue().getFloatValue();
			return (endT-startT)*(p+(n-p)*(startT+endT-2*previous.getTimestamp())/2/(next.getTimestamp()-previous.getTimestamp()));
		case NEAREST:
			p = previous != null ? previous.getValue().getFloatValue() : Float.NaN;
			n = next != null ? next.getValue().getFloatValue() : Float.NaN;
			final long boundary = (next.getTimestamp() + previous.getTimestamp())/2;
			if (boundary <= startT)
				return n*(endT-startT);
			if (boundary >= endT)
				return p*(endT-startT);
			return p*(boundary-startT) + n*(endT-boundary);
		default:
			return Float.NaN;
		}
	}
	

	
	protected static final SampledValue interpolate(final long t, final SampledValue previous, final SampledValue next, final InterpolationMode mode) {
		if (previous != null && previous.getTimestamp() == t) 
			return previous;
		if (next != null && next.getTimestamp() == t)
			return next;
		switch (mode) {
		case STEPS:
			if (previous == null)
				return null;
			return new SampledValue(previous.getValue(), t, previous.getQuality());
		case NONE:
			return null;
		case LINEAR:
			if (previous == null || next == null)
				return null;
			final float p = previous.getValue().getFloatValue();
			final float n = next.getValue().getFloatValue();
			Long tp = previous.getTimestamp();
			Long tn = next.getTimestamp();
			if (tn.longValue() == tp.longValue())
				throw new IllegalArgumentException("Received same timestamp values : " + tp);
			float newV = p + (n-p)*(t-tp)/(tn-tp);
			return new SampledValue(new FloatValue(newV), t, 
				previous.getQuality() == Quality.GOOD && next.getQuality() == Quality.GOOD ? Quality.GOOD : Quality.BAD);
		case NEAREST:
			if (previous == null && next == null)
				return null;
			tp = (previous != null ? previous.getTimestamp() : null);
			tn = (next != null ? next.getTimestamp() : null);
			final SampledValue sv = (tp == null ? next : tn == null ? previous : (t-tp)<=(tn-t) ? previous : next);
			return new SampledValue(sv.getValue(), t, sv.getQuality());
		default:
			return null;
		}
	}
	
	
}
