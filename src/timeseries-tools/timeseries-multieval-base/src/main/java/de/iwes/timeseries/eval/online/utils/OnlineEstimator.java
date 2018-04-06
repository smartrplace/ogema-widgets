package de.iwes.timeseries.eval.online.utils;

/**Note that the methods getDuration and getSetLastValue are simple replacements for the class
 * {@link InputSeriesMultiAggregator}. The advantage here is that these methods come with all
 * utils extending OnlineEstimator. This util does not require to declare the input series, but
 * only delivers backwards durations.
 *
 */
public class OnlineEstimator {
	private long lastEvalTime = -1;
	private float lastValue;
	
	/**Here we support an integration of steps defined for the specific result type. The
	 * steps of the entire evaluation may be different if a result is not calculated for
	 * each input type value.
	 * @param now
	 * @param dataPoint
	 * @return
	 */
	public long getDuration(long now) {
        final long duration;
 		if(lastEvalTime < 0) {
 			duration = -1;
		} else
			duration = now - lastEvalTime;
 		lastEvalTime = now;
		return duration;
	}
	public float getSetLastValue(float newValue) {
		float result = lastValue;
		lastValue = newValue;
		return result;
	};
}
