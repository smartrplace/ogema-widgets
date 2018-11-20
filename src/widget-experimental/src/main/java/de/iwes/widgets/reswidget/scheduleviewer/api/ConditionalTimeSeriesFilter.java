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
