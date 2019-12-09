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
package de.iwes.timeseries.eval.base.provider.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.ogema.core.administration.FrameworkClock;

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
 * Base class for {@link EvaluationProvider} implementations. Handles lists of
 * ongoing and completed evaluation and {@link EvaluationListener}s.
 * All non-abstract methods are final except {@link #label(de.iwes.widgets.api.widgets.localisation.OgemaLocale) }
 * and {@link #description(de.iwes.widgets.api.widgets.localisation.OgemaLocale) }
 * which may be overridden to provide better localization.
 */
public abstract class AbstractEvaluationProvider implements EvaluationProvider, EvaluationListener {
	
	private final ConcurrentMap<String, OnlineEvaluation> onlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, OnlineEvaluation> onlineFinished = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineOngoing = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, EvaluationInstance> offlineFinished = new ConcurrentHashMap<>();
    
    protected final String id;
    protected final String defaultLabel;
    protected final String defaultDescription;
	
	protected FrameworkClock clock;

	@Override
	public final String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return defaultLabel;
	}

	@Override
	public String description(OgemaLocale locale) {
		return defaultDescription;
	}
    
    /**
     * @param id widget id
     * @param label widget label
     * @param description evaluation description.
     * @param clock (optional)
     */
    public AbstractEvaluationProvider(FrameworkClock clock, String id, String label, String description) {
        this.clock = clock;
        this.id = id;
        this.defaultLabel = label;
        this.defaultDescription = description;
    }

	@Override
	public abstract List<RequiredInputData> inputDataTypes();
	
	@Override
	public final void evaluationDone(EvaluationInstance evaluation, Status status) {
		if (evaluation.isOnlineEvaluation()) {
			onlineFinished.put(evaluation.id(), (OnlineEvaluation) evaluation);
			onlineOngoing.remove(evaluation.id());
		} else {
			offlineFinished.put(evaluation.id(), evaluation);
			offlineOngoing.remove(evaluation.id());
		}
	}
		
	@Override
	public final List<OnlineEvaluation> getOnlineEvaluations(boolean includeOngoing, boolean includeFinished) {
		final List<OnlineEvaluation> list = new ArrayList<>();
		if (includeOngoing)
			list.addAll(onlineOngoing.values());
		if (includeFinished)
			list.addAll(onlineFinished.values());
		return list;
	}
	
	@Override
	public final List<EvaluationInstance> getOfflineEvaluations(boolean includeOngoing, boolean includeFinished) {
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
	public final EvaluationInstance newEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations) {
		return newEvaluation(null, input, requestedResults, configurations);
	}
	
	@Override
	public EvaluationInstance newEvaluation(String requestedEvalId, List<EvaluationInput> input,
			List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
		Objects.requireNonNull(requestedResults);
		Objects.requireNonNull(input);
		//if (input.isEmpty() || requestedResults.isEmpty())
		//	throw new IllegalArgumentException("Input list and requested result types must not be empty");
		final OnlineEvaluation instance = createEvaluation(requestedEvalId, input, requestedResults, configurations);
		if (instance.isOnlineEvaluation()) 
			onlineOngoing.put(instance.id(), instance);
		else
			offlineOngoing.put(instance.id(), instance);
		return instance;
	}
    
    /**
     * Called by {@link #newEvaluation } to create the actual evaluation processor.
     * @param input passed through from {@link #newEvaluation } call
     * @param requestedResults passed through from {@link #newEvaluation } call
     * @param configurations passed through from {@link #newEvaluation } call
     * @return evaluation implemenation.
     */
    abstract protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations);
	
    protected OnlineEvaluation createEvaluation(String id, List<EvaluationInput> input, List<ResultType> requestedResults, 
			Collection<ConfigurationInstance> configurations) {
    	if (id == null)
    		return createEvaluation(input, requestedResults, configurations);
    	throw new UnsupportedOperationException("Provider does not support request id");
    }
    
	@Override
	public final boolean hasOngoingEvaluations() {
		return !onlineOngoing.isEmpty() || !offlineOngoing.isEmpty();
	}
	
	@Override
	public abstract List<ResultType> resultTypes();
	
	@Override
	public final List<String> getEvaluationIds() {
		final List<String> ids= new ArrayList<>();
		ids.addAll(onlineOngoing.keySet());
		ids.addAll(onlineFinished.keySet());
		ids.addAll(offlineOngoing.keySet());
		ids.addAll(offlineFinished.keySet());
		return ids;
	}
	
	@Override
	public final EvaluationInstance getEvaluation(String id) {
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

}
