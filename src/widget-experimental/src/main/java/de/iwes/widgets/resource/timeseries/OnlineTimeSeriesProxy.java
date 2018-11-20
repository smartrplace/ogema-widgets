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

import java.util.Collections;
import java.util.Objects;
import java.util.WeakHashMap;

import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.tools.resource.util.ValueResourceUtils;
import org.ogema.tools.timeseries.api.MemoryTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.timeseries.implementations.SynchronizedTimeSeries;

class OnlineTimeSeriesProxy {
	
	final MemoryTimeSeries timeSeries = new SynchronizedTimeSeries(new FloatTreeTimeSeries());
	final FrameworkClock clock;
	final SingleValueResource resource;
	private final OnlineValueListener listener;
	private static final Object o = new Object();
	// synchronized on itself
	private final WeakHashMap<OnlineIteratorBlocking, Object> iterators = new WeakHashMap<>();
	// TODO not considered yet
	final OnlineTimeSeriesConfiguration config;
	
	public OnlineTimeSeriesProxy(SingleValueResource resource, FrameworkClock clock) {
		Objects.requireNonNull(resource);
		Objects.requireNonNull(clock);
		if (resource instanceof StringResource)
			throw new IllegalArgumentException("Time series of Strings not supported: " +resource );
		this.resource = resource;
		this.listener = new OnlineValueListener(resource, timeSeries, clock, iterators);
		resource.addValueListener(listener, true);
		this.clock = clock;
		this.config = new OnlineTimeSeriesConfiguration();
	}
	
	/**
	 * 
	 * @param onlineIterator
	 * @param blockingIterator
	 * @param maxBlockingTime
	 * @param cacheValues
	 * @return
	 */
//	ReadOnlyTimeSeries getTimeSeries() {
//		return new OnlineTimeSeriesImpl(this);
//	}
	
	public OnlineIterator getOnlineIterator(boolean blocking, long endTime) {
		final OnlineIterator iterator = blocking ? new OnlineIteratorBlocking(resource, clock, endTime) : new OnlineIteratorNonBlocking(resource, clock, endTime);
		if (blocking) {
			synchronized (iterators) {
				iterators.put((OnlineIteratorBlocking) iterator, o);
			}
		}
		return iterator;
	}
	
	@Override
	protected synchronized void finalize() throws Throwable {
		if (listener != null) {
			resource.removeValueListener(listener);
		}
		synchronized (iterators) {
			for (OnlineIteratorBlocking oib : iterators.keySet())
				oib.stop();
		}
	}
	
	SampledValue generateValue() {
		return listener.generateValue();
	}
	
	/*
	 * Class must be static, so that it does not keep a reference to the OnlineTimeSeriesProxy object,
	 * and the latter can be garbage collected. 
	 */
	final static class OnlineValueListener implements ResourceValueListener<SingleValueResource> {
		
		private final MemoryTimeSeries timeSeries;
		private final FrameworkClock clock;
		private final WeakHashMap<OnlineIteratorBlocking, Object> blockingIterators;
		private final SingleValueResource resource;
		
		public OnlineValueListener(SingleValueResource resource, MemoryTimeSeries timeSeries, FrameworkClock clock, 
					WeakHashMap<OnlineIteratorBlocking, Object> blockingIterators) {
			this.resource = resource;
			this.timeSeries = timeSeries;
			this.clock = clock;
			this.blockingIterators = blockingIterators;
		}
		
		private final SampledValue generateValue() {
			final float f = ValueResourceUtils.getFloatValue(resource);
			final boolean nan = Float.isNaN(f);
			return new SampledValue(f == 0F ? FloatValue.ZERO : nan ? FloatValue.NAN : new FloatValue(f), 
					clock.getExecutionTime(), (nan || !resource.isActive()) ? Quality.BAD : Quality.GOOD);
		}

		@Override
		public void resourceChanged(SingleValueResource resource) {
			final SampledValue sv = generateValue();
			timeSeries.addValues(Collections.singletonList(sv));
			// since callbacks are issued in the app thread, chronological ordering is guaranteed
			synchronized (blockingIterators) {
				for (final OnlineIteratorBlocking oib : blockingIterators.keySet()) {
					oib.trigger(sv);
				}
			}
		}
		
	}
	
}
