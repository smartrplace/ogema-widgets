package de.iwes.timeseries.aggregation.provider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;

import de.iwes.timeseries.aggregation.provider.AggregationProvider.AggregationType;

/**
 * A dummy iterator that returns a value at the beginning of each 
 * standard interval, such as every 15min, at the beginning of a day, week,
 * month, year, etc.
 */
// TODO need a MultiIterator that does not return additional points if only 
// certain specific iterators have a next value?
// or can we ignore this?
public class StandardIntervalIterator implements Iterator<SampledValue> {

	private final long duration;
	private final TemporalUnit unit;
	private final AggregationType type;
	private final ZonedDateTime endTime;
	private ZonedDateTime next; // null when end time exceeded
	
	public StandardIntervalIterator(AggregationType type, long startTime, long endTime) {
		this(type, startTime, endTime, ZoneId.systemDefault());
	}
	
	public StandardIntervalIterator(AggregationType type, long startTime, long endTime, ZoneId timeZone) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(timeZone);
		final long duration = type.getDuration();
		final TemporalUnit unit = type.getUnit();
		if (duration <= 0)
			throw new IllegalArgumentException("Duration must be positive, got " + duration);
		this.type = type;
		this.duration = duration;
		this.unit = unit;
		this.endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), timeZone);
		Instant i = Instant.ofEpochMilli(startTime);
		final ZonedDateTime zdt = ZonedDateTime.ofInstant(i, timeZone);
		final TemporalUnit truncationUnit = getNextApplicableTruncationUnit(duration,unit);
		try {
			next = zdt.truncatedTo(truncationUnit);
		} catch (UnsupportedTemporalTypeException e) {
			next = zdt; // TODO this is probably not always the desired solution
		}
	}
	
	private final static SampledValue getValue(ZonedDateTime time) {
		return new SampledValue(FloatValue.NAN, time.toInstant().toEpochMilli(), Quality.BAD);
	}
	
	private final static TemporalUnit getNextApplicableTruncationUnit(long duration,TemporalUnit unit) {
		if (duration == 1)
			return unit;
		if (unit.equals(ChronoUnit.MINUTES))
			return 60 % duration == 0 ? ChronoUnit.HOURS : ChronoUnit.MINUTES;
		if (unit.equals(ChronoUnit.HOURS))
			return 24 % duration == 0 ? ChronoUnit.DAYS : ChronoUnit.HOURS;
		if (unit.equals(ChronoUnit.SECONDS))
			return 60 % duration == 0 ? ChronoUnit.MINUTES : ChronoUnit.SECONDS;
		return unit;
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}
	
	@Override
	public SampledValue next() {
		if (next == null)
			throw new NoSuchElementException();
		final SampledValue current = getValue(next);
		next = next.plus(duration, unit);
		if (next.compareTo(endTime) > 0) 
			next = null;
		return current;
	}
	
	public AggregationType getType() {
		return type;
	}
	
}
