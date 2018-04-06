package de.iwes.timeseries.eval.base.provider.gap;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

/**
 *
 * @author jlapp
 */
public abstract class AbstractEvaluator {
    
    protected final List<EvaluationInput> input;
    protected final List<ResultType> requestedResults;
    protected final Collection<ConfigurationInstance> configurations;
    
    public AbstractEvaluator(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations) {
        this.input = input;
        this.requestedResults = requestedResults;
        this.configurations = configurations;
    }
    
    public abstract Map<ResultType, EvaluationResult> currentResults(Collection<ResultType> requestedResults);
    
    public abstract void step(SampledValueDataPoint data);
    
}
