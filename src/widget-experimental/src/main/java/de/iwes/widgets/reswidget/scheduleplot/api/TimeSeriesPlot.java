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
package de.iwes.widgets.reswidget.scheduleplot.api;

import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.plot.api.Plot2DConfiguration;
import de.iwes.widgets.html.plot.api.Plot2DDataSet;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public interface TimeSeriesPlot<C extends Plot2DConfiguration, D extends Plot2DDataSet,S extends ScheduleData<D>> extends OgemaWidget {

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
	 * 		if endTime &lt; startTime
	 */
	public void setInterval(long startTime, long endTime, OgemaHttpRequest req);

	public C getDefaultConfiguration();

	public C getConfiguration(OgemaHttpRequest req);

}
