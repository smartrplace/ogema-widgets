package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalValueContainer;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;

/**
 * Contains state variables for the RoomBaseEvaluation 
 */
class GenericGaRoSingleEvalValueContainer extends SpecificEvalValueContainer {
	private final List<GenericGaRoResultType> requestedResultsGaRo;
	private GenericGaRoEvaluationCore evalContainer = null;

	/**
	 * @param size
	 * @param nrValves
	 * @param requestedResults
	 * @param input
	 * @param startTimeOpenWindow
	 * 		null, if window is closed initially, the start time for evaluation otherwise
	 */
    GenericGaRoSingleEvalValueContainer(final int size, 
    		List<GenericGaRoResultType> requestedResultsGaRo, List<ResultType> requestedResults2,
    		List<EvaluationInput> input) {
    	super(size, requestedResults2, input);
    	this.requestedResultsGaRo = requestedResultsGaRo;
    }
    public void setEvalContainer(GenericGaRoEvaluationCore evalContainer) {
    	this.evalContainer = evalContainer;
    }
    
    public synchronized Map<ResultType, EvaluationResult> getCurrentResults() {
    	final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
     	List<TimeSeriesData> inputData = input.get(0).getInputData();
    	for (GenericGaRoResultType rt : requestedResultsGaRo) {
    		final SingleEvaluationResult singleRes;
    		if (rt == GenericGaRoSingleEvalProvider.GAP_TIME) {
        		singleRes = new SingleValueResultImpl<Long>(rt, gapTime, input.get(0).getInputData());
    		} else
    			singleRes = rt.getEvalResult(evalContainer, rt, inputData);
     		if(singleRes == null)
     			throw new IllegalArgumentException("Invalid result type requested: " + (rt != null ? rt.id() : null));
    		results.put(rt, new EvaluationResultImpl(Collections.singletonList(singleRes), rt));
    	}
    	return results;
    }

}