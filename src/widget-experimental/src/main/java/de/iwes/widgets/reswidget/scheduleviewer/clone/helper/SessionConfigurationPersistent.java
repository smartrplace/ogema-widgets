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
package de.iwes.widgets.reswidget.scheduleviewer.clone.helper;

import java.util.Collection;
import java.util.List;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider.SelectionConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;

public class SessionConfigurationPersistent implements SelectionConfiguration{

	private final List<ReadOnlyTimeSeries> timeSeriesSelected;
	private final List<Collection<TimeSeriesFilter>> programsPreselected;
	private final Integer conditionalTimeSeriesFilterCategoryPreselected;
	private final List<ConditionalTimeSeriesFilter<?>> filtersPreSelected;

	public SessionConfigurationPersistent(ApplicationManager appManager, String configurationId, List<ReadOnlyTimeSeries> timeSeriesSelected,
			List<Collection<TimeSeriesFilter>> programsPreselected,
			Integer conditionalTimeSeriesFilterCategoryPreselected,
			List<ConditionalTimeSeriesFilter<?>> filtersPreSelected) {
		this.timeSeriesSelected = timeSeriesSelected;
		this.programsPreselected = programsPreselected;
		this.conditionalTimeSeriesFilterCategoryPreselected = conditionalTimeSeriesFilterCategoryPreselected;
		this.filtersPreSelected = filtersPreSelected;
	}

	@Override
	public List<ReadOnlyTimeSeries> timeSeriesSelected() {
		return timeSeriesSelected;
	}

	@Override
	public List<Collection<TimeSeriesFilter>> programsPreselected() {
		return programsPreselected;
	}

	@Override
	public Integer conditionalTimeSeriesFilterCategoryPreselected() {
		return conditionalTimeSeriesFilterCategoryPreselected;
	}

	@Override
	public List<ConditionalTimeSeriesFilter<?>> filtersPreSelected() {
		return filtersPreSelected;
	}

	

}
