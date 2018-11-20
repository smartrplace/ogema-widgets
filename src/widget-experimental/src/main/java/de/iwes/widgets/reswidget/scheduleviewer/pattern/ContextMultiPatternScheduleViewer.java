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

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * Like a {@link ContextPatternScheduleViewer}, except that multiple pattern types can be specified.
 * All patterns must share a common context class.
 * 
 * @param <C> the common context type for the pattern classes.
 * 
 * @see ScheduleViewerBasic
 * @see ContextPatternScheduleViewer
 * @see MultiPatternScheduleViewer
 */
public abstract class ContextMultiPatternScheduleViewer<C> extends ScheduleViewerBasic<ReadOnlyTimeSeries> {

	private static final long serialVersionUID = 1L;
	private final List<Class<? extends ScheduleViewerContextPattern<?,?,C>>> patterns;
	
	public ContextMultiPatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, List<Class<? extends ScheduleViewerContextPattern<?,?,C>>> patterns) {
		this(page, id, am, null, null, patterns);
	}
	
	public ContextMultiPatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, 
			ScheduleViewerConfiguration config, DisplayTemplate<ReadOnlyTimeSeries> displayTemplate, List<Class<? extends ScheduleViewerContextPattern<?,?,C>>> patterns) {
		super(page, id, am, config, displayTemplate);
		this.patterns = patterns;
	}

	/**
	 * Associate a context object to the user session.
	 * @param req
	 * @return
	 */
	protected abstract C getContext(OgemaHttpRequest req);

	@Override
	protected List<ReadOnlyTimeSeries> update(OgemaHttpRequest req) {
		List<ReadOnlyTimeSeries> newScheds = new ArrayList<>();
		C context = getContext(req);
		for (Class<? extends ScheduleViewerContextPattern<?, ?, C>> pattern: patterns) {
			for (ScheduleViewerContextPattern<?, ?, C> match : am.getResourcePatternAccess().getPatterns(pattern, AccessPriority.PRIO_LOWEST, context)) {
				ReadOnlyTimeSeries schedule = match.getSchedule();
				if (!newScheds.contains(schedule))
					newScheds.add(schedule);
			}
		}
		return newScheds;
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
