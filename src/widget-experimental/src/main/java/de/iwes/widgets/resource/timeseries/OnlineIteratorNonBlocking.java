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
