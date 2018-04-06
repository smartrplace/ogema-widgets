package de.iwes.timeseries.eval.api.helper;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;

public class AlignedIntervalUtil {
	public static long geIntervalStart(final long start, final TemporalUnit unit) {
		return geIntervalStart(start, unit, ZoneId.systemDefault());
	}
	public static long geIntervalStart(final long start, final TemporalUnit unit, final ZoneId zoneId) {
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
	public static long getNextIntervalStart(long alignedTime, TemporalUnit unit) {
		return getNextIntervalStart(alignedTime, unit, 1, ZoneId.systemDefault());
	}	
	public static long getNextIntervalStart(long alignedTime, TemporalUnit unit, long multiplier, ZoneId zoneId) {
		final long end;
		try {
			if (!unit.isDateBased())
				end = alignedTime + unit.getDuration().multipliedBy(multiplier).toMillis();
			else
				end = ZonedDateTime.ofInstant(Instant.ofEpochMilli(alignedTime), zoneId).plus(multiplier, unit).toInstant().toEpochMilli();
		} catch (ArithmeticException e) {
			return multiplier > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
		}
		if (multiplier > 0 && end < alignedTime)
			return Long.MAX_VALUE;
		else if (multiplier < 0 && end > alignedTime)
			return Long.MIN_VALUE;
		return end;
	}


}
