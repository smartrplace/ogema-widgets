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
package de.iwes.timeseries.multi.provider.garoBase;

import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
//import de.iwes.timeseries.server.timeseries.source.ServerTimeseriesSource;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate basic field test evaluations
 */
@Service(MultiEvaluationProvider.class)
@Component
public class GaRoBaseMultiProvider extends GaRoEvalProvider<GaRoBaseMultiResult> {

	public GaRoBaseMultiProvider() {
		super(new GaRoDataType[]{
				GaRoDataType.Any},
				null);
	}

	@Override
	public String id() {
		return "BasicDataEvaluation";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Basic Smart Home Field Test Evaluation";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Summarizes basic evaluation and gap analysis over all gateways available";
	}

	public MultiEvaluationInstance<GaRoBaseMultiResult> createEvaluation(List<MultiEvaluationInputGeneric> input,
			Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
			List<ResultType> resultsRequested) {
		final GaRoBaseMultiEvaluation instance = new GaRoBaseMultiEvaluation(input, configurations, this, 
				resultStepSize, inputTypesFromRoom, inputTypesFromGw);
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<GaRoBaseMultiResult> resultType() {
		return GaRoBaseMultiResult.class;
	}
}
