package org.ogema.model.scheduleviewer.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Configuration;

/** Configuration of schedule viewer view
 * @deprecated if external schedule viewer configuration is required use ScheduleViewerConfigurationProvider*/
public interface ScheduleViewerConfig extends Configuration {

	ResourceList<TimeSeriesPresentationData> timeSeriesData();
	
	/**If true the dropdowns to select a setting and the time series will not be shown. In this
	 * mode schedule viewer is just design to show certain plots
	 */
	BooleanResource hideSelection();
	
	/**Filters that can be applied to this ScheduleViewerConfig*/
	ResourceList<TimeSeriesPresentationFilter> relevantFilters();
	
	/** 1: last 10 minutes
	 * 2: last hour
	 * 3: last day
	 * 4: last two days
	 * 5: last week
	 * 6: last month
	 * other: show all values available*
	 */
	IntegerResource intervalToDisplay();
	TimeSeriesPresentationFilter filterChosen();
}
