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
package de.iwes.timeseries.eval.base.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.model.simple.SingleValueResource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate average, std dev. etc.
 */
@Component(service=EvaluationProvider.class)
public class BasicEvaluationProvider implements EvaluationProvider, EvaluationListener {
	
	private final ConcurrentMap<String, OnlineEvaluation> onlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, OnlineEvaluation> onlineFinished = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineFinished = new ConcurrentHashMap<>();
	
	@Reference
	private FrameworkClock clock;

	@Override
	public String id() {
		return "basicEvalProvider";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Basic evaluation provider";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Calculates the average and extremal values of the input time series, and counts the data points.";
	}

	@Override
	public List<RequiredInputData> inputDataTypes() {
		return Collections.singletonList(input);
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
		final BasicEvaluation instance = new BasicEvaluation(input, requestedResults, configurations, this, 
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
			MAX_TYPE, MIN_TYPE, COUNTER, AVERAGE, INTEGRAL, NON_GAPTIME));
	
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

	private static final RequiredInputData input = new RequiredInputData() {
		
		@Override
		public String id() {
			return "generic";
		}
		
		@Override
		public String label(OgemaLocale locale) {
			return "Generic time series";
		}
		
		@Override
		public String description(OgemaLocale locale) {
			return "Requires one or multiple numerical time series as input";
		}
		
		@Override
		public Class<? extends SingleValueResource> requestedInputType() {
			return SingleValueResource.class;
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
	
	public final static ResultType MAX_TYPE = new ResultType() {

		@Override
		public String label(OgemaLocale locale) {
			return "Maximum value";
		}

		@Override
		public String description(OgemaLocale locale) {
			return "Determines the maximum value of a single time series";
		}

		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.NUMERIC;
		};
		
		@Override
		public String id() {
			return "max";
		}

		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
		
	};
	
	public final static ResultType MIN_TYPE = new ResultType() {

		@Override
		public String label(OgemaLocale locale) {
			return "Minimum value";
		}

		@Override
		public String description(OgemaLocale locale) {
			return "Determines the minimum value of a single time series";
		}

		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.NUMERIC;
		};
		
		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
		
		@Override
		public String id() {
			return "min";
		}
		
	};
	
	public final static ResultType COUNTER = new ResultType() {

		@Override
		public String label(OgemaLocale locale) {
			return "Number data points";
		}

		@Override
		public String description(OgemaLocale locale) {
			return "Counts the number of data points in the evaluated interval";
		}

		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.NUMERIC;
		};
		
		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
		
		@Override
		public String id() {
			return "count";
		}
		
	};
	
	public final static ResultType AVERAGE = new ResultType() {

		@Override
		public String label(OgemaLocale locale) {
			return "Average";
		}

		@Override
		public String description(OgemaLocale locale) {
			return "Determines the average value in the evaluated interval";
		}

		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.NUMERIC;
		};
		
		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
		
		@Override
		public String id() {
			return "avg";
		}
		
	};
	
	/**
	 * TODO configure the time unit
	 */
	public final static ResultType INTEGRAL = new ResultType() {

		@Override
		public String label(OgemaLocale locale) {
			return "Integral";
		}

		@Override
		public String description(OgemaLocale locale) {
			return "Integrates the time series";
		}

		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.NUMERIC;
		};
		
		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
		
		@Override
		public String id() {
			return "itg";
		}
		
	};
	public final static ResultType NON_GAPTIME = new ResultType() {

		@Override
		public String label(OgemaLocale locale) {
			return "NonGapTime";
		}

		@Override
		public String description(OgemaLocale locale) {
			return "Time in ms for which valid values were found in the interval";
		}

		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.NUMERIC;
		};
		
		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
		
		@Override
		public String id() {
			return "nongaptime";
		}
		
	};
    
}
