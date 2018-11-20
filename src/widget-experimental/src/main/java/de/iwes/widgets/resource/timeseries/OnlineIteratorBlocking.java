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

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.tools.resource.util.ValueResourceUtils;

import com.google.common.annotations.Beta;

@Beta
class OnlineIteratorBlocking implements OnlineIterator {
	
	private final Queue<SampledValue> queue = new ArrayDeque<>();
//	private final OnlineIteratorListener listener;
	private boolean closed = false;
	private SampledValue last;
	private final long endTime;
	private final FrameworkClock clock;
	 
	public OnlineIteratorBlocking(SingleValueResource resource, FrameworkClock clock, long endTime) {
		this.endTime = endTime;
		this.clock = clock;
		final float val = ValueResourceUtils.getFloatValue(resource);
		final SampledValue sv = new SampledValue(new FloatValue(val),clock.getExecutionTime(), 
				Float.isNaN(val) || !resource.isActive() ? Quality.BAD : Quality.GOOD);
		trigger(sv);
//		this.listener = new OnlineIteratorListener(queue,clock);
//		listener.resourceChanged(resource); // make sure the first hasNext call does not block
//		resource.addValueListener(listener);
	}
	
	@Override
	public void stop() {
		synchronized (queue) {
			if (closed)
				return;
			closed = true;
			last = queue.poll();
			queue.clear();
			queue.notifyAll();
		}
//		resource.removeValueListener(listener);
	}

	@Override
	public boolean hasNext() {
		synchronized (queue) {
			while (!closed && endTime > clock.getExecutionTime() && queue.peek() == null) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					return false;
				}
			}
			if (!closed && endTime < clock.getExecutionTime())
				stop();
			if (closed)
				return last != null;
		}
		return true;
	}

	@Override
	public SampledValue next() {
		synchronized (queue) {
			if (closed) {
				if (last == null)
					throw new NoSuchElementException("No further elements");
				final SampledValue lastLocal = last;
				last = null;
				return lastLocal;
			}
			return queue.poll();
		}
	}

	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	void trigger(SampledValue sv) {
		synchronized (queue) {
			queue.offer(sv);
			queue.notify();
		}
	}
	
	
	// -> now using a single listener for all iterators
	// must be static so it does not hold a reference to the iterator, which would prevent it from being collected
//	private final static class OnlineIteratorListener implements ResourceValueListener<SingleValueResource> {
//		
//		private final Queue<SampledValue> queue;
//		private final FrameworkClock clock;
//		
//		public OnlineIteratorListener(Queue<SampledValue> queue, FrameworkClock clock) {
//			this.queue = queue;
//			this.clock = clock;
//		}
//
//		@Override
//		public void resourceChanged(SingleValueResource resource) {
//			final float val = ValueResourceUtils.getFloatValue(resource);
//			final SampledValue sv = new SampledValue(new FloatValue(val),clock.getExecutionTime(), Float.isNaN(val) || !resource.isActive() ? Quality.BAD : Quality.GOOD);
//			synchronized (queue) {
//				queue.offer(sv);
//				queue.notify();
//			}
//		}
//		
//	}
	
}
