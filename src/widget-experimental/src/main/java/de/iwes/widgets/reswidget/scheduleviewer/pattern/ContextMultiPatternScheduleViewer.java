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
