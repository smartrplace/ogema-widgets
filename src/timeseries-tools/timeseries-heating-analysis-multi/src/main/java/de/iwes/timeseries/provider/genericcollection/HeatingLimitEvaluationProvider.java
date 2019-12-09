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
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;
import de.iwes.timeseries.eval.online.utils.LinearRegressionOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.LinearRegressionOnlineEstimator.RegressionResult;
import de.iwes.timeseries.provider.outsideTemperature.OutsideTemperatureEvalProvider;

/**
 * Calculate the time a room was heated per day.
 * @deprecated implementation not finished, do not use like this!
 */
@Deprecated
//@Service(EvaluationProvider.class)
//@Component
public class HeatingLimitEvaluationProvider extends GenericGaRoSingleEvalProviderPreEval {
	
    public final static String ID = "heatinglimit_eval_provider";
    public final static String LABEL = "Heating Limit Temperature evaluation provider";
    public final static String DESCRIPTION = "Heating Limit Temperature and related characteristics calculation";
    
    public HeatingLimitEvaluationProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	        	GaRoDataType.ValvePosition,
		};
	}
	public static final int VALVE_IDX = 0; 
        
    public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
     	public final InputSeriesAggregator valves;
     	
     	boolean closedAllDay = true;
     	LinearRegressionOnlineEstimator regressionEstimator = new LinearRegressionOnlineEstimator();
     	BaseOnlineEstimator valveHours = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
      	
     	float outsideTemperatureMean;
     	long fullDuration;
     	
     	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
    		valves = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				VALVE_IDX, startEnd[1], null, AggregationMode.MAX);
    		RoomData dailyComfortTemp1 = outsideTempProvider.getRoomData(startEnd[0],
    				currentGwId, currentRoomId);
    		outsideTemperatureMean = Float.parseFloat(dailyComfortTemp1.evalResults.get(OutsideTemperatureEvalProvider.OUTSIDE_TEMP_AVERAGE90.id()));
    		fullDuration = startEnd[1] - startEnd[0];
      	}
     	
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
			ValueDuration val;
    		switch(idxOfRequestedInput) {
    		case VALVE_IDX:// temperature sensor value
    			val = valves.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    			if(evalInstance.isRequested(LOWEST_CLOSEDVALVE_TEMP)) {
    				boolean isHeating = (val.value > 0);
    				if(isHeating) closedAllDay = false;
    			}
    			break;
      		}
    	}
    	
    	RegressionResult regParams = null;
    	private RegressionResult getRegressionParams() {
    		if(regParams == null) regParams = regressionEstimator.getRegressionParams();
    		return regParams;
    	}
    }
    
    public final static GenericGaRoResultType HEATING_LIMIT = new GenericGaRoResultType("Heating_Limit_Temperature",
    		TemperatureResource.class, ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).getRegressionParams().offset, inputData);
				}
    };
    public final static GenericGaRoResultType VALVE_HOURS_PER_KELVIN = new GenericGaRoResultType("Valve_Hours_Per_Kelvin",
    		FloatResource.class, ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					return new SingleValueResultImpl<Float>(rt, 1 / ((EvalCore)ec).getRegressionParams().gradient, inputData);
				}
    };
    public final static GenericGaRoResultType VALVE_HOURS_TOTAL = new GenericGaRoResultType("Valve Full Load Hours",
    		"Valve Full Load Hours Total", FloatResource.class, ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					return new SingleValueResultImpl<Float>(rt, (float) (((double)((EvalCore)ec).valveHours.getAverage())*((EvalCore)ec).fullDuration/(3600*1000)), inputData);
				}
    };
    public final static GenericGaRoResultType LOWEST_CLOSEDVALVE_TEMP = new GenericGaRoResultType("Lowest_ClosedValve_Temperature",
    		"Outside Temperature if valve was closed for entire day. NaN if not.", TemperatureResource.class, ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					if(((EvalCore)ec).closedAllDay)
						return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).outsideTemperatureMean, inputData);
					else
						return new SingleValueResultImpl<Float>(rt, Float.NaN, inputData);
				}
    };
    /*Requires temperature limit as pre-evaluation
    public final static GenericGaRoResultType HEATING_DAYS = new GenericGaRoResultType("Heating_Days") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					return new SingleValueResultImpl<Long>(rt, ((EvalCore)ec).heatingTime, inputData);
				}
    };*/
    
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(HEATING_LIMIT, VALVE_HOURS_PER_KELVIN,
    		LOWEST_CLOSEDVALVE_TEMP);
    
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
		return Arrays.asList(new PreEvaluationRequested(OutsideTemperatureEvalProvider.ID));
	}

	public GaRoPreEvaluationProvider outsideTempProvider;
	
	@Override
	public void preEvaluationProviderAvailable(int requestIdx, String providerId,
			GaRoPreEvaluationProvider provider) {
		switch(requestIdx) {
		case 0:
			outsideTempProvider = (GaRoPreEvaluationProvider) provider;
			break;
		}
		
	}
}
