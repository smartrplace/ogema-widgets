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