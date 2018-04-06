package de.iwes.timeseries.eval.online.utils;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;

/**Online estimator to build up a time series from input values*/
public class TimeSeriesOnlineBuilder {
	private final List<SampledValue> collected = new ArrayList<>();
	
	/** Construct Base Estimator object that can be fed with values via {@link #addValue(float)}
	 * 
	 * @param calculateMinMax if true minimum and maximum values are searched
	 * @param averageMode if NONE no average value is summed up, otherwise average is always calculated.
	 */
	public TimeSeriesOnlineBuilder() {
	}

	public ReadOnlyTimeSeries getTimeSeries() {
		FloatTreeTimeSeries result = new FloatTreeTimeSeries();
		//for an empty FloatTreeTimeSeries addValues should be pretty efficient
		result.addValues(collected);
		return result;
	}
	
	/**Report new value.
	 * @param value
	 * @param duration If the evaluation shall weight all values equally (duration of the value
	 * not considered, just hand over 1 every time, otherwise the duration of the value
	 * TODO: This only makes sense in certain cases of further processing. If this is not
	 * guaranteed use {@link #addValue(SampledValue)}.
	 */
	public void addValue(float value, long duration) {
		addValue(new SampledValue(new FloatValue(value), duration, Quality.GOOD));
	}
	public void addValue(SampledValue value) {
		collected.add(value);
	}
	
}
