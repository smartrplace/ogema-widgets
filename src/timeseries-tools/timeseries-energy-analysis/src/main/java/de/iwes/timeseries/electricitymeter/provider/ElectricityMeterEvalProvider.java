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
package de.iwes.timeseries.electricitymeter.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.units.PowerResource;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.BasicEvaluationProvider;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate correlation
 */
@Service(EvaluationProvider.class)
@Component
public class ElectricityMeterEvalProvider implements EvaluationProvider, EvaluationListener {
	
	private final ConcurrentMap<String, OnlineEvaluation> onlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, OnlineEvaluation> onlineFinished = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineFinished = new ConcurrentHashMap<>();
	
	@Reference
	private FrameworkClock clock;

	@Override
	public String id() {
		return "electricityMeterEvalProvider";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Electricity Meter Evaluation provider";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Determines the total energy metered an other statistics";
	}

	@Override
	public List<RequiredInputData> inputDataTypes() {
		return inputList;
	}
	
	@Override
	public void evaluationDone(EvaluationInstance evaluation, Status status) {
		if (evaluation.isOnlineEvaluation()) {
			onlineFinished.put(evaluation.id(), (OnlineEvaluation) evaluation);
			onlineOngoing.remove(evaluation.id());
		} else {
			offlineFinished.put(evaluation.id(), evaluation);
			offlineOngoing.remove(evaluation.id());
		}
	}
		
	@Override
	public List<OnlineEvaluation> getOnlineEvaluations(boolean includeOngoing, boolean includeFinished) {
		final List<OnlineEvaluation> list = new ArrayList<>();
		if (includeOngoing)
			list.addAll(onlineOngoing.values());
		if (includeFinished)
			list.addAll(onlineFinished.values());
		return list;
	}
	
	@Override
	public List<EvaluationInstance> getOfflineEvaluations(boolean includeOngoing, boolean includeFinished) {
		final List<EvaluationInstance> list = new ArrayList<>();
		if (includeOngoing)
			list.addAll(offlineOngoing.values());
		if (includeFinished)
			list.addAll(offlineFinished.values());
		return list;
	}

	
	@Override
	public List<Configuration<?>> getConfigurations() {
		return Collections.emptyList();
	}

	@Override
	public EvaluationInstance newEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations) {
		Objects.requireNonNull(requestedResults);
		Objects.requireNonNull(input);
		if (input.isEmpty() || requestedResults.isEmpty())
			throw new IllegalArgumentException("Input list and requested result types must not be empty");
		final ElectricityMeterEvalEvaluation instance = new ElectricityMeterEvalEvaluation(input, requestedResults, configurations, this, 
				clock != null ? clock.getExecutionTime() : System.currentTimeMillis());
		if (instance.isOnlineEvaluation()) 
			onlineOngoing.put(instance.id(), instance);
		else
			offlineOngoing.put(instance.id(), instance);
		return instance;
	}
	
	@Override
	public boolean hasOngoingEvaluations() {
		return !onlineOngoing.isEmpty() || !offlineOngoing.isEmpty();
	}
	
	private final List<ResultType> types = Collections.unmodifiableList(Arrays.asList(
			ENERGY, BasicEvaluationProvider.COUNTER
	));
	
	@Override
	public List<ResultType> resultTypes() {
		return types;
	}
	
	@Override
	public List<String> getEvaluationIds() {
		final List<String> ids= new ArrayList<>();
		ids.addAll(onlineOngoing.keySet());
		ids.addAll(onlineFinished.keySet());
		ids.addAll(offlineOngoing.keySet());
		ids.addAll(offlineFinished.keySet());
		return ids;
	}
	
	@Override
	public EvaluationInstance getEvaluation(String id) {
		EvaluationInstance instance = onlineOngoing.get(id);
		if (instance != null)
			return instance;
		instance = onlineFinished.get(id);
		if (instance != null)
			return instance;
		instance = offlineOngoing.get(id);
		if (instance != null)
			return instance;
		instance = offlineFinished.get(id);
		if (instance != null)
			return instance;
		return null;
	}

	private static final class ElectricityInput implements RequiredInputData {
		@Override
		public String id() {
			return "powerTimeSeries";
		}
		
		@Override
		public String label(OgemaLocale locale) {
			return "Power time series";
		}
		
		@Override
		public String description(OgemaLocale locale) {
			return "Requires one power as input (usually electricity)";
		}
		
		@Override
		public Class<? extends SingleValueResource> requestedInputType() {
			return PowerResource.class;
		}
		
		@Override
		public int cardinalityMin() {
			return 1;
		}
		
		@Override
		public int cardinalityMax() {
			return Integer.MAX_VALUE;
		}
		
	};
	
	private static final List<RequiredInputData> inputList = Collections.<RequiredInputData> unmodifiableList(Arrays.asList(
			new ElectricityInput()
	));
	
	public final static ResultType ENERGY = new ResultType() {
		
		@Override
		public String label(OgemaLocale locale) {
			return "Energy metered in kWh";
		}
		
		@Override
		public String id() {
			return "energy";
		}
		
		@Override
		public String description(OgemaLocale locale) {
			return "Calculate the total energy represented by a power time series (in kWh)";
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.NUMERIC;
		}
		
		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
	};
}
