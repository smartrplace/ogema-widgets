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
package de.iee.app.automatedheatingtimedetection.provider.motionclean;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesResultImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProviderPreEval;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;
import de.iwes.timeseries.eval.online.utils.TimeSeriesOnlineBuilder;

/**
 * Clean up motion sensor presence signal and perform basic presence and gap evaluation
 */
@Service(EvaluationProvider.class)
@Component
public class PrimaryPresenceEvalProvider extends GenericGaRoSingleEvalProviderPreEval {
	public static final long MINUTE_MILLIS = 60000;
	public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
	//Markers over MultiEvaluation
	//private int roomEvalCount = 0;
   	//private int eventId = 100;
		
	/** Adapt these values to your provider*/
    public final static String ID = "autoheattime-presence_cleaner_eval_provider";
    public final static String LABEL = "Auto-Heat-Time: Presence signal cleaner evaluation provider";
    public final static String DESCRIPTION = "Auto-Heat-Time: Provides cleaned up presence signal and more";
    
    public PrimaryPresenceEvalProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	/** Provide your data types here*/
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	        	GaRoDataType.MotionDetection,
		};
	}
	
	@Override
	protected long[] getMaximumGapTimes() {
		return new long[] {2*HOUR_MILLIS};
	}
	/** It is recommended to define the indices of your input here.*/
	public static final int MOTION_IDX = 0; 
        
 	public class EvalCore extends GenericGaRoEvaluationCore {
    	final long totalTime;
    	
    	/** Application specific state variables, see also documentation of the util classes used*/
    	public final InputSeriesAggregator motion;
      	public final TimeSeriesOnlineBuilder tsBuilder;

    	//Before we get the first motion signal we assume no presence
    	private boolean isPresent = false;
     	//private long presenceStarted;
    	//only relevant during presence
    	private long absenceSignalStarted = -1;
    	private long memorizePotentialAbsence;
    	//private static final long MINIMUM_PRESENCE_DURATION = 0*60000;
    	private static final long MINIMUM_ABSENCE_DURATION = 15*60000;
    	
    	private long presenceTime = 0;
    	private long absenceTime = 0;
    	private long durationTime = 0;
    	//private long startTime;
    	long durationPostPoned = -1;
     	
    	//Result object and extended presence time event detection
		//public PrimaryPresenceEvalResultObject evalResult = new PrimaryPresenceEvalResultObject();
    	
    	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
			//roomEvalCount++;
    		//example how to calculate total time assuming offline evaluation
    		totalTime = startEnd[1] - startEnd[0];
    		//startTime = startEnd[0];
    		
    		/**The InputSeriesAggregator aggregates the input from all motion sensors in the room.
    		 *	AggregationMode.MAX makes sure that the aggregated value will be one if any motion
    		 *  sensor in the room has signal one.
    		*/
    		motion = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				MOTION_IDX, startEnd[1], null, AggregationMode.MAX);
    		tsBuilder = new TimeSeriesOnlineBuilder();
    		
    		//evalResult.intervals = new ArrayList<>();
    		//evalResult.gwId = currentGwId;
    		//evalResult.roomId = currentRoomId;
    	}
    	
    	private void finalizePostPonedAbsence() {
			tsBuilder.addValue(new SampledValue(new FloatValue(0), memorizePotentialAbsence, Quality.GOOD));
			isPresent = false;
			long addPresence = (memorizePotentialAbsence - absenceSignalStarted);
			long addAbsence = durationPostPoned - addPresence;
			presenceTime += addPresence;
			absenceTime += durationPostPoned - addPresence;
			if(addAbsence < 0)
				System.out.println("   addPresence:"+addPresence+" addAbsence:"+addAbsence+"  but duration:"+durationPostPoned);
			durationPostPoned = -1;
			/*if(memorizePotentialAbsence - presenceStarted >= ExtendedPresenceTimeObject.MINIMUM_DURATION) {
				ExtendedPresenceTimeObject obj = new ExtendedPresenceTimeObject();
				obj.startTime = presenceStarted;
				obj.endTime = memorizePotentialAbsence;
				obj.gwId = currentGwId;
				obj.roomId = currentRoomId+"_"+roomEvalCount;
				obj.eventId = ""+eventId;
				eventId++;
				evalResult.intervals.add(obj);
			}*/   		
    	}
    	
    	/** In processValue the core data processing takes place. This method is called for each input
    	 * value of any input time series.*/
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		
 //			 SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//			 Date dateFormat = new java.util.Date(timeStamp);
