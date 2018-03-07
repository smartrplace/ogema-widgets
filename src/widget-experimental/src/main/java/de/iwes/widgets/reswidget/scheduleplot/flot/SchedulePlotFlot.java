/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.reswidget.scheduleplot.flot;

import java.util.Map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotflot.FlotConfiguration.AxisType;
import de.iwes.widgets.html.plotflot.FlotDataSet;
import de.iwes.widgets.html.plotflot.PlotFlot;
import de.iwes.widgets.html.plotflot.PlotFlotOptions;
import de.iwes.widgets.reswidget.scheduleplot.api.MaxValBuffer;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public class SchedulePlotFlot extends PlotFlot implements TimeSeriesPlot<FlotDataSet, ScheduleDataFlot> {
	
	private static final long serialVersionUID = 1L;
	private Map<String,SchedulePresentationData> defaultSchedules = null;
	final Cache<String, MaxValBuffer> maxValues;
	final Long bufferWindow;

	
	/****** Constructor *******/
	
	public SchedulePlotFlot(WidgetPage<?> page, String id, boolean globalWidget) {
		this(page, id, globalWidget, null);
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
	public SchedulePlotFlot(WidgetPage<?> page, String id, boolean globalWidget, Long bufferWindow) {
		super(page, id, globalWidget);
		this.bufferWindow = bufferWindow;
		if (bufferWindow != null) {
			if (bufferWindow < 0) 
				throw new IllegalArgumentException("Buffer window must be non-negative, got " + bufferWindow);
			maxValues = CacheBuilder.newBuilder().softValues().build();
		}
		else {
			maxValues = null;
		}
		getDefaultConfiguration().setXAxisType(AxisType.TIME);
	}

	/***** Inherited methods ******/
	
	@Override
	public SchedulePlotFlotOptions createNewSession() {
		return new SchedulePlotFlotOptions(this);
	}
	
	@Override
	public SchedulePlotFlotOptions getData(OgemaHttpRequest req) {
		return (SchedulePlotFlotOptions) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(PlotFlotOptions opt) {
		super.setDefaultValues(opt);
		SchedulePlotFlotOptions opt2 = (SchedulePlotFlotOptions) opt;
		if (defaultSchedules != null)
			opt2.getScheduleData().setSchedules(defaultSchedules);
	}
	
	
	/****** Public methods ********/
	
	@Override
	public void setDefaultSchedules(Map<String,SchedulePresentationData> schedules) {
		this.defaultSchedules = schedules;
	}

	@Override
	public ScheduleDataFlot getScheduleData(OgemaHttpRequest req) {
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
