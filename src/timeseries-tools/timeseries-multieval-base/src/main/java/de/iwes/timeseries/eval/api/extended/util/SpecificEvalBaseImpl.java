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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalValueContainer.EvalDataPointInfo;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;

/** Base class for specific base evaluations requesting a fixed number of input types
 *
 */
public abstract class SpecificEvalBaseImpl<T extends SpecificEvalValueContainer> extends EvaluationBaseImpl {

    protected final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources
    protected final String id;
    protected final int[] nrInput;
    private final int[] idxSumOfPrevious;
    protected final boolean[] isOptional;
    protected final long[] startEnd;
    
    protected long nextFixedTimeStep;
    protected long lastTimeStampEvaluated;

    //state variables
    protected final T values;
    /** Provide value container, will be called from super-constructor*/
    protected abstract T initValueContainer(List<EvaluationInput> input);
    /** Process new value
     * 
     * @param idxOfRequestedInput index of requested input
     * @param idxOfEvaluationInput index of time series within the requested input
     * @param totalInputIdx required for some access methods of EvaluationBaseImpl
     * @param timeStamp time of the current value
     * @param sv current SampledValue
     * @param dataPoint access to the input data structure
     * @param duration duration of the current value based on the assumption of InterpolationMode.STEPS.
     * 		See {@link #gapNotification()} for details. The duration is the time difference to the next SampledValue
     * 		in any input time series, so the time difference to the next call of processValue. If two or more values have
     * 		exactly the same time stamp for all calls except for the last one the duration is zero. In this way 
     * 		the sum of all durations reported is the total time span evaluated (if no gaps occur).
     * 		As we are assuming STEPS the sv value is
     * 		applicable from timeStamp until timestamp+duration
     */
    protected abstract void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx,
    		long timeStamp, SampledValue sv, SampledValueDataPoint dataPoint, long duration);
    /** The concept of gap notification is based on the concept that the incoming values are treated as
     * InterpolationMode.STEPS. Although for some input data LINEAR may be much more appropriate, this
     * mode is much more difficult to process correctly in a general case than STEPS. So if the evaluation
     * needs to process gap notification and duration correctly based on LINEAR this currently has to
     * be implemented in the application.<br>
     * If the maximum gap time for the respective input is exceeded, the time series is considered in
     * gap and the respective value is considered not available. This may highly depend on the input type.
     * For temperature sensors usually a certain rate of measurement data can be expected whereas a 
     * window sensor may only send data when the state is changed, so no gap detection based on the
     * interval between two values can be performed. This is also the standard value if the method is
     * not overwritten.<br>
     * For standard GaRo evaluations a default of GenericGaRoSingleEvaluation.MAX_DATA_INTERVAL is used by
     * default for all input time series and the values can be set by overwriting
     * GenericGaRoSingleEvaluation.getMaximumGapTimes() in the provider definition.<br>
     * If more than one input series is used and one input is in gap state then all other inputs during the
     * gap phase will be reported with zero duration. So the values are provided, but indicated that
     * they shall not be used until the gap is finished.<br>
     * A gap shall be detected for an input type when none of the time series belonging to this input type have an
     * input value for the respective period. A gap is detected for the time between the start time of the
     * evaluation and at least one value for each input type has been reported.<br>
     * Option required inputs (currently only supported for GaRo input see GaRoDataTypeParam or
     * via isOptional in this class) are not included into overall gab analysis meaning that durations
     * of other inputs are still reported even if such input is missing or has gaps.
     */
    protected long maximumGapTimeAccepted(int idxOfRequestedInput) {
    	return Long.MAX_VALUE;
    }
    
    /** See {@link #maximumGapTimeAccepted(int)} regarding the concept of gaps. This method is called when a
     * new gap is started. Additional calls may occur when new time series go into gap state during an
     * ongoing gap.
     * @param duration note that the duration of a gap may not be foreseeable, especially in an online
     * 		evaluation. So check for negative values indicating that the duration is not known when
     * 		using this value
     */
    protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx,
    		long timeStamp, SampledValue sv, SampledValueDataPoint dataPoint, long duration) {}
 
    /** The method is evaluated once at the beginning of the evaluation. If a positive value is
     * returned at the time specified an additional call is issued and the next time to
     * call is requested. If a non-positive value is received once no further requests are made.
     * The input index will be -1 then.
     */
    protected long getNextFixedTimeStamp(long currentTimeStep) {return -1;};
    
    protected void finishConstructor() {
        nextFixedTimeStep = getNextFixedTimeStamp(startEnd[0]);
        lastTimeStampEvaluated = startEnd[0];
    }
    
    public SpecificEvalBaseImpl(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			String evalId, int requestedInputNum, boolean[] isOptional) {
    	super(input, requestedResults, configurations, listener, time);
	    this.id = evalId + "_" + idcounter.incrementAndGet();
	    startEnd = EvaluationUtils.getStartAndEndTime(configurations, input, false);
	    if(input.isEmpty()) {
	    	values = initValueContainer(input);
	    	nrInput = new int[0];
	    	this.isOptional = new boolean[0];
	    	idxSumOfPrevious = new int[0];
	    	return;
	    }
	    if (input.size() != requestedInputNum)
        	throw new IllegalArgumentException("Expecting exactly "+requestedInputNum+" types of input time series, got " + input.size());
        nrInput = new int[requestedInputNum];
        idxSumOfPrevious = new int[requestedInputNum+1];
        if(isOptional == null)
        	this.isOptional = new boolean[requestedInputNum];
        else
        	this.isOptional = isOptional;
        int sum = 0;
        for(int i=0; i<requestedInputNum; i++) {
        	this.nrInput[i] = input.get(i).getInputData().size();
        	this.getIdxSumOfPrevious()[i] = sum;
        	sum += nrInput[i];
        }
        this.getIdxSumOfPrevious()[requestedInputNum] = sum;
        values = initValueContainer(input);
        
        finishConstructor();
    }
    
    @Override
    public String id() {
        return id;
    }

    @Override
    protected Map<ResultType, EvaluationResult> getCurrentResults() {
    	return values.getCurrentResults();
    }
    
    protected int getTotalInputIdx(int idxOfRequestedInput, int idxOfEvaluationInput) {
    	return getIdxSumOfPrevious()[idxOfRequestedInput] + idxOfEvaluationInput;
    }
    
    private int getRequiredInputIdx(int totalIdx) {
        for(int i=0; i<nrInput.length; i++) {
        	if(totalIdx < getIdxSumOfPrevious()[i+1]) {
        		return i;
        	}
        }
        throw new IllegalStateException("TotalIdx out of range!");
    }
    
    @Override
    protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
    	if (requestedResults.isEmpty())
    		return;
    	synchronized (values) {
    		int maxIdx = dataPoint.getElements().entrySet().size()-1;
    		int sameTimePointIdx = 0;
            long duration = -Long.MAX_VALUE;
            long tnext = -999;
            long t = -999;
            long limitFixedTS = -999;
	        for (Map.Entry<Integer, SampledValue> entry : dataPoint.getElements().entrySet()) {
	            final int idx = entry.getKey();
              	final SampledValue sv = entry.getValue();
  	            // In first loop iteration we calculate the duration
            	t = sv.getTimestamp();
  	            if(sameTimePointIdx == 0) {
  	  	            if(values.lastValueBeforeIntervalBoundaryInterchanged != null) {
  		            	if(t < values.lastValidTimeStamp) continue;
  		            	final SampledValueDataPoint dp = values.lastValueBeforeIntervalBoundaryInterchanged.dataPoint;
  	            		stepInternal(values.lastValueBeforeIntervalBoundaryInterchanged.inputIdx,
  	            				dp, t-dp.getTimestamp(), dp.getTimestamp()); //dp, t-dp.getTimestamp(), t);
  	            		lastTimeStampEvaluated = dp.getTimestamp();
  		            	values.lastValueBeforeIntervalBoundaryInterchanged = null;
  		            }
 	             	if((nextFixedTimeStep > 0)&&(nextFixedTimeStep < t)) {
 	            		//This should only occur at the beginning of the evaluation. In most cases the
 	            		//call is not really relevantas the evaluation has no data yet
 	            		while(nextFixedTimeStep < t) {
 	            			final long durationHere;
 	            			final long newFixedTimeStep = getNextFixedTimeStamp(nextFixedTimeStep);
 	            			if((newFixedTimeStep >= t) || (newFixedTimeStep < 0))
 	            				durationHere = t - nextFixedTimeStep;
 	            			else durationHere = newFixedTimeStep - nextFixedTimeStep;
 	            			//long durationHere = nextFixedTimeStep - lastTimeStampEvaluated;
 	            			stepInternal(-1, dataPoint, durationHere, nextFixedTimeStep);
 	            			lastTimeStampEvaluated = nextFixedTimeStep;
 	            			nextFixedTimeStep = newFixedTimeStep;
 	            		}
 	            	}
 	             	tnext = dataPoint.getNextTimestamp();
 	             	if(tnext == Long.MAX_VALUE) { //if(!dataPoint.hasNext(idx) || tnext == Long.MAX_VALUE) {
 	             		duration = startEnd[1] - t;
 	             		//duration = t - dataPoint.getPreviousTimestamp();
 	             	} else {
 	             		duration = dataPoint.getNextTimestamp() - t;
 	             	}
 	             	if(duration < 0) {
 	             		System.out.println("TODO: Interval boundaries interchanged (3) !!");
 	             		values.lastValueBeforeIntervalBoundaryInterchanged = new EvalDataPointInfo(idx, dataPoint);
 	             		values.lastValidTimeStamp =t;
 	             		continue;
 	             	}
 	             	if(nextFixedTimeStep > 0) {
 	             		if(startEnd[1] < tnext) limitFixedTS = startEnd[1];
 	             		else limitFixedTS = tnext;
 	             		if(nextFixedTimeStep < limitFixedTS) {
 	             			if(t > nextFixedTimeStep) {
 	             				//this should never occur
 	             				throw new IllegalStateException("we tried to make sure that nextFixedTimeStep is beyond t here!");
 	             			}
 	             			duration = nextFixedTimeStep - t;
 	             		}
 	             	}
 	           } //if(sameTimePointIdx == 0)
	           stepInternal(idx, dataPoint, (sameTimePointIdx==maxIdx)?duration:0, t);
	           lastTimeStampEvaluated = t;
 	           sameTimePointIdx++;
	        } //for

	        if((t < 0)||((tnext < 0)&&(t >= values.lastValidTimeStamp)))
	        	throw new IllegalStateException(" t:"+t+"tnext"+tnext);
	        
      	    if(nextFixedTimeStep > 0) {
    	        if((limitFixedTS < 0))
    	        	throw new IllegalStateException("limit:"+limitFixedTS+" t:"+t+"tnext"+tnext);
 	           while((nextFixedTimeStep > 0) && (nextFixedTimeStep < limitFixedTS)) {
 	        	   final long durationHere;
 	        	   final long newFixedTimeStep = getNextFixedTimeStamp(nextFixedTimeStep);
 	        	   if((newFixedTimeStep >= limitFixedTS) || (newFixedTimeStep < 0))
 	        		   durationHere = limitFixedTS - nextFixedTimeStep;
 	        	   else durationHere = newFixedTimeStep - nextFixedTimeStep;
 	        	   stepInternal(-1, dataPoint, durationHere, nextFixedTimeStep);
 	        	   lastTimeStampEvaluated = nextFixedTimeStep;
 	        	   nextFixedTimeStep = newFixedTimeStep;
 	           }
 	           /*if(!dataPoint.hasNext(idx) || tnext == Long.MAX_VALUE) {
 	        	   duration -= totalFixedDuration;
 	        	   if(duration < 0) duration = 0;
 	           } else {
 	        	   duration = dataPoint.getNextTimestamp() - lastFixedTimeStamp;
 	           }
        	   if(duration < 0) {
        		   System.out.println("TODO: Interval boundaries interchanged (3) !!");
        		   values.lastValueBeforeIntervalBoundaryInterchanged = new EvalDataPointInfo(idx, dataPoint);
        		   values.lastValidTimeStamp =t;
        		   continue;
        	   }*/
     	   }

    	} //synchronized
    }
    
    /** This method expects input that contains additional calls generated via
     * {@link #getNextFixedTimeStamp(long)} and calls that are corrected from potential
     * time stamp interchanges (so each new time stamp is larger or equal to the previous)-
     * The method does all the gap handling
     * 
     * @param idx
     * @param dataPoint
     * @param duration
     * @param timeStamp
     * @throws Exception
     */
    private void stepInternal(int idx, SampledValueDataPoint dataPoint, long duration,
    		long timeStamp) throws Exception {
        
        final SampledValue sv;
        final boolean quality;
        if(idx == -1) {
        	sv = null;
        	quality = true;
        }
        else  {
        	sv = dataPoint.getElement(idx);//entry.getValue();
        	quality = sv.getQuality() == Quality.GOOD;
        }
        final long  t = timeStamp; //dataPoint.getTimestamp(); //sv.getTimestamp();
        
        SampledValue nextEl = dataPoint.getNextElement(idx);
        long durationOfInput;
        if(nextEl != null)
        	durationOfInput = nextEl.getTimestamp() - t;
        else
        	durationOfInput = duration;
        
//System.out.println("Data point of idx "+idx+" qu:"+quality+" val:"+sv.getValue().getFloatValue());	            
        
        //dataPoint.getElement(0, InterpolationMode.LINEAR);
        
        //Check if we have an initial gap here
        int idxReqInp = -1;
        int idxOfEvaluationInput = -1;
        if(idx == -1) {
        	if((!values.allInitDone) && (values.gapStart < 0) && ((t -startEnd[0]) > 0)) {
                values.gapStart = startEnd[0];       		
        		gapNotification(idxReqInp, -1, -1, t, sv, dataPoint, -1);	
        	}        	
        } else {
        	idxReqInp= getRequiredInputIdx(idx);
        	idxOfEvaluationInput = idx - getIdxSumOfPrevious()[idxReqInp];
        	if((!values.allInitDone) && (!values.valueInitDone[idxReqInp])) {
            	values.valueInitDone[idxReqInp] = true;
            	if((values.gapStart < 0) && ((t -startEnd[0]) > 0)) {  //maximumGapTimeAccepted(idxReqInp) )) {
	            	if(!values.isInGap[idx]) {
	            		gapNotification(idxReqInp, idx - getIdxSumOfPrevious()[idxReqInp], idx, t,
	    	            		sv, dataPoint, -1);	
	            	}
	            	//setValuesIsInGap(idx, false, idxReqInp); //values.isInGap[allIdxForInitCheck] = true;
	                //gap started with start time
	                
	                values.gapStart = startEnd[0]; //t;
	                //we do not look for any further initial gaps
	                //values.allInitDone = true;
	            }
	            boolean found = false;
	            int i=0;
	            for(boolean b: values.valueInitDone) {
	            	if(isOptional[i]) continue;
	            	if(!b) {
	            		found = true;
	            		break;
	            	}
	            	i++;
	            }
	        	if(!found) {
	        		values.allInitDone = true;
	        	}	            	
            }
        }
        
        if (!quality) {
        	if(!values.isInGap[idx]) {
        		gapNotification(idxReqInp, idx - getIdxSumOfPrevious()[idxReqInp], idx, t,
	            		sv, dataPoint, -1);	
        	}
        	setValuesIsInGap(idx, true, idxReqInp); //values.isInGap[idx] = true;
            if(values.gapStart < 0) values.gapStart = t;
            return; //continue;
        }	            

        if(idx != -1) setValuesIsInGap(idx, false, idxReqInp); //values.isInGap[idx] = false;
       	boolean isInGapOfOtherInput = !values.allInitDone;
       	if(values.gapStart >= 0 && values.allInitDone) {
       		int i= 0;
         	for(boolean b: values.isInGapByInputType) {
         		if(isOptional[i]) continue;
         		if(b) {
         			isInGapOfOtherInput = true;
        			break;
         		} else i++;
         	}
        	if(!isInGapOfOtherInput) {
if(t < values.gapStart)
System.out.println("t:"+t+"  gapStart: "+values.gapStart);
        		values.gapTime += (t - values.gapStart);
        		values.gapStart = -1;
        	}
        }
        //boolean newGap = false;
        if(idx != -1) {
			if((timeStamp != Long.MAX_VALUE) && (durationOfInput > maximumGapTimeAccepted(idxReqInp))) {
				setValuesIsInGap(idx, true, idxReqInp); //values.isInGap[idx] = true;
	            //newGap = true;
	            //condition should be identical with if(!isInGapOfOtherInput)
	            if(values.isInGapByInputType[idxReqInp] && values.gapStart < 0) {
	    			gapNotification(idxReqInp, idx - getIdxSumOfPrevious()[idxReqInp], idx, t+maximumGapTimeAccepted(idxReqInp),
	    					sv, dataPoint, duration-maximumGapTimeAccepted(idxReqInp));
		            if(duration > maximumGapTimeAccepted(idxReqInp))
		            	duration = maximumGapTimeAccepted(idxReqInp);
	            	values.gapStart = t + duration; //maximumGapTimeAccepted(idxReqInp);
	            }
	        }
        }
 
        if(durationTime + values.gapTime != timeStamp - startEnd[0] && (!isInGapOfOtherInput)) {
			long diff1 = timeStamp - startEnd[0];
			long diff2 = durationTime + values.gapTime;
			System.out.println("Time loss of :"+(diff1 - diff2)+" inGapOfOther:"+isInGapOfOtherInput);
		}
		if(!isInGapOfOtherInput) {
			durationTime += duration;
		}
        
        if(isInGapOfOtherInput) {
    		processValue(idxReqInp, idxOfEvaluationInput, idx, t, sv, dataPoint,
    				0);        	
        } else {
    		processValue(idxReqInp, idxOfEvaluationInput, idx, t, sv, dataPoint,
    				duration); //(duration > maximumGapTimeAccepted(idxReqInp))?maximumGapTimeAccepted(idxReqInp):duration);        	        	
        }
        //moved up before adjustment of duration
		//if(newGap && (!isInGapOfOtherInput))
		//	gapNotification(idxReqInp, idx - idxSumOfPrevious[idxReqInp], idx, t+maximumGapTimeAccepted(idxReqInp),
		//			sv, dataPoint, duration-maximumGapTimeAccepted(idxReqInp));
		
     }
    long durationTime = 0;
    
    private void setValuesIsInGap(int baseIdx, boolean state, int requiredInputIdx) {
        values.isInGap[baseIdx] = state;
        int i = getTotalInputIdx(requiredInputIdx, 0);
        int end;
        if(requiredInputIdx == getIdxSumOfPrevious().length-1) end = size;
        else end = getTotalInputIdx(requiredInputIdx+1, 0);
        boolean allOfTypeInGap = true;
        for(;i<end; i++) {
        	if(!values.isInGap[i]) {
        		allOfTypeInGap = false;
        		break;
        	}
        }
        values.isInGapByInputType[requiredInputIdx] = allOfTypeInGap;
    }

    @Override
    public Status finishInternal() {
         if(values.gapStart >= 0) {
      		values.gapTime += (startEnd[1] - values.gapStart);
      		values.gapStart = -1;
        }
    	return super.finishInternal();
    }
    
	public int[] getIdxSumOfPrevious() {
		return idxSumOfPrevious;
	}
}
