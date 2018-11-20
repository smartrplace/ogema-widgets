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
package de.iwes.timeseries.aggregation.provider;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Calculate aggregated time series etc.
 */
@Component(service=EvaluationProvider.class)
public class AggregationProvider implements EvaluationProvider, EvaluationListener {
	
	static final Logger logger = LoggerFactory.getLogger(AggregationProvider.class);
	private final ConcurrentMap<String, OnlineEvaluation> onlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, OnlineEvaluation> onlineFinished = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineFinished = new ConcurrentHashMap<>();
	private final boolean storeResults;
	
	public AggregationProvider() {
		this(true);
	}
	
	public AggregationProvider(boolean storeResults) {
		this.storeResults = storeResults;
	}
	
	@Reference
	private FrameworkClock clock;

	@Override
	public String id() {
		return "aggregationProvider";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Aggregation provider";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Aggregates time series values into standard intervals like 15min, 1h, 1d, etc.";
	}

	@Override
	public List<RequiredInputData> inputDataTypes() {
		return Collections.singletonList(input);
	}
	
	@Override
	public void evaluationDone(EvaluationInstance evaluation, Status status) {
		if (!storeResults)
			return;
		if (evaluation.isOnlineEvaluation()) {
			onlineFinished.put(evaluation.id(), (OnlineEvaluation) evaluation);
			onlineOngoing.remove(evaluation.id());
		} else {
			offlineFinished.put(evaluation.id(), evaluation);
			offlineOngoing.remove(evaluation.id());
		}
	}
	
	@Override
	public boolean hasOngoingEvaluations() {
		return !onlineOngoing.isEmpty() || !offlineOngoing.isEmpty();
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
		final Aggregation instance = new Aggregation(input, requestedResults, configurations, this, 
				clock != null ? clock.getExecutionTime() : System.currentTimeMillis());
		if (storeResults) {
			if (instance.isOnlineEvaluation()) 
				onlineOngoing.put(instance.id(), instance);
			else
				offlineOngoing.put(instance.id(), instance);
		}
		return instance;
	}
	
	// the durations offered in a GUI; in principle, any duration is supported
	private final List<ResultType> types = Collections.unmodifiableList(Arrays.asList(
			new AggregationType(1, ChronoUnit.MINUTES),
			new AggregationType(15, ChronoUnit.MINUTES),
			new AggregationType(1, ChronoUnit.HOURS),
			new AggregationType(1, ChronoUnit.DAYS),
			new AggregationType(1, ChronoUnit.MONTHS),
			new AggregationType(1, ChronoUnit.YEARS)
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
	
	public final static class AggregationType implements ResultType, Comparable<AggregationType> {
		
		private final TemporalUnit unit;
		private final long duration;
		private final int hash;
		private final Duration totalDuration;
		
		public AggregationType(long duration, TemporalUnit unit) {
			Objects.requireNonNull(unit);
			if (duration <= 0)
				throw new IllegalArgumentException("Duration must be positive, got " + duration);
			this.duration = duration;
			this.unit = unit;
			this.hash = (int) duration * unit.hashCode();
			this.totalDuration = Duration.ofMillis(unit.getDuration().toMillis() * duration);
		}

		@Override
		public String label(OgemaLocale locale) {
			return "Aggregation: " + duration + " " + unit.toString();
		}

		@Override
		public String description(OgemaLocale locale) {
			return "Aggregates the values of a single time series into blocks of " + duration + " " + unit.toString();
		}

		@Override
		public ResultStructure resultStructure() {
			return ResultStructure.PER_INPUT;
		}
		
		@Override
		public ValueType valueType() {
			return ValueType.TIME_SERIES;
		};

		@Override
		public Boolean isSingleValueOrArray() {
			return true;
		}
		
		@Override
		public String id() {
			return "agg_" + duration + "_" + unit.toString();
		}
		
		/**
		 * Note: this is NOT the duration in ms; the time unit is determined by
		 * {@link #getUnit()}
		 * @return
		 */
		public long getDuration() {
			return duration;
		}
		
		public TemporalUnit getUnit() {
			return unit;
		}
		
		public long getDurationMs() {
			return totalDuration.toMillis();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof AggregationType))
				return false;
			final AggregationType other = (AggregationType) obj;
			return (other.duration == this.duration && other.unit.equals(this.unit));
		}
		
		@Override
		public int hashCode() {
			return hash;
		}
		
		@Override
		public String toString() {
			return label(OgemaLocale.ENGLISH);
		}
		
		@Override
		public int compareTo(AggregationType other) {
			return totalDuration.compareTo(other.totalDuration);
		}
		
	};
	
	
}
