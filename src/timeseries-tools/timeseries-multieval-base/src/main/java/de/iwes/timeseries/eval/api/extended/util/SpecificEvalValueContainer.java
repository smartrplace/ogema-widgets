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
package de.iwes.timeseries.eval.api.extended.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;

/**
 * Contains state variables for the RoomBaseEvaluation 
 */
public abstract class SpecificEvalValueContainer {

	protected final List<ResultType> requestedResults;
	protected final List<EvaluationInput> input;
	public final boolean[] isInGap;
	public final boolean[] isInGapByInputType;
	public final boolean[] valueInitDone;
	public boolean allInitDone = false;
	//will be negative when no gap is active in any input series
	public long gapStart;
	public long gapTime = 0;

	public static class EvalDataPointInfo {
		public EvalDataPointInfo(int inputIdx, SampledValueDataPoint dataPoint) {
			this.inputIdx = inputIdx;
			this.dataPoint = dataPoint;
		}
		int inputIdx;
		SampledValueDataPoint dataPoint;
	}
	public EvalDataPointInfo lastValueBeforeIntervalBoundaryInterchanged = null;
	public long lastValidTimeStamp;
	
	
	/**
	 * @param size
	 * @param nrValves
	 * @param requestedResults
	 * @param input
	 * @param startTimeOpenWindow
	 * 		null, if window is closed initially, the start time for evaluation otherwise
	 */
    protected SpecificEvalValueContainer(final int size, List<ResultType> requestedResults, List<EvaluationInput> input) {
    	this.requestedResults = requestedResults;
    	this.input = input;
    	this.gapStart = -1;
    	this.isInGap = new boolean[size];
    	this.isInGapByInputType = new boolean[input.size()];
    	Arrays.fill(isInGap, true);
    	this.valueInitDone = new boolean[input.size()];
    	Arrays.fill(valueInitDone, false);
    }
    
    public abstract Map<ResultType, EvaluationResult> getCurrentResults();

}