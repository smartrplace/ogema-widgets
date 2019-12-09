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
package de.iwes.timeseries.eval.garo.api.jaxb;

import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;

@SuppressWarnings("unchecked")
@Deprecated //base class should be sufficient
public class GenericGaRoMultiProviderJAXB<P extends GaRoSingleEvalProvider> extends GenericGaRoMultiProvider<P>{

	public GenericGaRoMultiProviderJAXB(P singleProvider, boolean doBasicEval) {
		super(singleProvider, doBasicEval);
	}

	@Override
	protected GenericGaRoMultiEvaluation<P> newGenericGaRoMultiEvaluation(List<MultiEvaluationInputGeneric> input,
			Collection<ConfigurationInstance> configurations, GaRoEvalProvider<GaRoMultiResult> dataProviderAccess,
			TemporalUnit resultStepSize, GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		return new GenericGaRoMultiEvaluationJAXB<P>(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw, singleProvider, doBasicEval,
				resultsRequested);
	}
}
