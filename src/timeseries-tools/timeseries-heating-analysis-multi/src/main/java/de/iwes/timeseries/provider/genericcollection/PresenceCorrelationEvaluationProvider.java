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
package de.iwes.timeseries.provider.genericcollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iee.app.automatedheatingtimedetection.provider.motionclean.PrimaryPresenceEvalProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProviderPreEval;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesMultiAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesMultiAggregator.MultiValueDuration;

/**
 * Calculate the time a room was heated per day.
 */
@Service(EvaluationProvider.class)
@Component
public class PresenceCorrelationEvaluationProvider extends GenericGaRoSingleEvalProviderPreEval {
	
    public final static String ID = "presence_heating_correlation_eval_provider";
    public final static String LABEL = "Prensence-Heating Correlation evaluation provider";
    public final static String DESCRIPTION = "Evaluates relation between setpoint / room temperature and presence detected";
    
    public PresenceCorrelationEvaluationProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataTypeI[] getGaRoInputTypes() {
		return new GaRoDataTypeI[] {
	        	GaRoDataType.TemperatureMeasurementRoomSensor,
	        	GaRoDataType.TemperatureMeasurementThermostat,
	        	GaRoDataType.TemperatureSetpointFeedback,
	        	PrimaryPresenceEvalProvider.CLEANED_PRESENCE_TS
		};
	}
	public static final int TEMPMES_IDX = 0; 
	public static final int TEMPMESTH_IDX = 1;
	public static final int SETP_IDX = 2;
	public static final int MOTION_IDX = 3;
      
	public static final long MIN_PRESENCE_TIME = 180*60000;
	public static final float MIN_PRESENCE_SHARE = (float) (((double)MIN_PRESENCE_TIME) / (1440*60000));
	public static final long MIN_PRESENCE_DETECTED_INTERVAL = 10*60000;
	
    public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
     	public final InputSeriesAggregator tempMes;
     	public final InputSeriesAggregator tempSetp;
     	//Usually we would not need InputSeriesAggregator here as this is a pre-eval input, but for
     	//input into Multi-Aggregator we still need it
     	public final InputSeriesAggregator motion;
     	private final InputSeriesMultiAggregator mesMotion;
     	private final InputSeriesMultiAggregator setpMotion;
     	
