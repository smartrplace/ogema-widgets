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

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public abstract class ScheduleViewerContextPattern<R extends Resource, T extends ReadOnlyTimeSeries, C> extends ContextSensitivePattern<R,C> {

	/**
	 * Note: a public constructor must be provided in the derived class.
	 * @param match
	 */
	protected ScheduleViewerContextPattern(Resource match) {
		super(match);
	}

	/**
	 * Associate a schedule to this pattern.
	 * @return
	 */
	public abstract T getSchedule();
	
	/**
	 * Override in derived class to provide a custom label for this pattern.
	 * @param locale
	 * @return
	 */
	public String getLabel(OgemaLocale locale) {
		return null;
	}
	
}

