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
package de.iwes.widgets.reswidget.scheduleviewer;

import java.util.Collection;
import java.util.OptionalLong;
import java.util.function.LongSupplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;

/**
 * A {@link Datepicker} that displays the first or last timestamp 
 * for which a set of schedules is defined.
 */
public abstract class StartEndDatepicker extends Datepicker {

	private static final long serialVersionUID = 1L;
	private final boolean startOrEnd;

	public StartEndDatepicker(WidgetPage<?> page, String id, boolean startOrEnd) {
		super(page, id);
		this.startOrEnd = startOrEnd;
	}

	public StartEndDatepicker(OgemaWidget parent, String id, OgemaHttpRequest req, boolean startOrEnd) {
		super(parent, id, req);
		this.startOrEnd = startOrEnd;
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		if (!autoModeEnabled(req))
			return;
		final Stream<ReadOnlyTimeSeries> schedules = getSelectedSchedules(req);
		final LongStream stream = schedules
			.map(schedule -> startOrEnd ? schedule.getNextValue(Long.MIN_VALUE) : schedule.getPreviousValue(Long.MAX_VALUE))
			.filter(sv -> sv != null)
			.mapToLong(SampledValue::getTimestamp);
		final OptionalLong extreme = startOrEnd ? stream.min() : stream.max();
		long t = extreme.orElseGet(System::currentTimeMillis);
		if (!startOrEnd && t != Long.MAX_VALUE)
			t++;
		setDate(t, req);
	}

	/**
	 * @param req
	 * @return
	 * 		never null
	 */
	protected abstract Stream<ReadOnlyTimeSeries> getSelectedSchedules(OgemaHttpRequest req);
	/**
	 * Override if required
	 * @param req
	 * @return
	 */
	protected boolean autoModeEnabled(OgemaHttpRequest req) {
		return true;
	};	
	
	
}
