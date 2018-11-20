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

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;

/**
 * Type used for presentation of time series in a {@link ScheduleViewer} widget,
 * for use if the default representation of Schedules and RecordedData elements is not
 * sufficient, e.g. because a custom label shall be provided.<br>
 * It can also be used with MemoryTimeSeries, for which the default value type or a default
 * label could not be determined, otherwise.<br>
 * 
 * @see DefaultSchedulePresentationData default implementation
 */
public interface SchedulePresentationData extends ReadOnlyTimeSeries {
	
	/**
	 * Return a label. Use default language, if the selected one is unknown. 
	 * @return
	 * 		A string, no longer than thirty characters, no shorter than five characters.
	 */
	String getLabel(OgemaLocale locale);
	
	/**
	 * Specify the type of the schedule. 
	 * @return
	 * 		Either one of the wrapper classes for numerical Java primitives:<br>
	 * 		Float.class, Integer.class, Boolean.class, etc., or <br>
	 * 		corresponding OGEMA resource types:<br>
	 * 		FloatResource.class, TemperatureResource.class, IntegerResource.class, etc.
	 */
	Class<?> getScheduleType();
	
}
