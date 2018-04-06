package de.iwes.timeseries.eval.viewer.api;

import java.util.Collection;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.LabelledItem;

/**
 * A profile is a filter for time series.
 * This is a refined version of the ... class
 */
public interface Profile extends LabelledItem {

	/**
	 * A default interpolation mode for schedules belonging to this profile
	 * @return
	 * 		may return null, in which case the mode of the selected timeseries is used
	 */
	InterpolationMode defaultInterpolationMode();
	
	/**
	 * Return true iff the passed time series is applicable to this profile
	 * @param timeseries
	 * @return
	 */
	boolean accept(ReadOnlyTimeSeries timeseries);
	
	/**
	 * Specify how time series of this profile have to be aggregated.
	 * @param constituents
	 * @param labelAddOn
	 * 		may be null
	 * @return
	 */
	ProfileSchedulePresentationData aggregate(Collection<ReadOnlyTimeSeries> constituents, String labelAddOn);
	
}
