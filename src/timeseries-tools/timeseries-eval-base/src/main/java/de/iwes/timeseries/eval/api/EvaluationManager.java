package de.iwes.timeseries.eval.api;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;

public interface EvaluationManager {
	
	/**
	 * standard start-up
	 */
	EvaluationInstance newEvaluation(EvaluationProvider provider, List<EvaluationInput> input, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations);
	
	/**
	 * Providers called with this method do not get its step/stepInternal method called. Instead the
	 * newEvaluation method of the provider needs to start a separate thread
	 * 
	 * @param provider
	 * @param input
	 * @param requestedResults
	 * @param configurations
	 * @return
	 * @deprecated call provider.newEvaluation(input, requestedResults, configurations) instead
	 */
	@Deprecated
	public EvaluationInstance newEvaluationSelfOrganized(EvaluationProvider provider, List<EvaluationInput> input,
			List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations);

}
