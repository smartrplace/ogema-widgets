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
package de.iwes.timeseries.eval.viewer.api;

import java.util.Collection;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.LabelledItem;

/**
 * A profile is a filter for time series.
 * This is a refined version of the ... class
 */
public interface Profile extends LabelledItem {

	/**
	 * A default interpolation mode for schedules belonging to this profile
	 * @return
	 * 		may return null, in which case the mode of the selected timeseries is used
	 */
	InterpolationMode defaultInterpolationMode();
	
	/**
	 * Return true iff the passed time series is applicable to this profile
	 * @param timeseries
	 * @return
	 */
	boolean accept(ReadOnlyTimeSeries timeseries);
	
	/**
	 * Specify how time series of this profile have to be aggregated.
	 * @param constituents
	 * @param labelAddOn
	 * 		may be null
	 * @return
	 */
	ProfileSchedulePresentationData aggregate(Collection<ReadOnlyTimeSeries> constituents, String labelAddOn);
	
}
