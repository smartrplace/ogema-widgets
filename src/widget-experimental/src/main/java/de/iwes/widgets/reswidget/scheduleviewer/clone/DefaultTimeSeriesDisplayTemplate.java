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

package de.iwes.widgets.reswidget.scheduleviewer.clone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilterExtended;
import de.iwes.widgets.template.DisplayTemplate;

public class DefaultTimeSeriesDisplayTemplate<T extends ReadOnlyTimeSeries> implements DisplayTemplate<T> {

	private final NameService nameService;
	private final List<TimeSeriesFilterExtended> filters;	
	private final String selectedNamingType;
	
	public DefaultTimeSeriesDisplayTemplate(NameService nameService, List<Collection<TimeSeriesFilterExtended>> filterCollection, String selectedNamingType) {
		this.nameService = nameService;
		this.filters = new ArrayList<>();
		for(Collection<TimeSeriesFilterExtended> item : filterCollection) {
			this.filters.addAll(item);
		}	
		this.selectedNamingType = selectedNamingType;
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
		
		for(TimeSeriesFilterExtended filter : filters) {
			if(filter.accept(schedule)) {
				if(ScheduleViewerBasic.SHORT_NAME.equals(selectedNamingType)) {
					return filter.shortName(schedule);
				}else if(ScheduleViewerBasic.LONG_NAME.equals(selectedNamingType)) {				
					return filter.longName(schedule);
				}
			}
		}
		
		if (schedule instanceof Schedule) {			
			if(ScheduleViewerBasic.LOCATION.equals(selectedNamingType)) {
				Schedule s = (Schedule) schedule;
				return s.getLocation();
			}
		}
		
		if(schedule instanceof SchedulePresentationData) {
			return ((SchedulePresentationData) schedule).getLabel(locale);
		}
		
		if (schedule instanceof Schedule) {
			if (nameService != null) {
				String name = nameService.getName((Schedule) schedule, locale);
				if (name != null) {
					return name;
				}
			}
			return ResourceUtils.getHumanReadableName((Schedule) schedule);
		}
		if (schedule instanceof RecordedData) {
			return ((RecordedData) schedule).getPath();
		}
		
		throw new IllegalArgumentException("Could not determine schedule label for time series " + schedule +
				". Please provide a custom DisplayTemplate.") ;
	}
	
	


}
