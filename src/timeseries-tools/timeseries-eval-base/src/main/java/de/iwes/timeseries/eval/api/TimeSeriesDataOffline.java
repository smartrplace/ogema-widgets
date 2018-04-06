package de.iwes.timeseries.eval.api;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;

/**
 * A wrapper for {@link ReadOnlyTimeSeries}, which allows to specify 
 * certain modifications of the original time series without actually 
 * modifying it. For instance, an interpolation mode different from the 
 * original mode of the time series can be set.<br>
 * Instances of this type serve as input data for time series evaluations.
 * <br>
 * Note: this can be applied to online data as well, the name is totally misleading.
 * See {@link OnlineTimeSeriesCache} for how to create a 
 * {@link ReadOnlyTimeSeries} for online data.
 */
public interface TimeSeriesDataOffline extends TimeSeriesData {

	/**
	 * The wrapped time series.
	 * @return
	 */
	ReadOnlyTimeSeries getTimeSeries();
	
	/**
	 * Offset each entry of the time series by some duration 
	 * @return
	 * 
	 */
	// TODO implement
	long timeOffset();
}
