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
package de.iwes.timeseries.eval.viewer.gui;

import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.DataTree;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.calendar.datepicker.DatepickerData;

class ViewerDatepicker extends Datepicker {

	private static final long serialVersionUID = 1L;
	private final boolean isStart;
	private final DataTree dataTree;
	private final ScheduleViewerPage page;

	public ViewerDatepicker(WidgetPage<?> page, String id, boolean isStart, DataTree dataTree, ScheduleViewerPage spage) {
		super(page, id);
		this.isStart = isStart;
		this.dataTree = dataTree;
		this.page = spage;
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		final OgemaWidget trigger = getPage().getTriggeringWidget(req);
		if (trigger != null && page.fixInterval(req))
			return;
		final List<? extends ReadOnlyTimeSeries> schedules = dataTree.getSelectedSchedules(req);
		long startTime = (isStart ? Long.MAX_VALUE: Long.MIN_VALUE);
		for (ReadOnlyTimeSeries sched: schedules) {
			SampledValue sv = (isStart ? sched.getNextValue(Long.MIN_VALUE): sched.getPreviousValue(Long.MAX_VALUE));
			if (sv != null) {
				long start0 = sv.getTimestamp();
				if ((isStart && start0 < startTime) || (!isStart && start0 > startTime)) 
					startTime = start0;
			}
		}
		if (isStart) {
			if (startTime == Long.MAX_VALUE)
				startTime = System.currentTimeMillis();
		} else {
			if (startTime == Long.MIN_VALUE)
				startTime = System.currentTimeMillis();
			if (startTime < Long.MAX_VALUE - 10000)
				startTime += 1001; // ensure all data points are really shown
		}
		setDate(startTime, req);
	}
	
	
	// TODO
	private static class ViewerDatepickerData extends DatepickerData {

		private boolean fixedInterval = false;
		
		public ViewerDatepickerData(ViewerDatepicker datepicker) {
			super(datepicker);
		}
		
	}
	

}
