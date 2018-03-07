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

package de.iwes.widgets.reswidget.scheduleplot.c3;

import java.util.Map;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plotc3.C3DataSet;
import de.iwes.widgets.html.plotc3.PlotC3;
import de.iwes.widgets.html.plotc3.PlotC3Options;
import de.iwes.widgets.reswidget.scheduleplot.api.ScheduleData;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public class SchedulePlotC3 extends PlotC3 implements TimeSeriesPlot<C3DataSet, ScheduleDataC3>{
	
	private static final long serialVersionUID = 1L;
	private Map<String,SchedulePresentationData> defaultSchedules = null;
	
	/****** Constructor *******/
	
	public SchedulePlotC3(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	/***** Inherited methods ******/
	
	@Override
	public SchedulePlotC3Options createNewSession() {
		return new SchedulePlotC3Options(this);
	}
	
	@Override
	public SchedulePlotC3Options getData(OgemaHttpRequest req) {
		return (SchedulePlotC3Options) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(PlotC3Options opt) {
		super.setDefaultValues(opt);
		SchedulePlotC3Options opt2 = (SchedulePlotC3Options) opt;
		if (defaultSchedules != null)
			opt2.getScheduleData().setSchedules(defaultSchedules);
	}
	
	
	/****** Public methods ********/
	
	@Override
	public void setDefaultSchedules(Map<String, SchedulePresentationData> schedules) {
		this.defaultSchedules = schedules;
	}

	@Override
	public ScheduleDataC3 getScheduleData(OgemaHttpRequest req) {
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
