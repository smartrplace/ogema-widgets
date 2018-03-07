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
