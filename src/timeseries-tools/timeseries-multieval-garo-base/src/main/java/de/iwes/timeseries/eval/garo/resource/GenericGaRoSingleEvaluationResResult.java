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
package de.iwes.timeseries.eval.garo.resource;

import java.util.Collection;
import java.util.List;

import org.ogema.core.model.Resource;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvaluation;

public class GenericGaRoSingleEvaluationResResult<T extends Resource> extends GenericGaRoSingleEvaluation {
	private GenericGaRoSCoreResResult<T> evalCoreMy;
	protected GenericGaRoSCoreResResult<T> getEvalCore() {
		return evalCoreMy;
	}

	public GenericGaRoSingleEvaluationResResult(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			GenericGaRoSingleEvalProvider provider) {
		super(input, requestedResults, configurations, listener, time, provider);
	}
	
	@Override
	protected void init(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			GenericGaRoSingleEvalProvider providerIn) {
		@SuppressWarnings("unchecked")
		GenericGaRoSingleEvalProviderResResult<T> provider = (GenericGaRoSingleEvalProviderResResult<T>)providerIn;
		String roomId = EvaluationUtils.getStringConfigurationValue("roomId", configurations);
		String roomName = EvaluationUtils.getStringConfigurationValue("roomName", configurations);;
		String gwId = EvaluationUtils.getStringConfigurationValue("gwId", configurations);;
		T resultRes = provider.getResultResource(roomId, roomName, gwId);
		
		this.evalCoreMy = provider.initEval(input, requestedResults, configurations, listener, time, size,
				nrInput, getIdxSumOfPrevious(), startEnd, resultRes, roomId, roomName, gwId);
		evalCoreMy.evalInstance = this;
		values.setEvalContainer(evalCoreMy);		
	}
}
