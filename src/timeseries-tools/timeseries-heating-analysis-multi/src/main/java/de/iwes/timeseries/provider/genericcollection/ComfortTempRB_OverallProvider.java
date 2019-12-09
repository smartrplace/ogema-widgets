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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;
import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProviderPreEval;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;

/**
 * Calculate the comfort temperature that was defined by the user.
 */
@Service(EvaluationProvider.class)
@Component
public class ComfortTempRB_OverallProvider extends GenericGaRoSingleEvalProviderPreEval {
	
    public final static String ID = "comforttemp_rboverall_eval_provider";
    public final static String LABEL = "Overall Comfort Temperature Real Based evaluation provider";
    public final static String DESCRIPTION = "Calculates the comfort temperature for each gateway with nightly derating.";
    
    public ComfortTempRB_OverallProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	        	GaRoDataType.TemperatureMeasurementRoomSensor,
	        	GaRoDataType.TemperatureMeasurementThermostat,
		};
	}
	public static final int TEMPMES_IDX = 0; 
	public static final int TEMPMESTH_IDX = 1; 
        
    public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
    	public InputSeriesAggregator tempMes;
    	public final BaseOnlineEstimator avNDEstimator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
    	public final BaseOnlineEstimator decreaseEstimator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
    	public final BaseOnlineEstimator decreaseEstimatorND = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
    	//public final BaseOnlineEstimator decreaseEstimatorND_MaxCT = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);

    	public final BaseOnlineEstimator refEstimator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
    	
    	private int useTempMesIdx;
    	private final float comfortTempVal;
    	//private final float comfortTempValMax;
private final float avTempPreEval;
private final String currentGwIdLoc = currentGwId;    	
private final String currentRoomIdLoc = currentRoomId; 
private int countNan = 0;
private int countOK = 0;

    	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
    		if(input.get(TEMPMES_IDX).getInputData().isEmpty()) useTempMesIdx = TEMPMESTH_IDX;
    		else useTempMesIdx = TEMPMES_IDX;
    		tempMes = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				useTempMesIdx, startEnd[1]);
    		RoomData dailyComfortTemp1 = comfortTempProvider.getRoomData(startEnd[0],
    				currentGwId, currentRoomId);
    		comfortTempVal = Float.parseFloat(dailyComfortTemp1.evalResults.get(ComfortTempRealBasedEvalProvider.COMFORT_TEMP2.id()));
    		//comfortTempValMax = Float.parseFloat(dailyComfortTemp1.evalResults.get(ComfortTempRealBasedEvalProvider.COMFORT_TEMP3.id()));
    		avTempPreEval = Float.parseFloat(dailyComfortTemp1.evalResults.get(ComfortTempRealBasedEvalProvider.TEMP_AV.id()));
    	}
    	
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		
    		if(Float.isNaN(comfortTempVal)) {
    			return;
    		}
			if(Float.isNaN(avTempPreEval)) {
				System.out.println("avTemp NaN, not comfortTempVal!");
			}
    		
    		if(idxOfRequestedInput == useTempMesIdx) {
    			final ValueDuration val = tempMes.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    			if(evalInstance.isRequested(TEMP_REF_ND) || evalInstance.isRequested(ROOM_TEMP_DECREASE_ND)) {
    					//||  evalInstance.isRequested(ROOM_TEMP_DECREASE_NDMAX)) {
    				LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
    				int hour = ld.getHour();
    				if(hour < 6 || hour >= 23) {
    					avNDEstimator.addValue(val.value, val.duration);
       					decreaseEstimatorND.addValue(0, val.duration);
       					//decreaseEstimatorND_MaxCT.addValue(0, val.duration);
    				}
    				else {
    					avNDEstimator.addValue(comfortTempVal, val.duration);
         				float dval = comfortTempVal - val.value;
           				if(dval >= 0) decreaseEstimatorND.addValue(dval, val.duration);    		
           				else decreaseEstimatorND.addValue(0, val.duration);
         				//float dval2 = comfortTempValMax - val.value;
           				//if(dval2 >= 0) decreaseEstimatorND_MaxCT.addValue(dval2, val.duration);
           				//else decreaseEstimatorND_MaxCT.addValue(0, val.duration);
     				}
     			}
       			if(evalInstance.isRequested(ROOM_TEMP_DECREASE)) {
       				float dval = comfortTempVal - val.value;
       				if(dval >= 0) decreaseEstimator.addValue(dval, val.duration);
       				else decreaseEstimator.addValue(0, val.duration);
       			}
       			if(evalInstance.isRequested(SETP_TEMP_AV_REF)) {
       				refEstimator.addValue(val.value, val.duration);
       			}
      		}
    	}
    	
    }
    
    public final static GenericGaRoResultType TEMP_REF_ND = new GenericGaRoResultType("Average_Setpoint_ND_Temperature",
    		TemperatureResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
EvalCore cec = ((EvalCore)ec);
float val = cec.avNDEstimator.getAverage();
if(val < cec.avTempPreEval)
	System.out.println("PreEval AV:"+cec.avTempPreEval+" now Ref_ND:"+val+" gw:"+cec.currentGwIdLoc+" room:"+cec.currentRoomIdLoc+" countOK:"+cec.countOK+" countNan:"+cec.countNan);
			return new SingleValueResultImpl<Float>(rt, val, inputData);
		}
    };
    public final static GenericGaRoResultType ROOM_TEMP_DECREASE = new GenericGaRoResultType("Temp_Decrease",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).decreaseEstimator.getAverage(), inputData);
		}
    };
    public final static GenericGaRoResultType ROOM_TEMP_DECREASE_ND = new GenericGaRoResultType("Temp_Decrease_NightDerating",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).decreaseEstimatorND.getAverage(), inputData);
		}
    };
    /*public final static GenericGaRoResultType ROOM_TEMP_DECREASE_NDMAX = new GenericGaRoResultType("Temp_Decrease_NightDerating_MAXComfortTemp",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).decreaseEstimatorND_MaxCT.getAverage(), inputData);
		}
    };*/
    public final static GenericGaRoResultType SETP_TEMP_AV_REF = new GenericGaRoResultType("Average_Setpoint_Reference_Temperature",
    		TemperatureResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = ((EvalCore)ec);
			float val = cec.refEstimator.getAverage();
			if(!Float.isNaN(val) && val != cec.avTempPreEval)
				System.out.println("PreEval AV:"+cec.avTempPreEval+" now:"+val+" gw:"+cec.currentGwIdLoc+" room:"+cec.currentRoomIdLoc);
			return new SingleValueResultImpl<Float>(rt, val, inputData);
		}
    };
     
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(
   		 TEMP_REF_ND, ROOM_TEMP_DECREASE, ROOM_TEMP_DECREASE_ND,
   		 SETP_TEMP_AV_REF
    );
    // ROOM_TEMP_DECREASE_NDMAX,
    
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
	
	/** Old style pre evaluation declaration*/
	public static final PreEvaluationRequested COMFORT_TEMP_PROVIDER = new PreEvaluationRequested(ComfortTempRealBasedEvalProvider.ID);

	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return Arrays.asList(COMFORT_TEMP_PROVIDER);
	}

	public GaRoPreEvaluationProvider comfortTempProvider;
	
	@Override
	public void preEvaluationProviderAvailable(int requestIdx, String providerId,
			GaRoPreEvaluationProvider provider) {
		switch(requestIdx) {
		case 0:
			comfortTempProvider = (GaRoPreEvaluationProvider) provider;
			break;
		}
	}
}
