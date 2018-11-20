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

import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.template.DisplayTemplate;
import de.iwes.widgets.template.LabelledItem;

public class DefaultTimeSeriesDisplayTemplate<T extends ReadOnlyTimeSeries> implements DisplayTemplate<T> {

	private final NameService nameService;
	
	public DefaultTimeSeriesDisplayTemplate(NameService nameService) {
		this.nameService = nameService;
	}
	
	@Override
	public String getId(T schedule) {
		if (schedule instanceof Schedule)
			return ((Schedule) schedule).getPath();
		if (schedule instanceof RecordedData)
			return ((RecordedData) schedule).getPath();
		if (schedule instanceof SchedulePresentationData)
			return ResourceUtils.getValidResourceName(((SchedulePresentationData) schedule).getLabel(OgemaLocale.ENGLISH));
		if (schedule instanceof OnlineTimeSeries)
			return ((OnlineTimeSeries) schedule).getResource().getPath();
		if (schedule instanceof LabelledItem)
			return ((LabelledItem) schedule).id();
		throw new IllegalArgumentException("Could not determine schedule id for time series " + schedule +
				". Please provide a custom DisplayTemplate.") ;
	}

	@Override
	public String getLabel(T schedule, OgemaLocale locale) {
		if (schedule instanceof SchedulePresentationData) 
			return ((SchedulePresentationData) schedule).getLabel(locale);
		if (schedule instanceof Schedule) {
			if (nameService != null) {
				String name = nameService.getName((Schedule) schedule, locale);
				if (name != null)
					return name;
			}
			return ResourceUtils.getHumanReadableName((Schedule) schedule);
		}
		if (schedule instanceof RecordedData)
			return ((RecordedData) schedule).getPath();
		if (schedule instanceof OnlineTimeSeries) {
			final Resource res = ((OnlineTimeSeries) schedule).getResource();
			if (nameService != null) {
				String name = nameService.getName(res, locale);
				if (name != null)
					return name;
			}
			return ResourceUtils.getHumanReadableName(res);
		}
		if (schedule instanceof LabelledItem)
			return ((LabelledItem) schedule).label(locale);
		throw new IllegalArgumentException("Could not determine schedule label for time series " + schedule +
				". Please provide a custom DisplayTemplate.") ;
	}

}
