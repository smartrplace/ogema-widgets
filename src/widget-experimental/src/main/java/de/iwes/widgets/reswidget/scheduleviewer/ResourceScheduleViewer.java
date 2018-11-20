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
package de.iwes.widgets.reswidget.scheduleviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.schedule.Schedule;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewer;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.pattern.MultiPatternScheduleViewer;
import de.iwes.widgets.reswidget.scheduleviewer.pattern.PatternScheduleViewer;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A version of the {@link ScheduleViewer} for schedules only (instead of general ReadOnlyTimeSeries),
 * that determines the schedules to be shown itself, by querying the OGEMA resource database. A custom
 * filter can be defined (see method {@link #accept(Schedule)}). If a more complex filter is required,
 * consider using a {@link PatternScheduleViewer} or {@link MultiPatternScheduleViewer} instead.
 * 
 * @param <T>
 * 
 * @see ScheduleViewerBasic
 * @see PatternScheduleViewer
 */
public class ResourceScheduleViewer<T extends Schedule> extends ScheduleViewerBasic<T> {

	private static final long serialVersionUID = 1L;
	protected final Class<T> type;
	private final static long MAX_UPDATE_INTERVAL = 5000; // do not update values more often than every 5s...
	private long lastUpdate = System.currentTimeMillis() - 2 * MAX_UPDATE_INTERVAL;
	private final CopyOnWriteArrayList<T> items = new CopyOnWriteArrayList<>();
	
	public ResourceScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, Class<T> type) {
		this(page, id, am, null, null, type);
	}
	
	public ResourceScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am,	ScheduleViewerConfiguration config, DisplayTemplate<T> displayTemplate, Class<T> type) {
		super(page, id, am, config, displayTemplate);
		this.type = type;
	}
	
	/**
	 * Override to filter out certain resources, for instance based on their name, type, or parent.
	 */
	protected boolean accept(T resource) {
		return true;
	}

	@Override
	protected List<T> update(OgemaHttpRequest req) {
		long now = System.currentTimeMillis();
		final boolean cancelUpdate;
		synchronized (this) {
			if (now-lastUpdate < MAX_UPDATE_INTERVAL)
				cancelUpdate = true;
			else {
				lastUpdate = now;
				cancelUpdate = false;
			}
		}
		if (cancelUpdate)
			return items;
		List<T> newScheds = new ArrayList<>();
		for (T schedule : am.getResourceAccess().getResources(type)) {
			if (accept(schedule)) 
				newScheds.add(schedule);
		}
		items.retainAll(newScheds);
		items.addAllAbsent(newScheds);
		return items;
	}
	
	/**
	 * Not supported by ResourceScheduleViewer
	 */
	@Override
	public void setSchedules(Collection<T> schedules, OgemaHttpRequest req) {
		throw new UnsupportedOperationException("Not supported by ResourceScheduleViewer");
	}
	
	/**
	 * Not supported by ResourceScheduleViewer
	 */
	@Override
	public void setDefaultSchedules(Collection<T> items) {
		throw new UnsupportedOperationException("Not supported by ResourceScheduleViewer");
	}
	
}
