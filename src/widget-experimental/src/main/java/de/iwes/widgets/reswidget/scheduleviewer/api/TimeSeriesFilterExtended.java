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
package de.iwes.widgets.reswidget.scheduleviewer.api;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

public interface TimeSeriesFilterExtended extends TimeSeriesFilter {
	
	String shortName(ReadOnlyTimeSeries schedule);
	
	/**Provide long name to be displayed in schedule selector.
	 * @param schedule any time series
	 */
	String longName(ReadOnlyTimeSeries schedule);
	
	/** May be null if not provided*/
	Class<?> type(ReadOnlyTimeSeries schedule);
	/** Provide time series accepted by the filter. In this case also MemoryTimeSeries can be provided
	 * that would not be accessible for the ScheduleViewer otherwise.
	 * 
	 * @return list of time series for which {@link #accept(ReadOnlyTimeSeries) returns true}
	 */
	//List<ReadOnlyTimeSeries> timeSeriesAccepted();
}
