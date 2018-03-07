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
package de.iwes.widgets.reswidget.scheduleviewer.api;

import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

public interface TimeSeriesFilterExtended extends TimeSeriesFilter {
	String shortName(ReadOnlyTimeSeries schedule);
	
	/**Provide long name to be displayed in schedule selector.
	 * @param schedule any time series in the return list {@link #timeSeriesAccepted()} */
	String longName(ReadOnlyTimeSeries schedule);
	
	/** Provide time series accepted by the filter. In this case also MemoryTimeSeries can be provided
	 * that would not be accessible for the ScheduleViewer otherwise.
	 * 
	 * @return list of time series for which {@link #accept(ReadOnlyTimeSeries) returns true}
	 */
	List<ReadOnlyTimeSeries> timeSeriesAccepted();
}
