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
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeParam;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;

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
    
	//copied from SpecificEvalBaseImpl
    protected int getRequiredInputIdx(int totalIdx) {
    	int[] idxSumOfPrevious = evalInstance.getIdxSumOfPrevious();
        for(int i=0; i<idxSumOfPrevious.length; i++) {
        	if(totalIdx < idxSumOfPrevious[i+1]) {
        		return i;
        	}
        }
        throw new IllegalStateException("TotalIdx out of range!");
    }
    protected int getEvaluationInputIdx(int totalIdx) {
    	int[] idxSumOfPrevious = evalInstance.getIdxSumOfPrevious();
        for(int i=0; i<idxSumOfPrevious.length; i++) {
        	if(totalIdx < idxSumOfPrevious[i+1]) {
        		return (totalIdx - idxSumOfPrevious[i]);
        	}
        }
        throw new IllegalStateException("TotalIdx out of range!");
    }
    
    public String getTimeSeriesId(int totalIdx, GaRoSingleEvalProvider evalProvider) {
		int idxOfRequestedInput = getRequiredInputIdx(totalIdx);
		int idxOfEvaluationInput = getEvaluationInputIdx(totalIdx);
		GaRoDataTypeI[] typeList = evalProvider.getGaRoInputTypes();
		if(typeList[idxOfRequestedInput] instanceof GaRoDataTypeParam) {
			GaRoDataTypeParam type = (GaRoDataTypeParam) typeList[idxOfRequestedInput];
			String ts = type.inputInfo.get(idxOfEvaluationInput).id();
			return ts;
		}
		return null;
    }
    /** If a short id is available it is typically returned by this method*/
    public String getDeviceName(int totalIdx, GaRoSingleEvalProvider evalProvider) {
		int idxOfRequestedInput = getRequiredInputIdx(totalIdx);
		int idxOfEvaluationInput = getEvaluationInputIdx(totalIdx);
		GaRoDataTypeI[] typeList = evalProvider.getGaRoInputTypes();
		if(typeList[idxOfRequestedInput] instanceof GaRoDataTypeParam) {
			GaRoDataTypeParam type = (GaRoDataTypeParam) typeList[idxOfRequestedInput];
			//String ts = type.inputInfo.get(idxOfEvaluationInput).id();
			String ts = type.deviceInfo.get(idxOfEvaluationInput).getDeviceName();
			return ts;
		}
		return null;
    }
    public String getDeviceResourceLocation(int totalIdx, GaRoSingleEvalProvider evalProvider) {
		int idxOfRequestedInput = getRequiredInputIdx(totalIdx);
		int idxOfEvaluationInput = getEvaluationInputIdx(totalIdx);
		GaRoDataTypeI[] typeList = evalProvider.getGaRoInputTypes();
		if(typeList[idxOfRequestedInput] instanceof GaRoDataTypeParam) {
			GaRoDataTypeParam type = (GaRoDataTypeParam) typeList[idxOfRequestedInput];
			//String ts = type.inputInfo.get(idxOfEvaluationInput).id();
			String ts = type.deviceInfo.get(idxOfEvaluationInput).getDeviceResourceLocation();
			return ts;
		}
		return null;
    }
    public GaRoDataTypeParam getTimeSeriesType(int totalIdx, GaRoSingleEvalProvider evalProvider) {
		int idxOfRequestedInput = getRequiredInputIdx(totalIdx);
		//int idxOfEvaluationInput = getEvaluationInputIdx(totalIdx);
		GaRoDataTypeI[] typeList = evalProvider.getGaRoInputTypes();
		if(typeList[idxOfRequestedInput] instanceof GaRoDataTypeParam) {
			GaRoDataTypeParam type = (GaRoDataTypeParam) typeList[idxOfRequestedInput];
			return type;
		}
		return null;
    }
}
