package de.iwes.timeseries.eval.api;

import java.util.List;

import de.iwes.timeseries.eval.api.ResultType.ResultStructure;

/**
 * Evaluation result(s) for one specific result type.
 */
public interface EvaluationResult {

	/**
	 * Each entry is for a fixed set of input time series,
	 * or a single time series, if the result structure is
	 * of type {@link ResultStructure#PER_INPUT})
	 * @return
	 */
	List<SingleEvaluationResult> getResults();
	
	ResultType getResultType();
	
}
