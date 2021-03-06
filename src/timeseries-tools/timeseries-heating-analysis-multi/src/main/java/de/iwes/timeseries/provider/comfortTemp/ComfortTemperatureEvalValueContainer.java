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
package de.iwes.timeseries.provider.comfortTemp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalValueContainer;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;
import de.iwes.timeseries.eval.online.utils.QuantileEstimator;

/**
 * Contains state variables for the RoomBaseEvaluation 
 */
@Deprecated
class ComfortTemperatureEvalValueContainer extends SpecificEvalValueContainer {
	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
	public final static long MAX_DEVIATION_TIME_ACCEPTED = 60000;
	
	//private final static long ONE_HOUR = 60 * 60 * 1000;
	private final int nrValves;
	public int countMultiValveDeviations;
	public final QuantileEstimator upperEstimator = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
			0.7f);
	public final QuantileEstimator lowerEstimator = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
			0.3f);
	
	public final BaseOnlineEstimator maxEstimator = new BaseOnlineEstimator(true, AverageMode.AVERAGE_ONLY);
	/**
	 * @param size
	 * @param nrValves
	 * @param requestedResults
	 * @param input
	 * @param startTimeOpenWindow
	 * 		null, if window is closed initially, the start time for evaluation otherwise
	 */
    ComfortTemperatureEvalValueContainer(final int size, List<ResultType> requestedResults, List<EvaluationInput> input) {
    	super(size, requestedResults, input);
    	this.nrValves = size;
    }
    
    public synchronized Map<ResultType, EvaluationResult> getCurrentResults() {
    	final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
     	for (ResultType rt : requestedResults) {
    		final SingleEvaluationResult singleRes;
    		if (rt == ComfortTemperatureEvalProvider.COMFORT_TEMP1) {
    			singleRes = new SingleValueResultImpl<Float>(rt, lowerEstimator.getQuantileMean(false, true), input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.COMFORT_TEMP2) {
        		singleRes = new SingleValueResultImpl<Float>(rt, upperEstimator.getQuantileMean(false, false), input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.COMFORT_TEMP3) {
        		singleRes = new SingleValueResultImpl<Float>(rt, maxEstimator.getMaximum(), input.get(0).getInputData());
       		} else if (rt == ComfortTemperatureEvalProvider.SETP_TEMP_AV) {
        		singleRes = new SingleValueResultImpl<Float>(rt, maxEstimator.getAverage(), input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.PERCENTILE10) {
    			singleRes = new SingleValueResultImpl<Float>(rt, lowerEstimator.getQuantileValue(), input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.PERCENTILE90) {
        		singleRes = new SingleValueResultImpl<Float>(rt, upperEstimator.getQuantileValue(), input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.GAP_TIME) {
        		singleRes = new SingleValueResultImpl<Long>(rt, gapTime, input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.SETPOINTS_USED_NUM) {
           		singleRes = new SingleValueResultImpl<Integer>(rt, upperEstimator.size(), input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.MULTI_THERMOSTAT_NUM) {
           		singleRes = new SingleValueResultImpl<Integer>(rt, nrValves, input.get(0).getInputData());
    		} else if (rt == ComfortTemperatureEvalProvider.MULTI_THERMOSTAT_DEVIATIONS_FOUND_NUM) {
    			singleRes = new SingleValueResultImpl<Integer>(rt,countMultiValveDeviations, input.get(0).getInputData());
    		} else 
    			throw new IllegalArgumentException("Invalid result type requested: " + (rt != null ? rt.id() : null));
    		results.put(rt, new EvaluationResultImpl(Collections.singletonList(singleRes), rt));
    	}
    	return results;
    }

}