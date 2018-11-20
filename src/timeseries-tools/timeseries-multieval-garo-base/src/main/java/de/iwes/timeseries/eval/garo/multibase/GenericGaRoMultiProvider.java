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
package de.iwes.timeseries.eval.garo.multibase;

import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
//import de.iwes.timeseries.server.timeseries.source.ServerTimeseriesSource;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate basic field test evaluations
 */
public class GenericGaRoMultiProvider<P extends GaRoSingleEvalProvider> extends GaRoEvalProvider<GaRoMultiResult> {
	private final P roomEval;
	public final boolean doBasicEval;
	
	public GenericGaRoMultiProvider(P singleProvider, boolean doBasicEval) {
		super(singleProvider.getGaRoInputTypes(), null);
		this.roomEval = singleProvider;
		this.doBasicEval = doBasicEval;
	}
	
	/*abstract protected GenericGaRoMultiEvaluation<P> newGenericGaRoMultiEvaluation(
			List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested);*/
	
	protected GenericGaRoMultiEvaluation<P> newGenericGaRoMultiEvaluation(
			List<MultiEvaluationInputGeneric> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<GaRoMultiResult> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		return new GenericGaRoMultiEvaluation<P>(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw, singleProvider, doBasicEval,
				resultsRequested);
	}

	@Override
	public String id() {
		return "Multi_"+roomEval.id();
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Multi-Eval for "+roomEval.label(locale);
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Multi-eval for provider:"+roomEval.description(locale);
	}

	@Override
	public MultiEvaluationInstance<GaRoMultiResult> createEvaluation(List<MultiEvaluationInputGeneric> input,
			Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
			List<ResultType> resultsRequested) {
		final GenericGaRoMultiEvaluation<P> instance = newGenericGaRoMultiEvaluation(input, configurations, this, 
				resultStepSize, inputTypesFromRoom, inputTypesFromGw,
				roomEval, doBasicEval, resultsRequested);
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<GaRoMultiResultUntyped> resultType() {
		return GaRoMultiResultUntyped.class;
	}
	
	public String getSingleEvalId() {
		return roomEval.id();
	}
	
	@Override
	public boolean executeSuperLevelOnly() {
		if(roomEval instanceof GaRoSingleEvalProviderPreEvalRequesting) {
			GaRoSingleEvalProviderPreEvalRequesting pre = (GaRoSingleEvalProviderPreEvalRequesting)roomEval;
			return pre.executeSuperLevelOnly();
		}
		return false;
	}
	
	@Override
	protected void performSuperEval(AbstractSuperMultiResult<GaRoMultiResult> destination) {
		if(roomEval instanceof GaRoSingleEvalProviderPreEvalRequesting) {
			GaRoSingleEvalProviderPreEvalRequesting pre = (GaRoSingleEvalProviderPreEvalRequesting)roomEval;
			pre.performSuperEval(destination);
		}
	}
}
