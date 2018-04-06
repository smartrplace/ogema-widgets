package de.iwes.timeseries.eval.api;

import java.util.List;

/**
 * Wraps a set of time series for an {@link EvaluationProvider}
 */
public interface EvaluationInput {
	
	/**
	 * Specifies whether the input is for online
	 * or offline evaluation. Online evaluation means that
	 * the call to iterator.hasNext() may be blocking.
	 * @return
	 */
	boolean isOnlineEvaluation();
	
	List<TimeSeriesData> getInputData();
	
}
