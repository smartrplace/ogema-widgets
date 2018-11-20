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

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;

/**
 * A wrapper for {@link ReadOnlyTimeSeries}, which allows to specify 
 * certain modifications of the original time series without actually 
 * modifying it. For instance, an interpolation mode different from the 
 * original mode of the time series can be set.<br>
 * Instances of this type serve as input data for time series evaluations.
 * <br>
 * Note: this can be applied to online data as well, the name is totally misleading.
 * See {@link OnlineTimeSeriesCache} for how to create a 
 * {@link ReadOnlyTimeSeries} for online data.
 */
public interface TimeSeriesDataOffline extends TimeSeriesData {

	/**
	 * The wrapped time series.
	 * @return
	 */
	ReadOnlyTimeSeries getTimeSeries();
	
	/**
	 * Offset each entry of the time series by some duration 
	 * @return
	 * 
	 */
	// TODO implement
	long timeOffset();
}