     	BaseOnlineEstimator tempMesPresence = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
     	BaseOnlineEstimator tempMesNonP = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
     	BaseOnlineEstimator tempSetpPresence = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
     	BaseOnlineEstimator tempSetpNonP = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);

     	/*long tempMesPresenceTimeUp = 0;
     	long tempMesNonPTimeUp = 0;
     	long tempMesPresenceTimeDown = 0;
     	long tempMesNonPTimeDown = 0;*/
     	//long presenceTime;
     	//long absenceTime;
     	float presenceShare;
     	float absenceShare;
     	
       	private final int useTempMesIdx;
       	private final Float comfortTemp; 
       	//private final float comfortTempSetp; 
      	//private final float avTemp; 
      	//private final float avTempSetp; 
     	//private final float comfortAvMean;
      	
   		private boolean presenceDetected = false;

   		public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
    		if(input.get(TEMPMES_IDX).getInputData().isEmpty()) useTempMesIdx = TEMPMESTH_IDX;
    		else useTempMesIdx = TEMPMES_IDX;
    		tempMes = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				useTempMesIdx, startEnd[1], null, AggregationMode.AVERAGING);
    		tempSetp = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				SETP_IDX, startEnd[1], null, AggregationMode.AVERAGING);
    		motion = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				MOTION_IDX, startEnd[1], null, AggregationMode.MAX);
    		mesMotion = new InputSeriesMultiAggregator(new InputSeriesAggregator[] {tempMes, motion});
    		setpMotion = new InputSeriesMultiAggregator(new InputSeriesAggregator[] {tempSetp, motion});
    		comfortTemp = getPreEvalRoomValue(ComfortTempRealBasedEvalProvider.ID, ComfortTempRealBasedEvalProvider.COMFORT_TEMP2.id()); //Float.parseFloat(dailyComfortTemp1.evalResults.get(ComfortTempRealBasedEvalProvider.COMFORT_TEMP2.id()));
    		presenceShare = getPreEvalRoomValue(PrimaryPresenceEvalProvider.ID, PrimaryPresenceEvalProvider.PRESENCE_SHARE.id());
    		absenceShare = getPreEvalRoomValue(PrimaryPresenceEvalProvider.ID, PrimaryPresenceEvalProvider.ABSENCE_SHARE.id());
      	}
     	
   		long presenceConfirmedTime = 0;
   		class InputCallData {
   			public InputCallData(MultiValueDuration mvalMes, MultiValueDuration mvalSetp, int idxOfEvaluationInput,
					SampledValue sv, SampledValueDataPoint dataPoint) {
				this.mvalMes = mvalMes;
				this.mvalSetp = mvalSetp;
				this.idxOfEvaluationInput = idxOfEvaluationInput;
				this.sv = sv;
				this.dataPoint = dataPoint;
			}
			MultiValueDuration mvalMes;
			MultiValueDuration mvalSetp;
   			int idxOfEvaluationInput;
   			SampledValue sv;
   			SampledValueDataPoint dataPoint;
   		}
   		InputCallData firstHiddenOff = null;
   		@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
			MultiValueDuration mval = null;
			if(presenceDetected && (firstHiddenOff != null) && ((presenceConfirmedTime + MIN_PRESENCE_DETECTED_INTERVAL) <= timeStamp)) {
				processValueMotion(firstHiddenOff.mvalMes, firstHiddenOff.mvalSetp, firstHiddenOff.idxOfEvaluationInput,
						firstHiddenOff.sv.getTimestamp(), firstHiddenOff.sv,
						firstHiddenOff.dataPoint, timeStamp);
				firstHiddenOff = null;
			}
			if(idxOfRequestedInput == useTempMesIdx) {
    			mval = mesMotion.getCurrentValueDuration(sv, dataPoint, true, 0, idxOfEvaluationInput);
    			if(presenceDetected) {
    				tempMesPresence.addValue(mval.values[0], mval.duration);
    			} else {
    				tempMesNonP.addValue(mval.values[0], mval.duration);    				
    			}
    			//addMesTimes(mval, timeStamp);
      		} else if(idxOfRequestedInput == SETP_IDX) {
    			mval = setpMotion.getCurrentValueDuration(sv, dataPoint, true, 0, idxOfEvaluationInput);
    			if(presenceDetected) {
    				tempSetpPresence.addValue(mval.values[0], mval.duration);
    			} else {
    				tempSetpNonP.addValue(mval.values[0], mval.duration);    				
    			}
      		} else if (idxOfRequestedInput == MOTION_IDX) {
    			MultiValueDuration mvalMes = mesMotion.getCurrentValueDuration(sv, dataPoint, true, 1, idxOfEvaluationInput);
    			MultiValueDuration mvalSetp = setpMotion.getCurrentValueDuration(sv, dataPoint, true, 1, idxOfEvaluationInput);
    			processValueMotion(mvalMes, mvalSetp, idxOfEvaluationInput, timeStamp, sv, dataPoint, timeStamp);
      		}
   		}
   		
   		private void processValueMotion(MultiValueDuration mvalMes, MultiValueDuration mvalSetp, int idxOfEvaluationInput,
   				long timeStamp,
   				SampledValue sv, SampledValueDataPoint dataPoint, long now) {
			if(mvalMes.values[1] > 0.5) {
				presenceDetected = true;
				tempMesPresence.addValue(mvalMes.values[0], mvalMes.duration);
				tempSetpPresence.addValue(mvalSetp.values[0], mvalSetp.duration);
				presenceConfirmedTime = sv.getTimestamp();
			} else {
				//cleaning
				if((presenceDetected) && (presenceConfirmedTime + MIN_PRESENCE_DETECTED_INTERVAL) > now) {
					if(firstHiddenOff == null) firstHiddenOff = new InputCallData(mvalMes, mvalSetp, idxOfEvaluationInput, sv, dataPoint);
					return;
				}
					
				presenceDetected = false;    				
				tempMesNonP.addValue(mvalMes.values[0], mvalMes.duration);    				
				tempSetpNonP.addValue(mvalSetp.values[0], mvalSetp.duration);    				
			}
			//addMesTimes(mvalMes, timeStamp);
//mval = mvalMes;
 //if(lastTimeGlob > 0)
//if(mval != null)
//	System.out.println("New value "+idxOfRequestedInput+","+idxOfEvaluationInput+" Method Duration:"+duration+" Mval Duration: "+mval.duration+" Time Dif Prev:"+(timeStamp - lastTimeGlob));
//else
//	System.out.println("New value "+idxOfRequestedInput+","+idxOfEvaluationInput+" Method Duration:"+duration+" Time Dif Prev:"+(timeStamp - lastTimeGlob));
//lastTimeGlob = timeStamp;
    	}
    	
