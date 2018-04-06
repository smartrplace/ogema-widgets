package de.iwes.timeseries.eval.base.provider.gap;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

/**
 *
 * @author jlapp
 */
public class GapEvaluation extends EvaluationBaseImpl {
    
    AbstractEvaluator evaluator;

    public GapEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
        super(input, requestedResults, configurations, listener, time);
        evaluator = new GapEvaluator(input, requestedResults, configurations);
    }
    
    @Override
    protected Map<ResultType, EvaluationResult> getCurrentResults() {
        return evaluator.currentResults(requestedResults);
    }

    @Override
    protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
        evaluator.step(dataPoint);
    }

    @Override
    public String id() {
        return "gap_evaluation";
    }
    
}
