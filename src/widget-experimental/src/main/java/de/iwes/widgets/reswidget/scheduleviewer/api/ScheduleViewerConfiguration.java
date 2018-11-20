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
package de.iwes.widgets.reswidget.scheduleviewer.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iwes.widgets.html.schedulemanipulator.ScheduleManipulatorConfiguration;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleplot.flot.SchedulePlotFlot;
import de.iwes.widgets.reswidget.scheduleviewer.ScheduleViewerBasic;

/**
 * Configuration class for the {@link ScheduleViewerBasic}.
 * Use a {@link ScheduleViewerConfigurationBuilder} to create an instance.
 */
public class ScheduleViewerConfiguration {
	
	public static final ScheduleViewerConfiguration DEFAULT_CONFIGURATION = new ScheduleViewerConfiguration(false, true, true,  null);
	public final boolean showManipulator;
	public final boolean showCsvDownload;
	public final boolean useNameService;
	public final boolean showOptionsSwitch;
	public final boolean showNrPointsPreview;
	private final boolean showPlotTypeSelector;
	private final boolean showDownsampleInterval;
	private final boolean showUpdateInterval;
	private final boolean loadSchedulesOnInit;
	@SuppressWarnings("rawtypes")
	private final Class<? extends TimeSeriesPlot> plotType;
	// FIXME final
	/**
	 * @deprecated Non-final fields will be removed in a future version of this
	 */
	@Deprecated
	public boolean showIndividualConfigBtn;
	public final Long bufferWindow;
	
	// FIXME final
	public boolean showStandardIntervals = false;
    /** 
     * start time before now
	 * if null, the start time will be determined from the selected schedules
	 * May be null.
	 */
	public final Long startTime;
	/**
	 * end time after now
	 * if null, the end time will be determined from the selected schedules
	 * May be null
	 */
	public final Long endTime;
	
	public final ScheduleManipulatorConfiguration manipulatorConfiguration;
	/**
	 * Each list entry is an unmodifiable map of programs the user can select.
	 * May be null.
	 */
	public final List<Map<String,TimeSeriesFilter>> programs;
	/**
	 * Each list entry is an unmodifiable map of programs the user can select.
	 * May be null.
	 */
	public final List<Map<String,ConditionalTimeSeriesFilter<?>>> filters;
	
	/**Create with default values (show all standard elements, but no filters)
	 * @deprecated use {@link ScheduleViewerConfigurationBuilder}
	 */
	@Deprecated
	public ScheduleViewerConfiguration() {
		this(true, true, false, true, new ScheduleManipulatorConfiguration(null, true, true), true, null, null, null, null, 24*60*60*1000L);
	}
	
	/**
	 * @param showManipulator
	 * @param useNameService
	 * @param showOptionsSwitch
	 * @param manipulatorConfiguration
	 * @deprecated use {@link ScheduleViewerConfigurationBuilder}
	 */
	@Deprecated
	public ScheduleViewerConfiguration(boolean showManipulator, boolean useNameService, boolean showOptionsSwitch, ScheduleManipulatorConfiguration manipulatorConfiguration) {
		this(showManipulator, false, useNameService, showOptionsSwitch, manipulatorConfiguration, true, null, null, null, null, 24*60*60*1000L);
	}
	
