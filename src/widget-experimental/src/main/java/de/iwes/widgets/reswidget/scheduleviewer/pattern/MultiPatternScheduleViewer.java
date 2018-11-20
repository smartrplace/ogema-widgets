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
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * Like a {@link PatternScheduleViewer}, except that multiple pattern types can be specified.
 */
public class MultiPatternScheduleViewer extends ScheduleViewerBasic<ReadOnlyTimeSeries> {

	private static final long serialVersionUID = 1L;
	private final static long MAX_UPDATE_INTERVAL = 10000; // do not update values more often than every 10s...
	private final List<Class<? extends ScheduleViewerPattern<?,?>>> patterns;
	private long lastUpdate = System.currentTimeMillis() - 2 * MAX_UPDATE_INTERVAL;
	private final CopyOnWriteArrayList<ReadOnlyTimeSeries> items = new CopyOnWriteArrayList<>();
	
	public MultiPatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, List<Class<? extends ScheduleViewerPattern<?,?>>> patterns) {
		this(page, id, am, null, null, patterns);
	}
	
	public MultiPatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, 
			ScheduleViewerConfiguration config, DisplayTemplate<ReadOnlyTimeSeries> displayTemplate, List<Class<? extends ScheduleViewerPattern<?,?>>> patterns) {
		super(page, id, am, config, displayTemplate);
		this.patterns = patterns;
	}

	@Override
	protected List<ReadOnlyTimeSeries> update(OgemaHttpRequest req) {
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
		List<ReadOnlyTimeSeries> newScheds = new ArrayList<>();
		for (Class<? extends ScheduleViewerPattern<?, ?>> pattern: patterns) {
			for (ScheduleViewerPattern<?, ?> match : am.getResourcePatternAccess().getPatterns(pattern, AccessPriority.PRIO_LOWEST)) {
				ReadOnlyTimeSeries schedule = match.getSchedule();
				if (!newScheds.contains(schedule))
					newScheds.add(schedule);
			}
		}
		items.retainAll(newScheds);
		items.addAllAbsent(newScheds);
		return items;
	}
	
	/**
	 * Not supported by MultiPatternScheduleViewer
	 */
	@Override
	public void setSchedules(Collection<ReadOnlyTimeSeries> schedules, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by MultiPatternScheduleViewer");
	}
	
	/**
	 * Not supported by PatternScheduleViewer
	 */
	@Override
	public void setDefaultSchedules(Collection<ReadOnlyTimeSeries> items) {
		throw new UnsupportedOperationException("Not supported by MultiPatternScheduleViewer");
	}
	
}
