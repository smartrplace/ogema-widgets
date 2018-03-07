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
import java.util.Collections;
import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.reswidget.scheduleviewer.api.ConditionalTimeSeriesFilter;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider.SessionConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilter;

public class DefaultSessionConfiguration implements SessionConfiguration{
	
	public final static DefaultSessionConfiguration DEFAULT_SESSION_CONFIGURATION = new DefaultSessionConfiguration(); 

	@Override
	public List<ReadOnlyTimeSeries> timeSeriesSelected() {
		return Collections.emptyList();
	}

	@Override
	public List<Collection<TimeSeriesFilter>> programsPreselected() {
		return Collections.emptyList(); 
	}

	@Override
	public Integer conditionalTimeSeriesFilterCategoryPreselected() {
		return Integer.valueOf(0);
	}

	@Override
	public List<ConditionalTimeSeriesFilter<?>> filtersPreSelected() {
		return Collections.emptyList();
	}

	@Override
	public ScheduleViewerConfiguration viewerConfiguration() {
		return ScheduleViewerConfiguration.DEFAULT_CONFIGURATION;
	}

	@Override
	public PreSelectionControllability intervalControllability() {
		return PreSelectionControllability.FLEXIBLE;
	}

	@Override
	public PreSelectionControllability timeSeriesSelectionControllability() {
		return PreSelectionControllability.FLEXIBLE;
	}

	@Override
	public PreSelectionControllability filterControllability() {
		return PreSelectionControllability.FLEXIBLE;
	}

	@Override
	public boolean overwritePrograms() {
		return false;
	}

	@Override
	public boolean overwriteConditionalFilters() {
		return false;
	}

	@Override
	public boolean overwriteProgramlistFixed() {
		return false;
	}

	@Override
	public boolean markTimeSeriesSelectedViaPreselectedFilters() {
		return false;
	}

}
