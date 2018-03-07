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

package de.iwes.widgets.reswidget.scheduleplot.api;

import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DDataSet;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public interface TimeSeriesPlot<D extends Plot2DDataSet,S extends ScheduleData<D>> extends OgemaWidget {
	
	public void setDefaultSchedules(Map<String,SchedulePresentationData> schedules);
	
	public S getScheduleData(OgemaHttpRequest req);
	
	/*
	 * Pass this through to schedule data
	 */
	/**
	 * Set the interval to be displayed.
	 * @param startTime
	 * @param endTime
	 * @throws IllegalArgumentException
	 * 		if endTime < startTime
	 */
	public void setInterval(long startTime, long endTime, OgemaHttpRequest req);

}