long lastTime = -1;
long lastDuration = -1;
//long lastTimeGlob = -1;

		/*private void addMesTimes(MultiValueDuration mval, long timestamp) {
if((lastDuration != (timestamp - lastTime)) && (lastTime > 0))
	System.out.println("Wrong Last Duration: "+lastDuration+" Last Time Dif:"+(timestamp - lastTime));
lastTime = timestamp;
lastDuration = mval.duration;
			if(presenceDetected) {
				presenceTime += mval.duration;
				//if(mval.values[0] > comfortAvMean)
				//	tempMesPresenceTimeUp += mval.duration;
				//else
				//	tempMesPresenceTimeDown += mval.duration;
			} else {
				absenceTime += mval.duration;
				//if(mval.values[0] > comfortAvMean)
				//	tempMesNonPTimeUp += mval.duration;
				//else
				//	tempMesNonPTimeDown += mval.duration;
			}
   		
    	}*/
    }
    
    public final static GenericGaRoResultType PRESENCE_NONP_DIF_TEMP = new GenericGaRoResultType("Presence_NonP_TempDiff_3hmin",
    		"Average of measured temperature in room during presence times minus average of measured temperature during non-presence times") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					if(cec.comfortTemp == null || Float.isNaN(cec.comfortTemp))
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
					if(cec.presenceShare >= MIN_PRESENCE_SHARE)
					//if(cec.presenceTime >= MIN_PRESENCE_TIME)
						return new SingleValueResultImpl<Float>(rt, cec.tempMesPresence.getAverage() - cec.tempMesNonP.getAverage(), inputData);
					else
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
				}
    };
    public final static GenericGaRoResultType PRESENCE_NONP_DIF_SETP = new GenericGaRoResultType("Presence_NonP_SetpointDiff_3hmin",
    		"Average of setpoint temperature in room during presence times minus average of setpoint temperature during non-presence times") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					if(cec.comfortTemp == null || Float.isNaN(cec.comfortTemp))
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
					if(cec.presenceShare >= MIN_PRESENCE_SHARE)
					//if(cec.presenceTime >= MIN_PRESENCE_TIME)
						return new SingleValueResultImpl<Float>(rt, cec.tempSetpPresence.getAverage() - cec.tempSetpNonP.getAverage(), inputData);
					else
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
				}
    };
    /*public final static GenericGaRoResultType PRESENCE_TIMESHARE_UP = new GenericGaRoResultType("Presence_TimeShare_Up_3hmin",
    		"Share of presence time during which the measured temperature was above mean of comfort temperature and average daily temperature") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					if(cec.presenceTime >= MIN_PRESENCE_TIME)
						return new SingleValueResultImpl<Float>(rt, (float) ((double)cec.tempMesPresenceTimeUp/(cec.tempMesPresenceTimeUp+cec.tempMesPresenceTimeDown)), inputData);
					else
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
				}
    };
    public final static GenericGaRoResultType PRESENCE_TIMESHARE_DOWN = new GenericGaRoResultType("Presence_TimeShare_Down_3hmin", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					if(cec.presenceTime >= MIN_PRESENCE_TIME)
						return new SingleValueResultImpl<Float>(rt, (float) ((double)cec.tempMesNonPTimeDown/(cec.tempMesNonPTimeUp+cec.tempMesNonPTimeDown)), inputData);
					else
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
				}
    };
    public final static GenericGaRoResultType PRESENCE_TIME_MINUTES = new GenericGaRoResultType("Presence_Time", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					return new SingleValueResultImpl<Float>(rt, (float) (cec.presenceTime/60000.0), inputData);
				}
    };
    public final static GenericGaRoResultType PRESENCE_TIME_HEATING_MINUTES = new GenericGaRoResultType("Presence_Time_Heating", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					if(cec.comfortTemp == null || Float.isNaN(cec.comfortTemp))
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
					return new SingleValueResultImpl<Float>(rt, (float) (cec.presenceTime/60000.0), inputData);
				}
    };
    public final static GenericGaRoResultType EVALUATED_TIME_MINUTES = new GenericGaRoResultType("Evaluated_Time", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					long evalTime = cec.presenceTime+cec.absenceTime; //cec.tempMesPresenceTimeUp+cec.tempMesPresenceTimeDown+cec.tempMesNonPTimeUp+cec.tempMesNonPTimeDown;
					//long presenceCheck = cec.tempMesPresenceTimeUp+cec.tempMesPresenceTimeDown;
//if(presenceCheck != cec.presenceTime) {
//	System.out.println("Presence time not consistend: Counted Total:"+cec.presenceTime+" Sum Up/Down:"+presenceCheck);
//}
					return new SingleValueResultImpl<Float>(rt, (float) (evalTime/60000.0), inputData);
				}
    };*/
    
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(PRESENCE_NONP_DIF_TEMP,
    		PRESENCE_NONP_DIF_SETP); //,
    		//PRESENCE_TIME_MINUTES, EVALUATED_TIME_MINUTES);
    		//PRESENCE_TIMESHARE_UP, PRESENCE_TIMESHARE_DOWN,);
    
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}

	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		return new EvalCore(input, requestedResults, configurations, listener, time, size, nrInput, idxSumOfPrevious, startEnd);
	}
	
	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return Arrays.asList(new PreEvaluationRequested(ComfortTempRealBasedEvalProvider.ID),
				new PreEvaluationRequested(PrimaryPresenceEvalProvider.ID));
	}
	
	/*
	public static final PreEvaluationRequested COMFORT_TEMP_PROVIDER = new PreEvaluationRequested(ComfortTempRealBasedEvalProvider.ID);
	//public static final PreEvaluationRequested COMFORT_TEMP_PROVIDER_SETP = new PreEvaluationRequested(ComfortTemperatureGenericEvalProvider.class.getSimpleName());

	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return Arrays.asList(COMFORT_TEMP_PROVIDER); //, COMFORT_TEMP_PROVIDER_SETP);
	}

	public GaRoPreEvaluationProvider comfortTempProvider;
	//public GaRoPreEvaluationProvider<Resource> comfortTempProviderSetp;
	
	@Override
	public void preEvaluationProviderAvailable(int requestIdx, String providerId,
			GaRoPreEvaluationProvider provider) {
		switch(requestIdx) {
		case 0:
			comfortTempProvider = (GaRoPreEvaluationProvider) provider;
			break;
		//case 1:
		//	comfortTempProviderSetp = (GaRoPreEvaluationProvider<Resource>) provider;
		//	break;
		}
		
	}*/
}