	/**
	 * @param showManipulator
	 * 		Allow the user to manipulate time series.
	 * @param showCsvDownload
	 * 		Add a download field for CSV data?
	 * @param useNameService
	 * 		For use with Schedules. If true, the schedule viewer uses the NameService to label the schedule resources.
	 * @param manipulatorConfiguration
	 * 		Configuration for the schedule manipulator. Ignored if showManipulator is false. <br>
	 * 		Note: the alert field is ignored, instead the alert of the SCheduleViewer is used.
	 * @param showOptionsSwitch if true the check boxes "Show empty schedules" and "Fix interval on schedule switch"
	 * 		are shown
	 * @param showNrPointsPreview if true a preview of the number of data points in the interval selected and
	 * 		in the schedules selected is shown. This gives an idea whether clicking on "Apply" may take quite some
	 * 		time to transmit and render a huge amount of data.
	 * @param startTime
	 * 		may be null
	 * @param endTime
	 * 		may be null
	 * @param programs
	 * 		may be null
	 * @param filters
	 * 		may be null
	 * @param bufferWindow
	 * 		may be null, in which case data is not buffered. If non-null, it must be positive.
	 * @deprecated use {@link ScheduleViewerConfigurationBuilder}
	 */
	@Deprecated
	public ScheduleViewerConfiguration(boolean showManipulator, boolean showCsvDownload, boolean useNameService, boolean showOptionsSwitch, 
			ScheduleManipulatorConfiguration manipulatorConfiguration, boolean showNrPointsPreview, Long startTime, Long endTime, 
			List<Collection<TimeSeriesFilter>> programs, List<Collection<ConditionalTimeSeriesFilter<?>>> filters, Long bufferWindow) {
		this.showManipulator = showManipulator;
		this.showCsvDownload = showCsvDownload;
		this.useNameService = useNameService;
		this.showOptionsSwitch = showOptionsSwitch;
		this.showNrPointsPreview = showNrPointsPreview;
		this.loadSchedulesOnInit = false;
		this.startTime = startTime;
		this.endTime = endTime;
		if (bufferWindow != null && bufferWindow < 0)
			throw new IllegalArgumentException();
		this.bufferWindow = bufferWindow;
		if (showManipulator) {
			if (manipulatorConfiguration == null)
				manipulatorConfiguration = new ScheduleManipulatorConfiguration();
			this.manipulatorConfiguration = manipulatorConfiguration;
		}
		else 
			this.manipulatorConfiguration = null;
		if (programs == null || programs.isEmpty())
			this.programs = null;
		else {
			List<Map<String,TimeSeriesFilter>> pfilters = new ArrayList<>();
			Map<String, TimeSeriesFilter> p;
			for (Collection<TimeSeriesFilter> set: programs) {
				p = new HashMap<>();
				for (TimeSeriesFilter f: set) {
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
		if (filters == null || filters.isEmpty())
			this.filters = null;
		else {
			List<Map<String,ConditionalTimeSeriesFilter<?>>> ffilters = new ArrayList<>();
			Map<String, ConditionalTimeSeriesFilter<?>> p;
			for (Collection<ConditionalTimeSeriesFilter<?>> set: filters) {
				p = new HashMap<>();
				for (ConditionalTimeSeriesFilter<?> f: set) {
					String id = f.id();
					if (id == null || id.trim().isEmpty()) 
						continue;
					id = id.trim();
					p.put(id, f);
				}
				ffilters.add(Collections.unmodifiableMap(p));
			}
			this.filters = Collections.unmodifiableList(ffilters);
		}
		this.showPlotTypeSelector = false;
		this.showDownsampleInterval = false;
		this.showUpdateInterval = false;
		this.plotType = SchedulePlotFlot.class;
	}
	
	ScheduleViewerConfiguration(boolean showManipulator, boolean showCsvDownload, boolean useNameService, boolean showOptionsSwitch, 
			ScheduleManipulatorConfiguration manipulatorConfiguration, boolean showNrPointsPreview, Long startTime, Long endTime, 
			List<Map<String,TimeSeriesFilter>> programs, List<Map<String,ConditionalTimeSeriesFilter<?>>> filters, Long bufferWindow,
			boolean showIndividualConfigPopup, boolean showIntervals, boolean showPlotTypeSelector, boolean downsamplingItv, boolean showUpdateInterval,
			Class<? extends TimeSeriesPlot> plotType, boolean loadSchedulesOnInit) {
		this.showManipulator = showManipulator;
		this.showCsvDownload = showCsvDownload;
		this.useNameService = useNameService;
		this.showOptionsSwitch = showOptionsSwitch;
		this.showNrPointsPreview = showNrPointsPreview;
		this.startTime = startTime;
		this.endTime = endTime;
		this.loadSchedulesOnInit = loadSchedulesOnInit;
		this.showUpdateInterval = showUpdateInterval;
		if (bufferWindow != null && bufferWindow < 0)
			throw new IllegalArgumentException();
		this.bufferWindow = bufferWindow;
		if (showManipulator) {
			if (manipulatorConfiguration == null)
				manipulatorConfiguration = new ScheduleManipulatorConfiguration();
			this.manipulatorConfiguration = manipulatorConfiguration;
		}
		else 
			this.manipulatorConfiguration = null;
		this.programs = programs;
		this.filters = filters;
		this.showIndividualConfigBtn = showIndividualConfigPopup;
		this.showStandardIntervals = showIntervals;
		this.showPlotTypeSelector = showPlotTypeSelector;
		this.showDownsampleInterval = downsamplingItv;
		this.plotType = plotType;
	}

	public boolean isShowManipulator() {
		return showManipulator;
	}

	public boolean isShowCsvDownload() {
		return showCsvDownload;
	}

	public boolean isUseNameService() {
		return useNameService;
	}

	public boolean isShowOptionsSwitch() {
		return showOptionsSwitch;
	}

	public boolean isShowNrPointsPreview() {
		return showNrPointsPreview;
	}

	public boolean isShowPlotTypeSelector() {
		return showPlotTypeSelector;
	}

	public boolean isShowIndividualConfigBtn() {
		return showIndividualConfigBtn;
	}

	public Long getBufferWindow() {
		return bufferWindow;
	}

	public boolean isShowStandardIntervals() {
		return showStandardIntervals;
	}

	public Long getStartTime() {
		return startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public ScheduleManipulatorConfiguration getManipulatorConfiguration() {
		return manipulatorConfiguration;
	}

	public List<Map<String, TimeSeriesFilter>> getPrograms() {
		return programs;
	}

	public List<Map<String, ConditionalTimeSeriesFilter<?>>> getFilters() {
		return filters;
	}

	public boolean isShowDownsampleInterval() {
		return showDownsampleInterval;
	}
	
	public boolean isShowUpdateInterval() {
		return showUpdateInterval;
	}
	
	@SuppressWarnings("rawtypes")
	public Class<? extends TimeSeriesPlot> getPlotLibrary() {
		return plotType;
	}
	
	public boolean isLoadSchedulesOnInit() {
		return loadSchedulesOnInit;
	}
	
}
