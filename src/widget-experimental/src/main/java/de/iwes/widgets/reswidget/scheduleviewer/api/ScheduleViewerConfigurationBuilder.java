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

package de.iwes.widgets.reswidget.scheduleviewer.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.resourcemanager.pattern.ResourcePattern;

import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.html.schedulemanipulator.ScheduleManipulatorConfiguration;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultTimeSeriesDisplayTemplate;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;
import de.iwes.widgets.reswidget.scheduleviewer.api.ScheduleViewerConfigurationProvider.SessionConfiguration;

/**
 * Configuration class for the {@link ScheduleViewerBasic}.
 */
public class ScheduleViewerConfigurationBuilder {

	public static ScheduleViewerConfigurationBuilder newBuilder() {
		return new ScheduleViewerConfigurationBuilder();
	}

	private ScheduleViewerConfigurationBuilder() {
	}

	private boolean downsamplingInterval = false;
	private boolean showPlotTypeSelector = false;
	private boolean showManipulator = false;
	private boolean showCsvDownload = true;
	private boolean useNameService = true;
	private boolean showOptionsSwitch = true;
	private boolean showNrPointsPreview = true;
	private boolean showIndividualConfigBtn = false;
	private Long bufferWindow = 24 * 60 * 60 * 1000L;

	private boolean showStandardIntervals = false;
	/**
	 * start time before now if null, the start time will be determined from the
	 * selected schedules May be null.
	 */
	private Long startTime = null;
	/**
	 * end time after now if null, the end time will be determined from the selected
	 * schedules May be null
	 */
	private Long endTime = null;

	private ScheduleManipulatorConfiguration manipulatorConfiguration;
	/**
	 * Each list entry is an unmodifiable map of programs the user can select. May
	 * be null.
	 */
	private List<Map<String, TimeSeriesFilter>> programs;
	/**
	 * Each list entry is an unmodifiable map of programs the user can select. May
	 * be null.
	 */
	private List<Map<String, ConditionalTimeSeriesFilter<?>>> filters;

	/**
	 * Create the configuration
	 * 
	 * @return
	 */
	public ScheduleViewerConfiguration build() {
		return new ScheduleViewerConfiguration(showManipulator, showCsvDownload, useNameService, showOptionsSwitch,
				manipulatorConfiguration, showNrPointsPreview, startTime, endTime, programs, filters, bufferWindow,
				showIndividualConfigBtn, showStandardIntervals, showPlotTypeSelector, downsamplingInterval);
	}

