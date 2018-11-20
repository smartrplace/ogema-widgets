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
package de.iwes.widgets.reswidget.scheduleplot.morris2;

import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration.AxisType;
import de.iwes.widgets.html.plotmorris.MorrisChartConfiguration;
import de.iwes.widgets.html.plotmorris.MorrisDataSet;
import de.iwes.widgets.html.plotmorris.PlotMorris;
import de.iwes.widgets.html.plotmorris.PlotMorrisOptions;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public class SchedulePlotMorris extends PlotMorris implements TimeSeriesPlot<MorrisChartConfiguration, MorrisDataSet, ScheduleDataMorris>{

	private static final long serialVersionUID = 1L;
	private Map<String,SchedulePresentationData> defaultSchedules = null;

	/****** Constructor *******/

	public SchedulePlotMorris(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	public SchedulePlotMorris(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		getDefaultConfiguration().setXAxisType0(AxisType.TIME);
	}

	/***** Inherited methods ******/

	@Override
	public SchedulePlotMorrisOptions createNewSession() {
		return new SchedulePlotMorrisOptions(this);
	}

	@Override
	public SchedulePlotMorrisOptions getData(OgemaHttpRequest req) {
		return (SchedulePlotMorrisOptions) super.getData(req);
	}

	@Override
	protected void setDefaultValues(PlotMorrisOptions opt) {
		super.setDefaultValues(opt);
		SchedulePlotMorrisOptions opt2 = (SchedulePlotMorrisOptions) opt;
		if (defaultSchedules != null)
			opt2.getScheduleData().setSchedules(defaultSchedules);
	}


	/****** Public methods ********/

	@Override
	public void setDefaultSchedules(Map<String,SchedulePresentationData> schedules) {
		this.defaultSchedules = schedules;
	}

	@Override
	public ScheduleDataMorris getScheduleData(OgemaHttpRequest req) {
		return getData(req).getScheduleData();
	}

	@Override
	public void setInterval(long startTime, long endTime, OgemaHttpRequest req) {
		if (endTime < startTime) throw new IllegalArgumentException("Start time after end time: " + startTime + ": " + endTime);
		ScheduleData<?> data = getScheduleData(req);
		data.setStartTime(startTime);
		data.setEndTime(endTime);
	}
}
