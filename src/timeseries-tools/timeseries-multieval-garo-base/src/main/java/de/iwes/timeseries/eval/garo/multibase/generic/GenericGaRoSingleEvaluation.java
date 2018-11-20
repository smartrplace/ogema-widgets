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
package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeParam;

public class GenericGaRoSingleEvaluation extends SpecificEvalBaseImpl<GenericGaRoSingleEvalValueContainer> {
	public static final long MAX_DATA_INTERVAL = 3600*1000;
	
	private List<ResultListener> interMediateListeners = new ArrayList<>();
	private GenericGaRoEvaluationCore evalCore;
	private final long[] maximumGapTimeAccepted;
	protected GenericGaRoEvaluationCore getEvalCore() {
		return evalCore;
	}
	
	@Override
	protected GenericGaRoSingleEvalValueContainer initValueContainer(List<EvaluationInput> input) {
		List<GenericGaRoResultType> requestedResultsGaRo = new ArrayList<>();
		for(ResultType r: requestedResults) {
			requestedResultsGaRo.add((GenericGaRoResultType) r);
		}
		//requestedResultsGaRo.addAll((Collection<? extends GenericGaRoResultType>) requestedResults);
		GenericGaRoSingleEvalValueContainer result = new GenericGaRoSingleEvalValueContainer(size, requestedResultsGaRo , requestedResults, input);
		return result;
	}

	public GenericGaRoSingleEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			GenericGaRoSingleEvalProvider provider) {
		this(input, requestedResults, configurations, listener, time, provider, null);
	}
	
	public GenericGaRoSingleEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			GenericGaRoSingleEvalProvider provider, long[] maximumGapTimeAccepted) {
		super(input, requestedResults, configurations, listener, time,
				provider.id(), provider.inputDataTypes().size(), getisOptional(provider.getGaRoInputTypes())); //"RoomBaseEvaluation", 3);
		init(input, requestedResults, configurations, listener, time, provider);
	    nextFixedTimeStep = getNextFixedTimeStamp(startEnd[0]);
	    this.maximumGapTimeAccepted = maximumGapTimeAccepted;
	}
	
	private static boolean[] getisOptional(GaRoDataTypeI[] gaRoDataTypeIs) {
		boolean[] result = new boolean[gaRoDataTypeIs.length];
		for(int i=0; i<gaRoDataTypeIs.length; i++) {
			GaRoDataTypeI type = gaRoDataTypeIs[i];
			if(type instanceof GaRoDataTypeParam) {
				GaRoDataTypeParam gtp = (GaRoDataTypeParam)type;
				result[i] = !gtp.isRequired();
			} else result[i] = false;
		}
		return result;
	}
	
	@Override
	protected void finishConstructor() {}
	
	protected void init(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			GenericGaRoSingleEvalProvider provider) {
		if(evalCore == null) {
			this.evalCore = provider.initEval(input, requestedResults, configurations, listener, time, size,
					nrInput, idxSumOfPrevious, startEnd);
			evalCore.evalInstance = this;
			values.setEvalContainer(evalCore);
		}
		if(values != null) values.setEvalContainer(evalCore);
	}
	
	@Override
	protected long maximumGapTimeAccepted(int idxOfRequestedInput) {
		if(maximumGapTimeAccepted == null) return MAX_DATA_INTERVAL;
		else return maximumGapTimeAccepted[idxOfRequestedInput];
	}
	
	@Override
	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
			int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		GenericGaRoEvaluationCore evalCoreLoc = getEvalCore();
		evalCoreLoc.gapTime = values.gapTime;
		evalCoreLoc.processValue(idxOfRequestedInput, idxOfEvaluationInput, totalInputIdx,
				timeStamp, sv, dataPoint, duration);
	}
	
	@Override
	protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput, int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		getEvalCore().gapNotification(idxOfRequestedInput, idxOfEvaluationInput, totalInputIdx, timeStamp, sv, dataPoint, duration);
	}

	@Override
	public void addIntermediateResultListener(ResultListener listener) {
		interMediateListeners.add(listener);
	}
	
	public void callListeners(ResultType type, long timeStamp, float value) {
		SampledValue sv = new SampledValue(new FloatValue(value), timeStamp, Quality.GOOD);
		for(ResultListener listen: interMediateListeners) {
			listen.resultAvailable(type, sv);
		}
	}

	public boolean isRequested(GenericGaRoResultType resultType) {
		return requestedResults.contains(resultType);
	}
	
	@Override
	protected long getNextFixedTimeStamp(long currentTimeStep) {
		return getEvalCore().getNextFixedTimeStamp(currentTimeStep);
	}
}
