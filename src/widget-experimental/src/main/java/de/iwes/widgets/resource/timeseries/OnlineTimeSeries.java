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

import java.util.Iterator;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import com.google.common.annotations.Beta;

@Beta
public interface OnlineTimeSeries extends ReadOnlyTimeSeries {
	
	/**
	 * returns the current resource value as a {@link SampledValue}. If the resource 
	 * is inactive, a bad quality value is returned.
	 * @return
	 */
	SampledValue getCurrentValue();

	/**
	 * Returns an iterator for future data points. The iterator will either wait
	 * for value changes in the underlying resource before generating a new value
	 * (in this case {@link Iterator#hasNext()} will block), or simply generate a new
	 * data point from the current resource value every time {@link Iterator#next()}
	 * is called. 
	 * 
	 * @param blocking
	 * 		true: hasNext blocks until a new resource value is available<br>
	 * 		false: hasNext simply returns true, and next generates a new data point
	 * 			every time it is called.
	 * @return
	 */
	OnlineIterator onlineIterator(boolean blocking);
	
	/**
	 * @param blocking
	 * @param endTime
	 * 		a timestamp in the future; after that time the iterator will stop generating new
	 * 		data points
	 * @return
	 * @see #onlineIterator(boolean)
	 */
	OnlineIterator onlineIterator(boolean blocking, long endTime);
	
	/**
	 * Returns an iterator over the set of cached values. Contrary to 
	 * {@link #onlineIterator(boolean)}, this does not generate any new
	 * data points, and hence is never blocking.
	 * 
	 * @see ReadOnlyTimeSeries#iterator()
	 */
	@Override
	Iterator<SampledValue> iterator();
	
	/**
	 * Returns an iterator over the set of cached values. Contrary to 
	 * {@link #onlineIterator(boolean)}, this does not generate any new
	 * data points, and hence is never blocking.
	 * 
	 * @see ReadOnlyTimeSeries#iterator(long, long)
	 */
	@Override
	Iterator<SampledValue> iterator(long startTime, long endTime);
	
	/**
	 * Get the configuration for the values cache. Changes to the configuration
	 * object will immediately take effect. 
	 * @return
	 */
	OnlineTimeSeriesConfiguration getConfiguration();
	
	/**
	 * The resource whose values are sampled
	 * @return
	 */
	SingleValueResource getResource();
	
}
