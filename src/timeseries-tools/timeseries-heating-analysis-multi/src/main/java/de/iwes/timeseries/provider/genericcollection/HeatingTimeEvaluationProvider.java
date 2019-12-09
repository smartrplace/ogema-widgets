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
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.TimeResource;
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
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProvider;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;

/**
 * Calculate the time a room was heated per day.
 */
@Service(EvaluationProvider.class)
@Component
public class HeatingTimeEvaluationProvider extends GenericGaRoSingleEvalProvider {
	
    public final static String ID = "heatingtime_eval_provider";
    public final static String LABEL = "Heating Time and related characteristics evaluation provider";
    public final static String DESCRIPTION = "Heating Time and related characteristics calculation";
    
    public HeatingTimeEvaluationProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	        	GaRoDataType.ValvePosition,
	        	GaRoDataType.TemperatureMeasurementRoomSensor,
	        	GaRoDataType.TemperatureMeasurementThermostat,
	        	GaRoDataType.TemperatureSetpointFeedback
		};
	}
	public static final int VALVE_IDX = 0; 
	public static final int TEMPMES_IDX = 1; 
	public static final int TEMPMESTH_IDX = 2; 
	public static final int TEMPSETP_IDX = 3; 
        
    public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
     	public final InputSeriesAggregator valves;
     	public final InputSeriesAggregator tempMes;
     	public final InputSeriesAggregator tempMesTH;
     	public final InputSeriesAggregator tempSetpts;
     	
     	long heatingTime = 0;
    	long totalTime = 0;
     	boolean isHeatingPeriod = false;
     	BaseOnlineEstimator tempDiffEstimator = new BaseOnlineEstimator(false, AverageMode.STD_DEVIATION);
     	BaseOnlineEstimator tempDiffEstimatorThermostat = new BaseOnlineEstimator(false, AverageMode.STD_DEVIATION);
     	BaseOnlineEstimator tempDiffEstimatorInterreal = new BaseOnlineEstimator(false, AverageMode.STD_DEVIATION);
     	float[] lastTempVals = new float[3];
     	
     	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
    		valves = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				VALVE_IDX, startEnd[1], null, AggregationMode.MAX);
    		tempMes = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				TEMPMES_IDX, startEnd[1]);
    		tempMesTH = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				TEMPMESTH_IDX, startEnd[1]);
    		tempSetpts = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				TEMPSETP_IDX, startEnd[1]);
    		
     		lastTempVals[0] = Float.NaN;
    		lastTempVals[1] = Float.NaN;
    		lastTempVals[2] = Float.NaN;
     	}
     	
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
			ValueDuration val;
    		switch(idxOfRequestedInput) {
    		case VALVE_IDX:// temperature sensor value
    			val = valves.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    			if(evalInstance.isRequested(HEATING_TIME)) {
    				boolean isHeating = (val.value > 0);
    				if(isHeating) {
    					heatingTime += duration;
    					this.isHeatingPeriod = true;
    				} else
    					this.isHeatingPeriod = false;
    				totalTime += duration;
    			}
    			break;
    		case TEMPMES_IDX:
    			val = tempMes.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    			if((val.value < 280)) {
    				System.out.println("Low value: "+(val.value-273.15f));
    			}
    			lastTempVals[0] = val.value;
    			//fallthrough
    		case TEMPMESTH_IDX:
       			val = tempMesTH.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    			if(idxOfRequestedInput == TEMPMESTH_IDX) lastTempVals[1] = val.value;
    			//fallthrough
    		case TEMPSETP_IDX:
       			val = tempSetpts.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    			if(idxOfRequestedInput == TEMPSETP_IDX) lastTempVals[2] = val.value;
    			if(isHeatingPeriod) {
    				if(!Float.isNaN(lastTempVals[0]) && (!Float.isNaN(lastTempVals[1]))) {
    					long myDuration = tempDiffEstimatorInterreal.getDuration(timeStamp);
						if(myDuration > 0)
							tempDiffEstimatorInterreal.addValue(tempDiffEstimatorInterreal.getSetLastValue(lastTempVals[0]-lastTempVals[1]), myDuration);
    				}
    				if(!Float.isNaN(lastTempVals[0]) && (!Float.isNaN(lastTempVals[2]))) {
    					long myDuration = tempDiffEstimator.getDuration(timeStamp);
						if(myDuration > 0)
							tempDiffEstimator.addValue(tempDiffEstimator.getSetLastValue(lastTempVals[0]-lastTempVals[2]), myDuration);
    				}
    				if(!Float.isNaN(lastTempVals[1]) && (!Float.isNaN(lastTempVals[2]))) {
    					long myDuration = tempDiffEstimatorThermostat.getDuration(timeStamp);
						if(myDuration > 0)
							tempDiffEstimatorThermostat.addValue(tempDiffEstimatorThermostat.getSetLastValue(lastTempVals[1]-lastTempVals[2]), myDuration);
    				}
    			}
    			break;
      		}
    	}
    }
    
    public final static GenericGaRoResultType HEATING_TIME = new GenericGaRoResultType("Heating_Time",
    		TimeResource.class, ID) {
			@Override
			public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
					List<TimeSeriesData> inputData) {
				return new SingleValueResultImpl<Long>(rt, ((EvalCore)ec).heatingTime, inputData);
			}
    };
    public final static GenericGaRoResultType HEATING_SHARE = new GenericGaRoResultType("Heating_Share",
    		"Share of time evaluated during which at least one radiator was heating", FloatResource.class, ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = (EvalCore)ec;
					return new SingleValueResultImpl<Float>(rt, (float) (cec.heatingTime / (double)cec.totalTime), inputData);
				}
    };
    public final static GenericGaRoResultType REAL_TO_SETP_STD = new GenericGaRoResultType("Standard_Deviation_Temp_Real_Setpoint",
    		"Standard Deviation between real and setpoint temperature during heating periods. Real temperature only "
    		+ "based on room temperature sensor data", FloatResource.class, ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = (EvalCore)ec;
					return new SingleValueResultImpl<Float>(rt, cec.tempDiffEstimator.getStdDeviation(), inputData);
				}
    };
    public final static GenericGaRoResultType REAL_TO_SETP_OFFSET = new GenericGaRoResultType("Offset_Temp_Real_Setpoint",
    		"Average offset of real minus setpoint temperature during heating periods. Real temperature only "
    		+ "based on room temperature sensor data", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = (EvalCore)ec;
					return new SingleValueResultImpl<Float>(rt, cec.tempDiffEstimator.getAverage(), inputData);
				}
    };
    public final static GenericGaRoResultType REALTHERMOSTAT_TO_SETP_STD = new GenericGaRoResultType("Standard_Deviation_Temp_RealTHERMOSTAT__Setpoint",
    		"Standard Deviation between temperater measurement at thermostat and setpoint temperature during heating periods.") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = (EvalCore)ec;
					return new SingleValueResultImpl<Float>(rt, cec.tempDiffEstimatorThermostat.getStdDeviation(), inputData);
				}
    };
    public final static GenericGaRoResultType REALTHERMOSTAT__TO_SETP_OFFSET = new GenericGaRoResultType("Offset_Temp_RealTHERMOSTAT__Setpoint",
    		"Average offset of  temperater measurement at thermostat minus setpoint temperature during heating periods."
    		+ "based on room temperature sensor data") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = (EvalCore)ec;
					return new SingleValueResultImpl<Float>(rt, cec.tempDiffEstimatorThermostat.getAverage(), inputData);
				}
    };
    public final static GenericGaRoResultType INTERREAL_STD = new GenericGaRoResultType("Standard_Deviation_Temp_Room_Thermostat",
    		"Standard Deviation between temperature measurement of room sensor and measurement at thermostat during heating periods.") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = (EvalCore)ec;
					return new SingleValueResultImpl<Float>(rt, cec.tempDiffEstimatorInterreal.getStdDeviation(), inputData);
				}
    };
    public final static GenericGaRoResultType INTERREAL_OFFSET = new GenericGaRoResultType("Offset_Temp_Room_Thermostat",
    		"Average offset of temperater measurement of room sensor minus measurement at thermostat during heating periods.") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = (EvalCore)ec;
					return new SingleValueResultImpl<Float>(rt, cec.tempDiffEstimatorInterreal.getAverage(), inputData);
				}
    };
    
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(HEATING_TIME, HEATING_SHARE,
    		REAL_TO_SETP_STD, REAL_TO_SETP_OFFSET,
    		INTERREAL_STD, INTERREAL_OFFSET,
    		REALTHERMOSTAT_TO_SETP_STD, REALTHERMOSTAT__TO_SETP_OFFSET);
    
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
}
