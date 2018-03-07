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

package de.iwes.widgets.api.extended.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

class WidgetThreadFactory implements ThreadFactory {

	// FIXME what happens to the thread group when this class is removed?
	private final static ThreadGroup WIDGET_THREADS = new ThreadGroup("widget.threads");
	private final static AtomicLong threadCount = new AtomicLong(); // counts all sessions, not per page
	private final static WidgetThreadFactory instance = new WidgetThreadFactory();

	static WidgetThreadFactory getInstance() {
		return instance;
	}
	
	private WidgetThreadFactory() {
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(WIDGET_THREADS, r, "WidgetThread_"+ threadCount.incrementAndGet());
	}
	
	public static int activeCount() {
		return WIDGET_THREADS.activeCount();
	}
		
}
