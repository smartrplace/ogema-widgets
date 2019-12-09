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
package de.iwes.timeseries.roomeval.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;

/**
 * Contains state variables for the RoomBaseEvaluation 
 */
class RoomBaseValueContainer {

	private final static long ONE_HOUR = 60 * 60 * 1000;
	private final List<ResultType> requestedResults;
	private final List<EvaluationInput> input;
	private final int nrValves;
    // state variables; all synchronized on this
	// integral of valve state over time
	float valveOpenMillis = 0;
	// integral of valve state over time excluding intervals where window is closed
	float valveOpenWindowOpenMillis = 0;
	// TODO initialize!
	int openWindowsCounter = 0;
	final List<Long> windowOpenDurations = new ArrayList<>(20); // in ms
	final boolean[] isInGap;
	// null when window is closed, opening time stamp otherwise; TODO initialize 
	Long lastOpened; 
    // if true, then the valve position is assumed in the range 0-100, instead of 0-1 as specified
	// if null, then this has not been determined yet
    Boolean usingScaledValvePosition;
	
	/**
	 * @param size
	 * @param nrValves
	 * @param requestedResults
	 * @param input
	 * @param startTimeOpenWindow
	 * 		null, if window is closed initially, the start time for evaluation otherwise
	 */
    RoomBaseValueContainer(final int size, final int nrValves, List<ResultType> requestedResults, List<EvaluationInput> input, Long startTimeOpenWindow) {
    	this.requestedResults = requestedResults;
    	this.input = input;
    	this.nrValves = nrValves;
    	this.isInGap = new boolean[size];
    	Arrays.fill(isInGap, true);
   		lastOpened = startTimeOpenWindow; 
   		lastOpened = startTimeOpenWindow; 
    }
    
    synchronized Map<ResultType, EvaluationResult> getCurrentResults() {
    	final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();
    	final float valveScale = (usingScaledValvePosition != null && usingScaledValvePosition) ? 100 : 1;
    	for (ResultType rt : requestedResults) {
    		final SingleEvaluationResult singleRes;
    		if (rt == RoomBaseEvalProvider.VALVE_HOURS_TOTAL) {
    			singleRes = new SingleValueResultImpl<Float>(rt, valveOpenMillis / ONE_HOUR / nrValves / valveScale, input.get(2).getInputData());
    		} else if (rt == RoomBaseEvalProvider.VALVE_HOURS_WINDOWPEN) {
    			final List<TimeSeriesData> in = new ArrayList<>();
    			in.addAll(input.get(2).getInputData());
    			in.addAll(input.get(1).getInputData());
    			singleRes = new SingleValueResultImpl<Float>(rt, valveOpenWindowOpenMillis / ONE_HOUR / nrValves / valveScale, in);
    		} else if (rt == RoomBaseEvalProvider.WINDOWPEN_DURATION_AV) {
    			long duration = 0;
    			for (Long d : windowOpenDurations)
    				duration += d;
    			singleRes = new SingleValueResultImpl<Long>(rt, duration/(Math.max(1, windowOpenDurations.size())), input.get(1).getInputData());
    		} else if (rt == RoomBaseEvalProvider.WINDOWPEN_DURATION_NUM) {
    			singleRes = new SingleValueResultImpl<Integer>(rt, windowOpenDurations.size(), input.get(1).getInputData());
    		} else 
    			throw new IllegalArgumentException("Invalid result type requested: " + (rt != null ? rt.id() : null));
    		results.put(rt, new EvaluationResultImpl(Collections.singletonList(singleRes), rt));
    	}
    	return results;
    }

}