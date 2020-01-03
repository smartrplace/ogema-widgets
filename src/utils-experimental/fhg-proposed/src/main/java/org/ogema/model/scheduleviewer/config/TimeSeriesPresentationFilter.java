package org.ogema.model.scheduleviewer.config;

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;

/** Configure which time series from a ScheduleViewerConfig shall be presented
 * @deprecated if external schedule viewer configuration is required use ScheduleViewerConfigurationProvider*/
public interface TimeSeriesPresentationFilter extends Configuration {

	/**The types listed here shall be selected if they are in the path of the schedule resource*/
	StringArrayResource selectedDeviceTypes();
	/** All {@link TimeSeriesPresentationData} with name values beginning with one of
	 * the Strings giving here are selected*/
	StringArrayResource selectedScheduleNameBeginnings();
	
	/**If true only those given in the ResourceList are excluded from the filtered view
	 */
	BooleanResource showAllExcept();
	
	@Override
	/** Name to be shown for this filter*/
	StringResource name();
}
