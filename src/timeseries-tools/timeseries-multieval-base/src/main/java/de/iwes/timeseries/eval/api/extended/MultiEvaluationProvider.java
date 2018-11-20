/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iwes.timeseries.eval.api.extended;

import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.widgets.template.LabelledItem;

/**Performs multiple evaluations via normal {@link EvaluationProvider}<br>
 * TODO: For now MultiEvaluationProviders only support OfflineEvaluations, but this could be
 * changed in the futures, implications have to be evaluated based on first implementation
 * experiences.<br>
 * TODO: Check if concept of EvaluationManager is relevant here. For now we start evaluations
 * directly here.
 */
public interface MultiEvaluationProvider<T extends MultiResult> extends LabelledItem {

	/**
	 * The required input data provider types. For each element in the list, one or more
	 * data providers must be provided when starting a new evaluation via
	 * {@link #newEvaluation(List, List, Collection)}. 
	 * The time series acquired via the data providers given to an evaluation are specified
	 * in the description.
	 */
	List<DataProviderType> inputDataTypes();
	
	/**
	 * The result structure offered by this evaluation provider.<br>
	 * Note: In contrast to EvaluationProviders it is not possible to request a subset of the evaluation data provided. In general
	 * this will be too complex for a MultiEvaluationProvider. In case different output options shall
	 * be supported this should be configured via configurations.
	 */
	<X extends MultiResult> Class<X> resultType();
	
	/**
	 * The configuration types supported by this provider.
	 * Note: This could be part of common interface with EvaluationProvider
	 */
	List<Configuration<?>> getConfigurations();
	
	/**
	 * Returns an existing evaluation
	 * @param id
	 * @return
	 */
	MultiEvaluationInstance<T> getEvaluation(String id);
	
	/**
	 * Returns ids of stored evaluations
	 * Note: This could be part of common interface with EvaluationProvider
	 */
	List<String> getEvaluationIds();
	
	/**
	 * Note: This could be part of common interface with EvaluationProvider
	 */
	boolean hasOngoingEvaluations();
	
	List<MultiEvaluationInstance<T>> getOfflineEvaluations(boolean includeOngoing, boolean includeFinished);
	
	/**
	 * Creates an evaluation, but does not start it. Use 
	 * {@link EvaluationManager#newEvaluation(EvaluationProvider, List, List, Collection)}
	 * instead to create and start an evaluation.
	 * @param input
	 * 		pass exactly one entry per input data type (see {@link #inputDataTypes()})
	 * @param configurations
	 * 		provider specific configurations, see {@link #getConfigurations()}.
	 * @throws IllegalArgumentException
	 * 		if the size of the input list does not match, the list of result types contains 
	 * 		unsupported elements, or the list of configurations contains unsupported elements.
	 * @return
	 */
	MultiEvaluationInstance<T> newEvaluation(List<MultiEvaluationInputGeneric> input, 
			Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
			List<ResultType> resultsRequested);

}
