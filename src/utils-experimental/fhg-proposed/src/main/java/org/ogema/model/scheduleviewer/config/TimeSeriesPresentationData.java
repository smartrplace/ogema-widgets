package org.ogema.model.scheduleviewer.config;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;

/** Configure how a plot shall be presented
 * @deprecated if external schedule viewer configuration is required use ScheduleViewerConfigurationProvider*/
public interface TimeSeriesPresentationData extends Configuration {

	/**If this is a SingleValueResource historicalData shall be obtained directly
	 * via getHistoricalData
	 */
	
	public StringResource scheduleLocation();
	
	@Override
	/** Name to be used for this time series, e.g. in ScheduleView selection*/
	public StringResource name();
	
	/** If factor is not active or zero the factor will be determined automatically
	 */
	public FloatResource factor();
}
