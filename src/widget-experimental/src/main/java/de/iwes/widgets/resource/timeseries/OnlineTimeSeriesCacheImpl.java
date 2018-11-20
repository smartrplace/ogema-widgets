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

import java.util.Objects;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.model.simple.SingleValueResource;

import com.google.common.annotations.Beta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Beta
@Service(OnlineTimeSeriesCache.class)
@Component
public class OnlineTimeSeriesCacheImpl implements OnlineTimeSeriesCache {

	@Reference
	private FrameworkClock clock;
	
	private final Cache<SingleValueResource, OnlineTimeSeriesProxy> cache = CacheBuilder.newBuilder()
			.softValues()
			.build();
	
	@Override
	public OnlineTimeSeries getResourceValuesAsTimeSeries(SingleValueResource resource) {
		Objects.requireNonNull(resource);
		return new OnlineTimeSeriesImpl(getProxy(resource), resource);
	}
	
	private final OnlineTimeSeriesProxy getProxy(final SingleValueResource resource) {
		OnlineTimeSeriesProxy proxy = cache.getIfPresent(resource);
		if (proxy == null) { 
			synchronized (cache) {
				proxy = cache.getIfPresent(resource);
				if (proxy == null) {
					proxy = new OnlineTimeSeriesProxy(resource, clock);
					cache.put(resource, proxy);
				}
			}
		}
		return proxy;
	}
	
}