	/**
	 * The schedule manipulator is only shown when a single schedule is selected.
	 * The manipulator shows single values from an interval selected and allows to
	 * edit them. If the default value remains at false the schedule manipulator
	 * remains hidden even if a single schedule is selected.<br>
	 * The schedule manipulator is placed below the plot canvas then it is active.
	 * Default value: false
	 * 
	 * @param showManipulator
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowManipulator(boolean showManipulator) {
		this.showManipulator = showManipulator;
		return this;
	}

	/**
	 * The CSV download allows to download a file containing all data currently
	 * selected. It is placed below the plot canvas. Default value: true
	 * 
	 * @param showCsvDownload
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowCsvDownload(boolean showCsvDownload) {
		this.showCsvDownload = showCsvDownload;
		return this;
	}

	/**
	 * The labels shown for various types of ReadOnlyTimeSeries that can be offered
	 * and selected within in the widget are determined via the
	 * {@link DefaultTimeSeriesDisplayTemplate}. If this option is set to true the
	 * template takes into account the {@link NameService} offered by the widget
	 * application (normally standard OGEMA widget name service, which can provide
	 * nicer human readable names for certain resources, which is most relevant for
	 * schedule labels). Default value: true
	 * 
	 * @param useNameService
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setUseNameService(boolean useNameService) {
		this.useNameService = useNameService;
		return this;
	}

	/**
	 * The schedule viewer widget normally offers two switch items: One if selected
	 * "fixes" the interval meaning the start and end time are not adjusted when the
	 * time series selected change. The other switch allows to control whether empty
	 * schedules are offered for selection. Excluding empty schedules does not work
	 * for time series types for which checking the data size would be too
	 * costly.<br>
	 * When setting this option to false both elements are removed from the view and
	 * cannot be changed from their standard values (which is changing the interval
	 * always to the largest interval that shows all data selected and showing also
	 * empty time series) Default value: true
	 * 
	 * @param showOptionsSwitch
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowOptionsSwitch(boolean showOptionsSwitch) {
		this.showOptionsSwitch = showOptionsSwitch;
		return this;
	}

	/**
	 * Normally the schedule viewer displays the number of data points selected.
	 * This may be updated only on a plot update if calculation of data size would
	 * be too costly beforehand. Setting this option to false disables this field of
	 * the schedule viewer. Default value: true
	 * 
	 * @param showNrPointsPreview
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowNrPointsPreview(boolean showNrPointsPreview) {
		this.showNrPointsPreview = showNrPointsPreview;
		return this;
	}

	/**
	 * If set true a button is shown that allows to set a scaling factor and an
	 * offset for certain time series can be set TODO: Check the feature and
	 * complete documentation Default value: false
	 * 
	 * @param showIndividualConfigBtn
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowIndividualConfigBtn(boolean showIndividualConfigBtn) {
		this.showIndividualConfigBtn = showIndividualConfigBtn;
		return this;
	}

	/**
	 * To improve performance for larger views you can increase the internal buffer
	 * time here. TODO CN: Add more documentation on this option. Default value: 1
	 * day
	 * 
	 * @param bufferWindow
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setBufferWindow(Long bufferWindow) {
		this.bufferWindow = bufferWindow;
		return this;
	}

	/**
	 * If set to true standard intervals like "last day" and "last week" are offered
	 * to set the interval displayed. Default value: false
	 * 
	 * @param showStandardIntervals
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowStandardIntervals(boolean showStandardIntervals) {
		this.showStandardIntervals = showStandardIntervals;
		return this;
	}

	/**
	 * Default value: null
	 * 
	 * @param startTime
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setStartTime(Long startTime) {
		this.startTime = startTime;
		return this;
	}

	/**
	 * Default value: null
	 * 
	 * @param endTime
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setEndTime(Long endTime) {
		this.endTime = endTime;
		return this;
	}

	/**
	 * See {@link ScheduleManipulatorConfiguration} for details. Only relevant when
	 * {@link #setShowManipulator(boolean)} has been set true Default value: null
	 * 
	 * @param manipulatorConfiguration
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setManipulatorConfiguration(
			ScheduleManipulatorConfiguration manipulatorConfiguration) {
		this.manipulatorConfiguration = manipulatorConfiguration;
		return this;
	}

	/**
	 * A {@link TimeSeriesFilter} is a labeled item that determines by its accept
	 * method whether a ReadOnlyTimeSeries shall be displayed or not. It also
	 * defines some standard filters like "SCHEDULES_ONLY". If this configuration is
	 * set for each entry in the outer input list a MultiSelect and shown that
	 * allows the user to choose a filter. The elements of the outer list are
	 * connected via AND logic whereas the different filtering options provided by
	 * inner collection are connected via OR logic. This means that from each
	 * Collection contained in the outer list at least one TimeSeriesFilter has to
	 * accept a schedule to be used.<br>
	 * For SessionConfigurations and specific schedule viewer applications this can
	 * be used to provide a fixed list of time series as a program TODO CN: Add
	 * documentation on ManipulatorConfigurations Default value: null
	 * 
	 * @param programs
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setPrograms(List<Collection<TimeSeriesFilter>> programs) {
		if (programs == null || programs.isEmpty())
			this.programs = null;
		else {
			List<Map<String, TimeSeriesFilter>> pfilters = new ArrayList<>();
			Map<String, TimeSeriesFilter> p;
			for (Collection<TimeSeriesFilter> set : programs) {
				p = new HashMap<>();
				for (TimeSeriesFilter f : set) {
					String id = f.id();
					if (id == null || id.trim().isEmpty())
						continue;
					id = id.trim();
					p.put(id, f);
				}
				pfilters.add(Collections.unmodifiableMap(p));
			}
			this.programs = Collections.unmodifiableList(pfilters);
		}
		return this;
	}

	/**
	 * Set a session configuration to be used when no session configuration is
	 * provided via URL parameters/ScheduleViewerConfigurationProviders
	 */
	public ScheduleViewerConfigurationBuilder setDefaultSessionConfiguration(
			SessionConfiguration defaultConfiguration) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * A {@link ConditionalTimeSeriesFilter} determines whether a time series is
	 * applicable to a {@link ResourcePattern} instance. If this configuration is
	 * set for each entry in the outer input list a FlexBox containing two Dropdowns
	 * is generated allowing to choose a filter and a pattern. TODO CN: Add
	 * documentation on ManipulatorConfigurations
	 * 
	 * Default value: null
	 * 
	 * @param filters
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setFilters(List<List<ConditionalTimeSeriesFilter<?>>> filters) {
		return setFilters(filters, null);
	}

	/**
	 * @see ScheduleViewerConfigurationBuilder#setFilters(List, Integer)
	 * @param filters 
	 * @param conditionalTimeSeriesFilterCategoryPreselected Index of outer list to be used
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setFilters(List<List<ConditionalTimeSeriesFilter<?>>> filters,
			Integer conditionalTimeSeriesFilterCategoryPreselected) {
		if (filters == null || filters.isEmpty()) {
			this.filters = null;
		} else {
			List<Map<String, ConditionalTimeSeriesFilter<?>>> ffilters = new ArrayList<>();
			Map<String, ConditionalTimeSeriesFilter<?>> p = new HashMap<>();
			
			if (conditionalTimeSeriesFilterCategoryPreselected == null || filters.size() < conditionalTimeSeriesFilterCategoryPreselected) {				
				for (Collection<ConditionalTimeSeriesFilter<?>> innerFilterList : filters) {
					p = new HashMap<>();
					for (ConditionalTimeSeriesFilter<?> filter : innerFilterList) {
						String id = filter.id();
						if (id == null || id.trim().isEmpty())
							continue;
						id = id.trim();
						p.put(id, filter);
					}
					ffilters.add(Collections.unmodifiableMap(p));
				}				
			} else if(filters != null){
				Collection<ConditionalTimeSeriesFilter<?>> innerFilterList = filters.get(conditionalTimeSeriesFilterCategoryPreselected);
				for (ConditionalTimeSeriesFilter<?> filter : innerFilterList) {
					String id = filter.id();
					if (id == null || id.trim().isEmpty())
						continue;
					id = id.trim();
					p.put(id, filter);
				}
				ffilters.add(Collections.unmodifiableMap(p));
			}
			this.filters = Collections.unmodifiableList(ffilters);
		}

		return this;
	}
	
	/**
	 * Let the user select the line type? Default is false.
	 * @param showPlotTypeSelector
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowPlotTypeSelector(boolean showPlotTypeSelector) {
		this.showPlotTypeSelector = showPlotTypeSelector;
		return this;
	}
	
	/**
	 * Allow the user to select a downsampling interval? Default is false.
	 * @param downsamplingInterval
	 * @return
	 */
	public ScheduleViewerConfigurationBuilder setShowDownsamplingInterval(boolean downsamplingInterval) {
		this.downsamplingInterval = downsamplingInterval;
		return this;
	}
	
}
