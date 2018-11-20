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
package de.iwes.widgets.reswidget.scheduleviewer.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewer;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A version of the {@link ScheduleViewer} that determines the schedules to be displayed based on 
 * matches for a specific {@link ResourcePattern}  class. It shows one schedule per pattern match.
 * Another variant exists, which allows multiple pattern classes to be specified, see {@link MultiPatternScheduleViewer}.
 * 
 * @param <T>
 * 
 * @see ScheduleViewerBasic
 * @see MultiPatternScheduleViewer
 */
public class PatternScheduleViewer<P extends ScheduleViewerPattern<?, T>, T extends ReadOnlyTimeSeries> extends ScheduleViewerBasic<T> {

	private static final long serialVersionUID = 1L;
	private final static long MAX_UPDATE_INTERVAL = 5000; // do not update values more often than every 5s...
	private final Class<P> pattern;
	private long lastUpdate = System.currentTimeMillis() - 2 * MAX_UPDATE_INTERVAL;
	private final CopyOnWriteArrayList<T> items = new CopyOnWriteArrayList<>();
	
	public PatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, Class<P> pattern) {
		this(page, id, am, null, null, pattern);
	}
	
	public PatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, ScheduleViewerConfiguration config, DisplayTemplate<T> displayTemplate, Class<P> pattern) {
		super(page, id, am, config, displayTemplate);
		this.pattern = pattern;
	}
	
	@Override
	protected List<T> update(OgemaHttpRequest req) {
		long now = System.currentTimeMillis();
		boolean cancelUpdate = false;
		synchronized (this) {
			if (now-lastUpdate < MAX_UPDATE_INTERVAL)
				cancelUpdate = true;
			else
				lastUpdate = now;
		}
		if (cancelUpdate)
			return items;
		List<T> newScheds = new ArrayList<>();
		for (P match : am.getResourcePatternAccess().getPatterns(pattern, AccessPriority.PRIO_LOWEST)) {
			newScheds.add(match.getSchedule());
		}
		items.retainAll(newScheds);
		items.addAllAbsent(newScheds);
		return items;
	}
	
	/**
	 * Not supported by PatternScheduleViewer
	 */
	@Override
	public void setSchedules(Collection<T> schedules, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by PatternScheduleViewer");
	}
	
	/**
	 * Not supported by PatternScheduleViewer
	 */
	@Override
	public void setDefaultSchedules(Collection<T> items) {
		throw new UnsupportedOperationException("Not supported by PatternScheduleViewer");
	}
	
}
