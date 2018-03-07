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
