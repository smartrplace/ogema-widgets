package de.iwes.timeseries.eval.api.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.api.configuration.StartEndConfiguration;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;

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
    			if (r instanceof SingleValueResult<?>) {
    				final Object value = ((SingleValueResult<?>) r).getValue();
    				result.put(type.id(), value.toString());
    			}
    		}
    	}
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
    public static Collection<ConfigurationInstance> addStartEndTime(long startTime, long endTime, Collection<ConfigurationInstance> configurations) {
        if (configurations == null) {
            configurations = new ArrayList<>();
        }
        ConfigurationInstance config = new DateConfiguration(startTime, StartEndConfiguration.START_CONFIGURATION);
        configurations.add(config);
        config = new DateConfiguration(endTime, StartEndConfiguration.END_CONFIGURATION);
        configurations.add(config);
        return configurations;
    }
}
