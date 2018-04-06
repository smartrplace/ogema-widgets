package de.iwes.timeseries.eval.garo.multibase;

import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;

/**
 * Calculate the comfort temperature that was defined by the user.
 */
//@Service(EvaluationProvider.class)
//@Component
public interface GaRoSingleEvalProvider extends EvaluationProvider, EvaluationListener {
   GaRoDataType[] getGaRoInputTypes();

   /**
    * 
    * @return null if all room types shall be used, -1 if evaluation shall be made on
    * entire building (unit)
    */
   int[] getRoomTypes();
   
   /**
    * 
    * @return null if no extended result definition is used
    */
   <R> Class<? extends GaRoMultiResultExtended<R>> extendedResultDefinition();
   
	public enum IntervalAggregationMode {
		AVERAGING,
		INTEGRATING,
		MIN,
		MAX,
		/** In mode OTHER the evaluation has to run for every aggregation level requested by the user. If this
		 * is expected to be very time-consuming defined pre-evaluations with another aggregation mode, which
		 * can avoid touching the raw data again when calculating aggregated results.
		 */
		OTHER
	}
	
	/**Get aggregation mode for a result type
	 * @param resultType must be one of the result returned by {@link #resultTypes()}*/
	public IntervalAggregationMode getResultAggregationMode(ResultType resultType);

}
