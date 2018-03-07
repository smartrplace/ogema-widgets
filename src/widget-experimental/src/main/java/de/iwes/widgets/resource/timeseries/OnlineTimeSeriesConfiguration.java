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

import com.google.common.annotations.Beta;

@Beta
public class OnlineTimeSeriesConfiguration {

	public static final int MAX_ALLOWED_CACHE_SIZE = 10000;
	private volatile long minCacheDuration = 2 * 60 * 60 * 1000; // 2h
	private volatile int maxCacheSize = 1000;

	public long getMinCacheDuration() {
		return minCacheDuration;
	}
	
	/**
	 * Set the duration (in ms) after which the points cache shall be cleared.
	 * Note: it is not guaranteed that the cache will survive the specified duration.
	 * @return
	 */
	public void setMinCacheDuration(long minCacheDuration) {
		this.minCacheDuration = minCacheDuration;
	}
	
	/**
	 * Return the number of data points which the cache shall not exceed.
	 * @return
	 */
	public int getMaxCacheSize() {
		return maxCacheSize;
	}
	
	/**
	 * Specify the number of data points which the cache shall not exceed.
	 * @return
	 */
	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}
	
	
	
	
}
