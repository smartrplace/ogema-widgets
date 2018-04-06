package de.iwes.timeseries.eval.api;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;

/**
 * An evaluation provider analyses time series, providing e.g. statistical information
 * about them. 
 * Register instances of this as a service.
 */
public interface EvaluationProvider extends LabelledItem {

	/**
	 * The required input time series. For each element in the list, one or more
	 * time series must be provided when starting a new evaluation via
	 * {@link #newEvaluation(List, List, Collection)}. 
	 * For instance, a provider that evaluates a individual time series should return 
	 * a single element in the list, a provider that evaluates the correlation between
	 * two time series will return two elements.  
	 * @return
	 */
	List<RequiredInputData> inputDataTypes();
	
	/**
	 * The result types offered by this evaluation provider. Pass a subset of the
	 * types to {@link #newEvaluation(List, List, Collection)}, in order to trigger 
	 * an evaluation for them.
	 * @return
	 */
	List<ResultType> resultTypes();
	
	/**
	 * The configuration types supported by this provider.
	 * @return
	 */
	List<Configuration<?>> getConfigurations();
	
	/**
	 * Returns an existing evaluation
	 * @param id
	 * @return
	 */
	EvaluationInstance getEvaluation(String id);
	
	/**
	 * Returns ids of stored evaluations
	 * @return
	 */
	List<String> getEvaluationIds();
	
	boolean hasOngoingEvaluations();
	
	List<OnlineEvaluation> getOnlineEvaluations(boolean includeOngoing, boolean includeFinished);
	
	List<EvaluationInstance> getOfflineEvaluations(boolean includeOngoing, boolean includeFinished);
	
	
	/**
	 * Method for internal use. Creates an evaluation, but does not start it. Use 
	 * {@link EvaluationManager#newEvaluation(EvaluationProvider, List, List, Collection)}
	 * instead to create and start an evaluation.
	 * @param input
	 * 		pass exactly one entry per input data type (see {@link #inputDataTypes()})
	 * @param requestedResults
	 * 		a subset of {@link #resultTypes()}
	 * @param configurations
	 * 		provider specific configurations, see {@link #getConfigurations()}
	 * @throws IllegalArgumentException
	 * 		if the size of the input list does not match, the list of result types contains 
	 * 		unsupported elements, or the list of configurations contains unsupported elements.
	 * @return
	 */
	EvaluationInstance newEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations);
	
	// TODO clean up -> remove stored evaluation; or store them persistently
	
	/**
	 * If the evaluation needs the data in specific time steps, 
	 * @return
	 */
	default Long requestedUpdateInterval() {
		return null;
	}
	
}
