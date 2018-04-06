package de.iwes.timeseries.eval.api;

import org.ogema.core.timeseries.InterpolationMode;

/**
 * Common specification of offline or online time series input
 */
public interface TimeSeriesData extends LabelledItem {

	/**
	 * May be null, in which case the interpolation mode
	 * of the time series is used. This allows to overwrite 
	 * the explicit time series interpolation mode.
	 * @return
	 */
	InterpolationMode interpolationMode();

	/**
	 * offset each value of the time series by a specific 
	 * value
	 * @return
	 */
	// TODO implement
	float offset();
	
	/**
	 * multiply each value of the time series by a factor
	 * @return
	 */
	// TODO implement
	float factor();
}
