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
package de.iwes.timeseries.multi.provider.comfortTemp;

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
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.provider.comfortTemp.ComfortTemperatureEvalProvider;
//import de.iwes.timeseries.server.timeseries.source.ServerTimeseriesSource;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate basic field test evaluations
 */
//@Service(MultiEvaluationProvider.class)
//@Component
@Deprecated
public class CTGaRoEvalProvider extends GaRoEvalProvider<CTGaRoMultiResult> {

	public CTGaRoEvalProvider() {
		super(ComfortTemperatureEvalProvider.INPUT_TYPES,
				null);
	}

	@Override
	public String id() {
		return "Multi_"+ComfortTemperatureEvalProvider.ID;
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Multi-Eval for "+ComfortTemperatureEvalProvider.LABEL;
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Multi-eval for provider:"+ComfortTemperatureEvalProvider.DESCRIPTION;
	}

	public MultiEvaluationInstance<CTGaRoMultiResult> createEvaluation(List<MultiEvaluationInputGeneric> input,
			Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
			List<ResultType> resultsRequested) {
		final CTGaRoEvaluation instance = new CTGaRoEvaluation(input, configurations, this, 
				resultStepSize, inputTypesFromRoom, inputTypesFromGw);
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<CTGaRoMultiResult> resultType() {
		return CTGaRoMultiResult.class;
	}
}
