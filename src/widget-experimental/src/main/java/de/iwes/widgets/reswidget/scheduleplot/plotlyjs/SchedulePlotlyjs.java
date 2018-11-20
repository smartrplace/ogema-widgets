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
package de.iwes.widgets.reswidget.scheduleplot.plotlyjs;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.cache.Cache;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration.AxisType;
import de.iwes.widgets.html.plotlyjs.Plotlyjs;
import de.iwes.widgets.html.plotlyjs.PlotlyjsConfiguration;
import de.iwes.widgets.html.plotlyjs.PlotlyjsDataSet;
import de.iwes.widgets.html.plotlyjs.PlotlyjsOptions;
import de.iwes.widgets.reswidget.scheduleplot.api.DefaultMaxValuesSupplier;
import de.iwes.widgets.reswidget.scheduleplot.api.MaxValBuffer;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public class SchedulePlotlyjs extends Plotlyjs implements TimeSeriesPlot<PlotlyjsConfiguration, PlotlyjsDataSet, ScheduleDataPlotlyjs> {

	private static final long serialVersionUID = 1L;
	private Map<String,SchedulePresentationData> defaultSchedules = null;
	final Supplier<Cache<String, MaxValBuffer>> maxValues;
	final Long bufferWindow;


	/****** Constructor *******/

	public SchedulePlotlyjs(WidgetPage<?> page, String id, boolean globalWidget) {
		this(page, id, globalWidget, null);
	}

	public SchedulePlotlyjs(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
		getDefaultConfiguration().setXAxisType0(AxisType.TIME);
		// TODO?
		this.bufferWindow = null;
		this.maxValues = null;
	}

	/**
	 *
	 * @param page
	 * @param id
	 * @param globalWidget
	 * @param bufferWindow
	 * 		If this is non-null, a buffer will be used to store some derived data about the schedules,
	 * 		thereby reducing the overhead when reading schedule data in a request.
	 */
	public SchedulePlotlyjs(WidgetPage<?> page, String id, boolean globalWidget, Long bufferWindow) {
		super(page, id, globalWidget);
		getDefaultConfiguration().setXAxisType0(AxisType.TIME);
		this.bufferWindow = bufferWindow;
		if (bufferWindow != null) {
			if (bufferWindow < 0)
				throw new IllegalArgumentException("Buffer window must be non-negative, got " + bufferWindow);
			maxValues = new DefaultMaxValuesSupplier();
		}
		else {
			maxValues = null;
		}
//		getDefaultConfiguration().setXAxisType(AxisType.TIME);
	}

	/***** Inherited methods ******/

	@Override
	public SchedulePlotlyjsOptions createNewSession() {
		return new SchedulePlotlyjsOptions(this);
	}

	@Override
	public SchedulePlotlyjsOptions getData(OgemaHttpRequest req) {
		return (SchedulePlotlyjsOptions) super.getData(req);
	}

	@Override
	protected void setDefaultValues(PlotlyjsOptions opt) {
		super.setDefaultValues(opt);
		SchedulePlotlyjsOptions opt2 = (SchedulePlotlyjsOptions) opt;
		if (defaultSchedules != null)
			opt2.getScheduleData().setSchedules(defaultSchedules);
	}


	/****** Public methods ********/

	@Override
	public void setDefaultSchedules(Map<String,SchedulePresentationData> schedules) {
		this.defaultSchedules = schedules;
	}

	@Override
	public ScheduleDataPlotlyjs getScheduleData(OgemaHttpRequest req) {
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
