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
package de.iwes.timeseries.provider.outsideTemperature;

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
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesResultImpl;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.QuantileEstimator;
import de.iwes.timeseries.eval.online.utils.TimeSeriesOnlineBuilder;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;

/**
 * Contains state variables for the RoomBaseEvaluation 
 */
class OutsideTemperatureEvalValueContainer extends SpecificEvalValueContainer {
	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
	
	//private final static long ONE_HOUR = 60 * 60 * 1000;
	public final QuantileEstimator upperEstimator = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
			0.9f);
	public final BaseOnlineEstimator baseEstimator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
	public final TimeSeriesOnlineBuilder tsBuilder = new TimeSeriesOnlineBuilder();
	
	/**
	 * @param size
	 * @param nrValves
	 * @param requestedResults
	 * @param input
	 * @param startTimeOpenWindow
	 * 		null, if window is closed initially, the start time for evaluation otherwise
	 */
    OutsideTemperatureEvalValueContainer(final int size, List<ResultType> requestedResults, List<EvaluationInput> input) {
    	super(size, requestedResults, input);
    }
    
    public synchronized Map<ResultType, EvaluationResult> getCurrentResults() {
    	final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
     	for (ResultType rt : requestedResults) {
    		final SingleEvaluationResult singleRes;
    		if (rt == OutsideTemperatureEvalProvider.OUTSIDE_TEMP_AVERAGE90) {
    			singleRes = new SingleValueResultImpl<Float>(rt, upperEstimator.getQuantileMean(true, true), input.get(0).getInputData());
    		} else if (rt == OutsideTemperatureEvalProvider.OUTSIDE_TEMP_AVERAGE) {
    			singleRes = new SingleValueResultImpl<Float>(rt, baseEstimator.getAverage(), input.get(0).getInputData());
    		} else if (rt == OutsideTemperatureEvalProvider.OUTSIDE_TEMP_TIMESERIES) {
    			singleRes = new TimeSeriesResultImpl(rt, tsBuilder.getTimeSeries(), input.get(0).getInputData());
    		} else if (rt == OutsideTemperatureEvalProvider.GAP_TIME) {
    			singleRes = new SingleValueResultImpl<Long>(rt, gapTime, input.get(0).getInputData());
     		} else 
    			throw new IllegalArgumentException("Invalid result type requested: " + (rt != null ? rt.id() : null));
    		results.put(rt, new EvaluationResultImpl(Collections.singletonList(singleRes), rt));
    	}
    	return results;
    }

}