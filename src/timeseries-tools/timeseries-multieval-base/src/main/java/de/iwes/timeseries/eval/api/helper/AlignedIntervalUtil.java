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
