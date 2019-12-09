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
package de.iwes.timeseries.provider.heatingloss;

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

/**
 * Contains state variables for the RoomBaseEvaluation 
 */
class HeatLossEvalValueContainer extends SpecificEvalValueContainer {
	public final static float HEATING_LIMIT_FIXED = 15 + 273.15f;
	public final static float DELTA_T_HEATING_LIMIT_FIXED = 5;
	
	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
	
	//private final static long ONE_HOUR = 60 * 60 * 1000;
	//BaseOnlineEstimator dailyAvEst = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
	float dailyAv = -99;
	float dailyComfortTemp = -9999;
	BaseOnlineEstimator heatingFigureIntegrator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
	BaseOnlineEstimator heatingDegreeDaysIntegrator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
	BaseOnlineEstimator heatingDegreeDaysIntegratorLowered = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
	
	BaseOnlineEstimator setPointRelativeIntegrator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
	BaseOnlineEstimator setPointReductionIntegrator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
	/**
	 * @param size
	 * @param nrValves
	 * @param requestedResults
	 * @param input
	 * @param startTimeOpenWindow
	 * 		null, if window is closed initially, the start time for evaluation otherwise
	 */
    HeatLossEvalValueContainer(final int size, List<ResultType> requestedResults, List<EvaluationInput> input) {
    	super(size, requestedResults, input);
    }
    
    public synchronized Map<ResultType, EvaluationResult> getCurrentResults() {
    	final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
     	for (ResultType rt : requestedResults) {
    		final SingleEvaluationResult singleRes;
    		if (rt == HeatLossEvalProvider.DAILY_TEMPERATURE_FIGURE) {
    			float val;
    			if(dailyAv <= HEATING_LIMIT_FIXED)
    				//This is the same now for all. The average per day times one day evaluation period is the same number
    				val = HEATING_LIMIT_FIXED - dailyAv;
    			else
    				val = 0;
   				singleRes = new SingleValueResultImpl<Float>(rt, val, input.get(0).getInputData());
    		} else if (rt == HeatLossEvalProvider.HEATING_DEGREE_DAYS) {
    			float val = dailyComfortTemp - DELTA_T_HEATING_LIMIT_FIXED;
    			if(dailyAv <= val) {
    				//This is the same now for all. The average per day times one day evaluation period is the same number
    				//val = HEATING_LIMIT_FIXED - dailyAv;
    			} else
    				val = 0;
   				singleRes = new SingleValueResultImpl<Float>(rt, val, input.get(0).getInputData());

    		} else if (rt == HeatLossEvalProvider.HEATING_DEGREE_DAYS_LOWERED) {
    			singleRes = new SingleValueResultImpl<Float>(rt,heatingDegreeDaysIntegrator.getAverage(), input.get(0).getInputData());
    		} else if (rt == HeatLossEvalProvider.HEATING_DEGREE_DAYS_LOWERED) {
    			singleRes = new SingleValueResultImpl<Float>(rt, heatingDegreeDaysIntegratorLowered.getAverage(), input.get(0).getInputData());
    		} else if (rt == HeatLossEvalProvider.SETPOINT_RELATIVE_AV) {
    			singleRes = new SingleValueResultImpl<Float>(rt, setPointRelativeIntegrator.getAverage(), input.get(0).getInputData());
    		} else if (rt == HeatLossEvalProvider.SETPOINT_REDUCTION_AV) {
    			singleRes = new SingleValueResultImpl<Float>(rt, setPointReductionIntegrator.getAverage(), input.get(0).getInputData());
    		} else if (rt == HeatLossEvalProvider.GAP_TIME) {
    			singleRes = new SingleValueResultImpl<Long>(rt, gapTime, input.get(0).getInputData());
     		} else 
    			throw new IllegalArgumentException("Invalid result type requested: " + (rt != null ? rt.id() : null));
    		results.put(rt, new EvaluationResultImpl(Collections.singletonList(singleRes), rt));
    	}
    	return results;
    }

}