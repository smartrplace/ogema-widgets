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

package de.iwes.widgets.reswidget.scheduleviewer;

import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.template.DisplayTemplate;

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
		throw new IllegalArgumentException("Could not determine schedule label for time series " + schedule +
				". Please provide a custom DisplayTemplate.") ;
	}

}
