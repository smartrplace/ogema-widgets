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
package org.ogema.tools.widgets.test.base.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ogema.tools.widgets.test.base.GenericWidget.HttpCallback;

public class RequestFuture implements Future<Void> {
	
	private final CountDownLatch latch;
	private final HttpCallback callback;
	
	public RequestFuture(CountDownLatch latch, HttpCallback callback) {
		this.latch = latch;
		this.callback = callback;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false; 
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return latch.getCount() == 0;
	}

	@Override
	public Void get() throws InterruptedException, ExecutionException {
		latch.await();
		Exception e = callback.getException();
		if (e != null)
			throw new ExecutionException(e);
		return null;
	}

	@Override
	public Void get(long timeout, TimeUnit unit)	throws InterruptedException, ExecutionException, TimeoutException {
		boolean fine = latch.await(timeout, unit);
		if (!fine)
			throw new TimeoutException("Timeout");
		Exception e = callback.getException();
		if (e != null)
			throw new ExecutionException(e);
		return null;

	}
	
}