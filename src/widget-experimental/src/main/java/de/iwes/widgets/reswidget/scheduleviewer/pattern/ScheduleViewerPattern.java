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

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public abstract class ScheduleViewerPattern<R extends Resource, T extends ReadOnlyTimeSeries> extends ResourcePattern<R> {

	/**
	 * Note: a public constructor must be provided in the derived class.
	 * @param match
	 */
	protected ScheduleViewerPattern(Resource match) {
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

