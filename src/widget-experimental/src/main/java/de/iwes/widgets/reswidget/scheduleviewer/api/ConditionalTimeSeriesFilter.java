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

import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

public interface ConditionalTimeSeriesFilter<P extends ResourcePattern<?>> extends TimeSeriesFilter {

	/**
	 * Simply returns the pattern class.
	 * @return
	 */
	Class<P> getPatternClass();
	
//	/**
//	 * Return a short, descriptive name for the pattern demanded model. 
//	 * @param locale
//	 * @return
//	 */
//	String getPatternLabel(OgemaLocale locale);
	
	/**
	 * Determines whether a time series is applicable to the selected pattern instance.
	 * @param schedule
	 * @param instance
	 * @return
	 */
	boolean accept(ReadOnlyTimeSeries schedule, P instance);
	
}
