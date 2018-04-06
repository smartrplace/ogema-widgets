package de.iwes.timeseries.eval.api;

import org.ogema.core.model.simple.SingleValueResource;

/**
 * A wrapper for {@link SingleValueResource}, which allows to specify 
 * additional information when taking an online data series from the resource.
 * For instance, an interpolation mode different from the 
 * original mode of the time series can be set.<br>
 * Instances of this type serve as input data for time series evaluations.
 */
public interface TimeSeriesDataOnline extends TimeSeriesData {

	/**
	 * The resource providing online data.
	 * @return
	 */
	SingleValueResource getResource();
}
