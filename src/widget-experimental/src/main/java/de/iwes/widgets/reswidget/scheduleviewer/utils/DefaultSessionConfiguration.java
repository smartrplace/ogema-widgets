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
package de.iwes.widgets.reswidget.scheduleviewer.utils;

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
	public boolean overwriteDefaultTimeSeries() {
		return false;
	}

	@Override
	public boolean markTimeSeriesSelectedViaPreselectedFilters() {
		return false;
	}

	@Override
	public boolean generateGraphImmediately() {
		return false;
	}


}
