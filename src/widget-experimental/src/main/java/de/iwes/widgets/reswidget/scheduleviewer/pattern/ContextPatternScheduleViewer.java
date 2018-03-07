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
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewer;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A version of the {@link ScheduleViewer} that determines the schedules to be displayed based on 
 * matches for a specific {@link ContextSensitivePattern}  class. It shows one schedule per pattern match, and
 * the context is associated to the user session.
 * Another variant exists, which allows multiple pattern classes to be specified, see {@link MultiPatternScheduleViewer}.
 * 
 * @param <T> time series type
 * @param <P> pattern type
 * @param <C> context type
 * 
 * @see ScheduleViewerBasic
 * @see MultiPatternScheduleViewer
 * 
 */
// XXX unfortunately, this cannot be derived from PatternScheduleViewer, because the ScheduleViewerContextPattern type cannot extend the basic ScheduleViewerPattern
public abstract class ContextPatternScheduleViewer<P extends ScheduleViewerContextPattern<?, T, C>, T extends ReadOnlyTimeSeries,C> extends ScheduleViewerBasic<T> {

	private static final long serialVersionUID = 1L;
	private final Class<P> pattern;
	
	public ContextPatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, Class<P> pattern) {
		this(page, id, am, null, null, pattern);
	}
	
	public ContextPatternScheduleViewer(WidgetPage<?> page, String id, ApplicationManager am, ScheduleViewerConfiguration config, DisplayTemplate<T> displayTemplate, Class<P> pattern) {
		super(page, id, am, config, displayTemplate);
		this.pattern = pattern;
	}

	/**
	 * Associate a context object to the user session.
	 * @param req
	 * @return
	 */
	protected abstract C getContext(OgemaHttpRequest req);
	
	@Override
	protected List<T> update(OgemaHttpRequest req) {
		C context = getContext(req);
		List<T> newScheds = new ArrayList<>();
		for (P match : am.getResourcePatternAccess().getPatterns(pattern, AccessPriority.PRIO_LOWEST, context)) {
			newScheds.add(match.getSchedule());
		}
		return newScheds;
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
