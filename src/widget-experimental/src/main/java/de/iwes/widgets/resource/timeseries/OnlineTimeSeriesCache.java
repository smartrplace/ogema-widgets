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
package de.iwes.widgets.resource.timeseries;

import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.InterpolationMode;

import com.google.common.annotations.Beta;

@Beta
public interface OnlineTimeSeriesCache {
	
	/**
	 * Get a time series representation of the resource values. New resource values will be
	 * cached in a time series, similar to the OGEMA log data, except that the time series is non-persistent. 
	 *
	 * @param resource
	 * @return
	 * 		a time series with interpolation mode {@link InterpolationMode#NONE}
	 */
//	ReadOnlyTimeSeries getResourceValuesAsTimeSeries(SingleValueResource resource);
	
	/**
	 * Get a time series representation of the resource values. New resource values will be
	 * cached in a time series, similar to the OGEMA log data, except that the time series is non-persistent. 
	 * 
	 * @param resource
	 * @return
	 * 		a time series with interpolation mode {@link InterpolationMode#NONE}
	 */
	OnlineTimeSeries getResourceValuesAsTimeSeries(SingleValueResource resource);
	
	
	

}
