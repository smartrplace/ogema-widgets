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
