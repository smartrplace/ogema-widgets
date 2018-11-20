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
package de.iwes.widgets.latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;

public class LatchWidgetData extends EmptyData {
	
	private CountDownLatch latch;

	public LatchWidgetData(LatchWidget empty) {
		super(empty);
	}
	
	public void reset(int expectedCounts) {
		writeLock();
		try {
			latch = new CountDownLatch(expectedCounts);
		} finally {
			writeUnlock();
		}
	}
	
	@Override
	public JSONObject onPOST(String data, OgemaHttpRequest req) throws UnsupportedOperationException {
		return new JSONObject();
	}
	
	public void reset(int expectedCounts, final long timeout, final TimeUnit unit) {
		final CountDownLatch local;
		writeLock();
		try {
			latch = new CountDownLatch(expectedCounts);
			local = latch;
		} finally {
			writeUnlock();
		}
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					boolean done = local.await(timeout, unit);
					while (!done) {
						local.countDown();
						done = local.getCount() <= 0;
					}
				} catch (InterruptedException ignore) {
					long cnt = local.getCount();
					while (cnt > 0) {
						local.countDown();
						cnt= local.getCount();
					}
				}
			}
		};
		Thread t = new Thread(r, "latchWidgetWaiter");
		t.start();
		
	}
 	
	
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		readLock();
		try {
			return latch.await(timeout, unit);
		} finally {
			readUnlock();
		}
	}
	
	public void countDown() {
		readLock();
		try {
			latch.countDown();
		} finally {
			readUnlock();
		}
	}
	
	public long getCount() {
		readLock();
		try {
			return latch.getCount();
		} finally {
			readUnlock();
		}
	}

}
