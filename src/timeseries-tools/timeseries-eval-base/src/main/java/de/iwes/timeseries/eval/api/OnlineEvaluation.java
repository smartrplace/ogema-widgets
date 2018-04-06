package de.iwes.timeseries.eval.api;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface OnlineEvaluation extends EvaluationInstance {

	/**
	 * Finish the online evaluation
	 * @return
	 */
	Status finish();
	
	/**
	 * Finish the online evaluation
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws TimeoutException
	 */
	@Deprecated
	Status finish(long timeout, TimeUnit unit) throws TimeoutException;
	
	/**
	 * Get current results but continue the evaluation. Contrary to
	 * {@link #getResults()} this does not throw {@link IllegalStateException}
	 * when called before the evaluation is finished.
	 * @throws IllegalStateException
	 * 		If the evaluation failed
	 * @return
	 */
	Map<ResultType, EvaluationResult> getIntermediateResults();
	
	
}
