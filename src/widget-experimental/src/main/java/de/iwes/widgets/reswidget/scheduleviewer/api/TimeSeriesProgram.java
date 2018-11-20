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
	 * @return As documented in {@link ScheduleViewerBasic} the elements of the list must be of type
	 * {@link Schedule}, {@link RecordedData} or {@link SchedulePresentationData}.
	 * Note that the latter can be provided by using or extending {@link DefaultSchedulePresentationData}.
	 * To control how the data is labeled it is recommended to provide elements of type SchedulePresentationData.
	 */
	List<ReadOnlyTimeSeries> timeSeries();
}
