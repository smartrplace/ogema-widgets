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

import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;

/** A set of time series ("program") with id and label to be presented to the user
 * Not used anymore (?)*/
@Deprecated
public interface TimeSeriesProgram { //extends LabelledItem {
	
	/**
	 * A unique id
	 * @return
	 */

	String id();
	
	/**
	 * A label for display in the user interface for the program
	 * @param locale
	 * @return
	 */
	String label(OgemaLocale locale);
	
	/** The time series that shall be presented with this offer
	 * Determines whether the time series is selected or not, when this program (filter) 
	 * is applied.
	 * @param schedule
	 * @return As documented in {@link ScheduleViewerBasic} the elements of the list must be of type
	 * {@link Schedule}, {@link RecordedData} or {@link SchedulePresentationData}.
	 * Note that the latter can be provided by using or extending {@link DefaultSchedulePresentationData}.
	 * To control how the data is labeled it is recommended to provide elements of type SchedulePresentationData.
	 */
	List<ReadOnlyTimeSeries> timeSeries();
}
