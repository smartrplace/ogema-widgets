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
package de.iwes.timeseries.eval.api.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.api.configuration.StartEndConfiguration;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesResultImpl;

public class EvalHelperExtended {
	
	public static void addResult(ResultType resultType, List<SingleEvaluationResult> singleResultList,
			 Map<ResultType,EvaluationResult> results) {
		results.put(resultType, new EvaluationResultImpl(singleResultList, resultType));
	}
	
	public static void addSingleResult(ResultType resultType, SingleEvaluationResult singleResult, Map<ResultType,EvaluationResult> results) {
//		List<SingleEvaluationResult> singleResultList = new ArrayList<>();
//		singleResultList.add(singleResult);
//		results.put(resultType, new EvaluationResultImpl(singleResultList, resultType));
		addResult(resultType, Collections.singletonList(singleResult), results);
	}
	
	public static void addSingleResult(ResultType resultType, float value, Map<ResultType,EvaluationResult> results) {
		List<SingleEvaluationResult> singleResultList = new ArrayList<>();
		singleResultList.add(new SingleValueResultImpl<>(resultType, value, Collections.<TimeSeriesData> emptyList()));
		results.put(resultType, new EvaluationResultImpl(singleResultList, resultType));
	}
	
	public static Double getSingleResultValue(ResultType resultType, EvaluationInstance instance) {
		Map<ResultType, EvaluationResult> evalRes = instance.getResults();
		EvaluationResult min = evalRes.get(resultType);
		List<SingleEvaluationResult> minres = min.getResults();
		if(minres.isEmpty()) return null;
		SingleEvaluationResult ser = minres.get(0);
		if(ser instanceof SingleValueResult) {
			@SuppressWarnings("unchecked")
			SingleValueResult<Float> svr = (SingleValueResult<Float>)ser;
			return (double) svr.getValue();
		}
		throw new IllegalStateException("Unexpected result type");
	}
	
	public static Integer getSingleResultInt(ResultType resultType, EvaluationInstance instance) {
		Map<ResultType, EvaluationResult> evalRes = instance.getResults();
		EvaluationResult min = evalRes.get(resultType);
		List<SingleEvaluationResult> minres = min.getResults();
		if(minres.isEmpty()) return null;
		SingleEvaluationResult ser = minres.get(0);
		if(ser instanceof SingleValueResult) {
			@SuppressWarnings("unchecked")
			SingleValueResult<Integer> svr = (SingleValueResult<Integer>)ser;
			return (int) svr.getValue();
		}
		throw new IllegalStateException("Unexpected result type");
	}
	
	public static Long getSingleResultLong(ResultType resultType, EvaluationInstance instance) {
		Map<ResultType, EvaluationResult> evalRes = instance.getResults();
		EvaluationResult min = evalRes.get(resultType);
		List<SingleEvaluationResult> minres = min.getResults();
		if(minres.isEmpty()) return null;
		SingleEvaluationResult ser = minres.get(0);
		if(ser instanceof SingleValueResult) {
			@SuppressWarnings("unchecked")
			SingleValueResult<Long> svr = (SingleValueResult<Long>)ser;
			return (long) svr.getValue();
		}
		throw new IllegalStateException("Unexpected result type");
	}
	
	/**Provide single value results as String into Map, other results are omitted*/
	public static Map<String, String> getResults(EvaluationInstance instance) {
		Map<String, String> result = new HashMap<>();
		final Map<ResultType, EvaluationResult> results = instance.getResults();
    	for (ResultType type : instance.getResultTypes()) {
    		if (results.containsKey(type)) {
    			final SingleEvaluationResult r = results.get(type).getResults().iterator().next();
    			if (r instanceof SingleValueResult<?> && (!(r instanceof TimeSeriesResultImpl))) {
    				final Object value = ((SingleValueResult<?>) r).getValue();
    				result.put(type.id(), value.toString());
    			}
    		}
    	}
    	return result;
	}

	/**Provide time series results as EfficientTimeSeries objects into Map, other results are omitted*/
	public static Map<String, EfficientTimeSeriesArray> getResultsTS(EvaluationInstance instance) {
		Map<String, EfficientTimeSeriesArray> result = new HashMap<>();
		final Map<ResultType, EvaluationResult> results = instance.getResults();
    	for (ResultType type : instance.getResultTypes()) {
    		if (results.containsKey(type)) {
    			final SingleEvaluationResult r = results.get(type).getResults().iterator().next();
    			if (r instanceof TimeSeriesResult) {
    				ReadOnlyTimeSeries value = ((TimeSeriesResult) r).getValue();
    				result.put(type.id(), EfficientTimeSeriesArray.getInstance(value));
    			}
    		}
    	}
    	if(result.isEmpty()) return null;
    	return result;
	}

	public enum PrintMode {
		OverallEval,
		PerGW
	}
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
    /**
     * Add the mandatory start/end time information to the list of
     * configurations or create list with this information if not yet existing
     *
     * @param startTime
     * @param endTime
     * @param configurations may be null if no other configurations shall be set
     * for the evaluation
     * @return list of configurations with start and end time
     */
    public static Collection<ConfigurationInstance> addStartEndTime(long startTime, long endTime,
    		final Collection<ConfigurationInstance> configurations) {
        List<ConfigurationInstance> configurationsRet;
        if (configurations == null) configurationsRet = new ArrayList<>();
        else configurationsRet = new ArrayList<>(configurations);
        ConfigurationInstance config = new DateConfiguration(startTime, StartEndConfiguration.START_CONFIGURATION);
        configurationsRet.add(config);
        config = new DateConfiguration(endTime, StartEndConfiguration.END_CONFIGURATION);
        configurationsRet.add(config);
		return configurationsRet;
    }
}