//			 
//			 if (sdf.format(dateFormat).equalsIgnoreCase("Sonntag") || 
//					 sdf.format(dateFormat).equalsIgnoreCase("Samstag")) {
//	   				tsBuilder.addValue(new SampledValue(new FloatValue(0), timeStamp, Quality.GOOD));
//	   				isPresent = false;
//			 }
			
    		durationTime += duration;
    		
    		if(isPresent && (memorizePotentialAbsence > 0) && (memorizePotentialAbsence < timeStamp) &&
    				((timeStamp - absenceSignalStarted) > MINIMUM_ABSENCE_DURATION)) {
    			//In the mean time the datapoint is in the past, so we have to indicate absence
    			finalizePostPonedAbsence();
    		}
    		
    		switch(idxOfRequestedInput) {
    		case MOTION_IDX:
    			final ValueDuration val = motion.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
   				boolean newPresence = (val.value > 0.5f);
   				if(isPresent && (!newPresence)) {
   					if(absenceSignalStarted <= 0) {
	   					absenceSignalStarted = timeStamp;
   					//if(timeStamp - presenceStarted < MINIMUM_PRESENCE_DURATION) {
   					//	memorizePotentialAbsence = presenceStarted + MINIMUM_PRESENCE_DURATION;
   					//	break; //we do not put this data point in the cleaned time series for now
   					//} else {
   						memorizePotentialAbsence = timeStamp;
   						durationPostPoned = duration;
    				} else {
    					durationPostPoned += duration;
    				}
   					break;
  				} else if(isPresent && (absenceSignalStarted > 0) && newPresence) {
   					presenceTime += (timeStamp - absenceSignalStarted);
   					absenceSignalStarted = -1;
   					memorizePotentialAbsence = -1;
     			} else if(!isPresent && newPresence) {
   					//presenceStarted = timeStamp;
   					absenceSignalStarted = -1;
   					memorizePotentialAbsence = -1;
   					isPresent = true;
   				}
   				tsBuilder.addValue(sv);
   				if(isPresent) presenceTime += duration;
   				else absenceTime += duration;
   				//isPresent = newPresence;
   				//evalInstance.callListeners(CLEANED_PRESENCE_TS, timeStamp, val.value);
				if(memorizePotentialAbsence > 0)
					System.out.println("Unexpected!");
   				break;
   			default:
   				throw new IllegalStateException("Unknown input idx: "+idxOfRequestedInput);
     		}
    		
     	}
    	
    	// Gap notification usually is only required when a result time series is created, otherwise gap handling is
    	// done by the framework
    	@Override
    	protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput, int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		tsBuilder.addValue(new SampledValue(new FloatValue(Float.NaN), sv.getTimestamp(), Quality.BAD));
    	}

    }
    
 	/**
 	 * Define the results of the evaluation here including the final calculation
 	 */
    public final static GenericGaRoResultType CLEANED_PRESENCE_TS = new GenericGaRoResultType("Cleaned_Presence_TS", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					return new TimeSeriesResultImpl(rt, cec.tsBuilder.getTimeSeries(), inputData);
				}
    };
    /*public final static GenericGaRoResultType EVAL_RESULT = new GenericGaRoResultType("Primary_Presence_Result_Object", ID) {
				@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			
			//TODO: For testing
			//if(cec.evalResult.intervals.isEmpty()) cec.evalResult.intervals.add(new ExtendedPresenceTimeObject());
			
			return new SingleValueResultImpl<PrimaryPresenceEvalResultObject>(rt, cec.evalResult, inputData);
		}
    };*/
    public final static GenericGaRoResultType PRESENCE_SHARE = new GenericGaRoResultType("Presence_Timeshare", FloatResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
    		if(cec.isPresent && (cec.memorizePotentialAbsence > 0)) {
    			//In the mean time the datapoint is in the past, so we have to indicate absence
    			cec.finalizePostPonedAbsence();
    		}
			if(cec.presenceTime + cec.absenceTime != cec.durationTime)
				System.out.println("Presence:"+cec.presenceTime+" Absence:"+cec.absenceTime+" but duration:"+cec.durationTime);
			return new SingleValueResultImpl<Float>(rt, (float) ((double)cec.presenceTime/cec.totalTime), inputData);
		}
    };
    public final static GenericGaRoResultType ABSENCE_SHARE = new GenericGaRoResultType("Absence_Timeshare", FloatResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
    		if(cec.isPresent && (cec.memorizePotentialAbsence > 0)) {
    			//In the mean time the datapoint is in the past, so we have to indicate absence
    			cec.finalizePostPonedAbsence();
    		}
			return new SingleValueResultImpl<Float>(rt, (float) ((double)cec.absenceTime/cec.totalTime), inputData);
		}
    };
    public final static GenericGaRoResultType GAP_TIME_REL = new GenericGaRoResultType("GAP_TIME_REL",
    		"Time share in gaps between existing data", FloatResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			return new SingleValueResultImpl<Float>(rt, (float) ((double)ec.gapTime/cec.totalTime), inputData);
		}
    };
    public final static GenericGaRoResultType ONLY_GAP_REL = new GenericGaRoResultType("ONLY_GAP_REL",
    		"Time share in evals without any data",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			if(cec.durationTime == 0 && cec.gapTime == 0)
				return new SingleValueResultImpl<Float>(rt, 1.0f, inputData);
			else
				return new SingleValueResultImpl<Float>(rt, 0.0f, inputData);
		}
    };
    public final static GenericGaRoResultType TOTAL_TIME = new GenericGaRoResultType("TOTAL_HOURS", FloatResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			return new SingleValueResultImpl<Float>(rt, (float) (cec.totalTime / HOUR_MILLIS), inputData);
		}
    };
    public final static GenericGaRoResultType DURATION_TIME = new GenericGaRoResultType("DURATION_HOURS", FloatResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			return new SingleValueResultImpl<Float>(rt, (float) (cec.durationTime / HOUR_MILLIS), inputData);
		}
    };
   private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(CLEANED_PRESENCE_TS,
    		GAP_TIME_REL, ONLY_GAP_REL, PRESENCE_SHARE, ABSENCE_SHARE, TOTAL_TIME, DURATION_TIME); //EVAL_RESULT
    
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}

	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		return new EvalCore(input, requestedResults, configurations, listener, time, size, nrInput, idxSumOfPrevious, startEnd);
	}

	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return null;
	}
	
	/*@Override
	public Class<? extends GaRoMultiResultExtended> extendedResultDefinition() {
		return PrimaryPresenceMultiResult.class;
	}
	
	@Override
	public Class<? extends GaRoSuperEvalResult<?>> getSuperResultClassForDeserialization() {
		return (Class<? extends GaRoSuperEvalResult<?>>) GaRoSuperEvalResultPrimaryPresence.class;
	}*/
}
