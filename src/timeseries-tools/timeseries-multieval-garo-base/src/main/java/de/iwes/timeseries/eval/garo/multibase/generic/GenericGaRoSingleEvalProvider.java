package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.AbstractEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;

/**
 * Generic GaRoSingleEvalProvider that allows to defined eval providers without
 * separate EvaluationInstance classes.
 */
public abstract class GenericGaRoSingleEvalProvider extends AbstractEvaluationProvider implements GaRoSingleEvalProvider {
    protected abstract List<GenericGaRoResultType> resultTypesGaRo();
	protected abstract GenericGaRoEvaluationCore initEval(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd);
    
	@Override
	public <R> Class<? extends GaRoMultiResultExtended<R>> extendedResultDefinition() {
		return null;
	}
 	@Override
	public int[] getRoomTypes() {
		return null;
	}

	
	public GenericGaRoSingleEvalProvider(String id, String label, String description) {
        super(null, id, label, description);
    }
    
    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new GenericGaRoSingleEvaluation(input, requestedResults, configurations, this, time,
        		this);
//        		resultsOffered);
    }
        
    @Override
    public List<RequiredInputData> inputDataTypes() {
        return GaRoEvalHelper.getInputDataTypes(getGaRoInputTypes());
    }

	@Override
	public IntervalAggregationMode getResultAggregationMode(ResultType resultType) {
		return IntervalAggregationMode.AVERAGING;
	}
	
    public final static ResultType GAP_TIME = new GenericGaRoResultType("GAP_TIME") {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) { //never called
			return null;}
    };
    
    @Override
    public List<ResultType> resultTypes() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ResultType> result = new ArrayList<>((List) resultTypesGaRo());
    	result.add(GAP_TIME);
        return result;
    }
}
