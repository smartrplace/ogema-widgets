package de.iwes.timeseries.eval.online.utils;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;

public class InputSeriesMultiAggregator {
	private final InputSeriesAggregator[] aggregators;
	private final int size;
	private final float[] lastValues;
	private final long[] lastNextTimeStamps;
	
	/**
	 * 
	 * @param aggregators. The endTime of the first aggregator will be used as endTime for entire evaluation
	 * if the endTimes would differ (which normally should not be the case)
	 */
	public InputSeriesMultiAggregator(InputSeriesAggregator[] aggregators) {
		this.aggregators = aggregators;
		this.size = aggregators.length;
		this.lastValues = new float[size];
		this.lastNextTimeStamps = new long[size];
		for(int i=0; i<size; i++) lastNextTimeStamps[i] = -1;
	}

	public class MultiValueDuration {
		public float[] values;
		public long duration;
	}
	
	public MultiValueDuration getCurrentValueDuration(SampledValue sv, SampledValueDataPoint dataPoint,
			boolean ignoreMissingPoints, int idxOfRequestedInput, int idxOfEvaluationInput) {
		MultiValueDuration result = new MultiValueDuration();
		result.values = new float[size];
		long nextTime = Long.MAX_VALUE;
		for(int i=0; i<size; i++) {
			if(i == idxOfRequestedInput) {
				ValueDuration val = aggregators[i].getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, ignoreMissingPoints);
				result.values[i] = lastValues[i] = val.value;
				lastNextTimeStamps[i] = val.nextTimeStamp;
			} else {
				result.values[i] = lastValues[i];
				if(lastNextTimeStamps[i] <= 0) {
					for(int newIdx=0; newIdx<aggregators[i].nrInput; newIdx++) {
						SampledValue nextSv = dataPoint.getNextElement(aggregators[i].getTotalInputIdx(newIdx));
						if(nextSv != null) {
							lastNextTimeStamps[i] = nextSv.getTimestamp();
							break;
						}
					}
				}
			}
			if((lastNextTimeStamps[i] > 0) && (lastNextTimeStamps[i] < nextTime))
				nextTime = lastNextTimeStamps[i];
		}
		long now = sv.getTimestamp();
		result.duration = aggregators[0].getNextTimeStamp(now, nextTime) - now;
		return result;
	}
}
