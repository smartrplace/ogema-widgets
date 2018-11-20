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
	 */
	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}
	
	
	
	
}
