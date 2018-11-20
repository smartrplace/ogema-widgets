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

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;

public abstract class GenericGaRoEvaluationCore {
	public GenericGaRoSingleEvaluation evalInstance;
	public long gapTime;
    /** Process new value
     * 
     * @param idxOfRequestedInput index of requested input
     * @param idxOfEvaluationInput index of time series within the requested input
     * @param totalInputIdx required for some access methods of EvaluationBaseImpl
     * @param timeStamp time of the current value
     * @param sv current SampledValue
     * @param dataPoint access to the input data structure
     * @param duration 
     */
    protected abstract void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx,
    		long timeStamp, SampledValue sv, SampledValueDataPoint dataPoint, long duration);

    /** See {@link SpecificEvalBaseImpl#gapNotification(int, int , int ,long , SampledValue , SampledValueDataPoint , long)}
     */
    protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx,
    		long timeStamp, SampledValue sv, SampledValueDataPoint dataPoint, long duration) {}
    
    /** See {@link SpecificEvalBaseImpl#getNextFixedTimeStamp(long)}
     */
    public long getNextFixedTimeStamp(long currentTime) {
		return -1;
	}

}
