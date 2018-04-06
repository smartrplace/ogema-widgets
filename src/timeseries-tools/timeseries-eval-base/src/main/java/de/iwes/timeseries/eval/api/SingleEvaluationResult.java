package de.iwes.timeseries.eval.api;

import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Evaluation result for a specific result type and specific input data. 
 * Actual results must implement one of the subinterfaces specified here, such as
 * {@link SingleValueResult}, {@link ArrayResult}, or {@link TimeSeriesResult}.
 */
public interface SingleEvaluationResult {
	
//	/**
//	 * Reference to the result this belongs to
//	 * @return
//	 */
//	EvaluationResult result();
	
	ResultType getResultType();
	
	/**
	 * The time series this has been calculated for. Must contain exactly one value for
	 * result type PER_INPUT.
	 * @return
	 */
	List<TimeSeriesData> getInputData();
	
	public static interface SingleValueResult<T> extends SingleEvaluationResult {
		
		T getValue();
		
	}
	
	public static interface ArrayResult extends SingleEvaluationResult {
		
		List<SingleEvaluationResult> getValues();
		
		/**
		 * may either return null, or a list of the same size as {@link #getValues()}
		 * @param locale
		 * @return
		 */
		default List<String> getLabels(OgemaLocale locale) {
			return null;
		};
		
	}
	
	public static interface TimeSeriesResult extends SingleValueResult<ReadOnlyTimeSeries> {
	}
	
}