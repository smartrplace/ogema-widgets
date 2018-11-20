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
package de.iwes.timeseries.eval.api;

import org.ogema.core.timeseries.InterpolationMode;

/**
 * Common specification of offline or online time series input
 */
public interface TimeSeriesData extends LabelledItem {

	/**
	 * May be null, in which case the interpolation mode
	 * of the time series is used. This allows to overwrite 
	 * the explicit time series interpolation mode.
	 * @return
	 */
	InterpolationMode interpolationMode();

	/**
	 * offset each value of the time series by a specific 
	 * value
	 * @return
	 */
	// TODO implement
	float offset();
	
	/**
	 * multiply each value of the time series by a factor
	 * @return
	 */
	// TODO implement
	float factor();
}
