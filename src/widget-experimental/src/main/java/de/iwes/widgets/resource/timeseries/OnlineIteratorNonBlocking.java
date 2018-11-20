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

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import com.google.common.annotations.Beta;

@Beta
class OnlineIteratorNonBlocking implements OnlineIterator {
	
	private final SingleValueResource resource;
	private final FrameworkClock clock;
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private volatile SampledValue last = null;
	private final long end;
	 
	public OnlineIteratorNonBlocking(SingleValueResource resource, FrameworkClock clock, long endTime) {
		this.resource = resource;
		this.clock = clock;
		this.end = endTime;
	}
	
	@Override
	public void stop() {
		if (closed.getAndSet(true))
			return;
		last = next();
	}

	@Override
	public boolean hasNext() {
		if (!closed.get() && end < clock.getExecutionTime())
			stop();
		return !closed.get() || last != null;
	}

	@Override
	public SampledValue next() {
		if (closed.get()) {
			if (last != null) {
				SampledValue lastLocal = last;
				last = null;
				return lastLocal;
			}
			throw new NoSuchElementException();
		}
		final float val = ValueResourceUtils.getFloatValue(resource);
		return new SampledValue(new FloatValue(val), clock.getExecutionTime(), Float.isNaN(val) || !resource.isActive() ? Quality.BAD : Quality.GOOD);
	}

	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	
}
