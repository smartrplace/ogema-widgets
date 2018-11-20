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
	 * Like {@link #newEvaluation(EvaluationProvider, List, List, Collection)}, 
	 * but requesting a specific eval id.
	 * @throws UnsupportedOperationException if the provider does not support the method
	 * 		{@link EvaluationProvider#newEvaluation(String, List, List, Collection)}
	 * @throws IllegalArgumentException if an evaluation with the passed id already exists
	 */
	EvaluationInstance newEvaluation(String id, EvaluationProvider provider, List<EvaluationInput> input, List<ResultType> requestedResults, 
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
