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
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;

/**
 * Calculate the comfort temperature that was defined by the user.
 */
@Service(EvaluationProvider.class)
@Component
public class ValveHourCalculator extends GenericGaRoSingleEvalProvider {
	
    public final static String ID = "base_valveHour_provider_gen";
    public final static String LABEL = "Heat Consumption of Radiators Estimator";
    public final static String DESCRIPTION = "Calculates Consumption of radiators in valve hours";
    
    public ValveHourCalculator() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	    		GaRoDataType.ValvePosition};
	}
	public static final int VALVE_IDX = 0;
        
    public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
    	private final static long ONE_HOUR = 60 * 60 * 1000;
        // state variables
        // if true, then the valve position is assumed in the range 0-100, instead of 0-1 as specified
    	// if null, then this has not been determined yet
        Boolean usingScaledValvePosition;
    	
        private final boolean valveRequested;

    	private final InputSeriesAggregator valve;

    	final BaseOnlineEstimator valveHourTotal = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY); 
    	final BaseOnlineEstimator valveHoursNewEstm = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
     	final long fullDuration;
  	
     	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
            this.valveRequested = requestedResults.contains(VALVE_HOURS_TOTAL);
    	    valve = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    					VALVE_IDX, startEnd[1]);
    		fullDuration = startEnd[1] - startEnd[0];
     	}
     	
        public static final int MULTIAGG_VALVEIDX = 1;

    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
        	switch(idxOfRequestedInput) {
        	case VALVE_IDX: // valve position
        		if (!valveRequested)
        			return;
        		ValueDuration vd = valve.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
        		
        		valveHourTotal.addValue(vd.value, (vd.duration>0)?vd.duration:0);
        		if(evalInstance.isRequested(VALVE_HOURS_TOTAL_NEW))
        			valveHoursNewEstm.addValue(vd.value, vd.duration);
        		if(vd.duration < 0)
        			System.out.println("Duration:"+vd.duration);

        		//Special workaround for faulty driver
        		final SampledValue last = dataPoint.previous(totalInputIdx);
        		if (last != null) {
        			if (usingScaledValvePosition == null) {
        				final float val = sv.getValue().getFloatValue();
        				if (val > 1)
        					usingScaledValvePosition = true;
        				else if (0 < val && val < 1)
        					usingScaledValvePosition = false;
        			} //if
         		} //if
        	} //switch
    	} //processValue
    	
    }
    
    public final static GenericGaRoResultType VALVE_HOURS_TOTAL = new GenericGaRoResultType("Valve Full Load Hours",
    		"Valve Full Load Hours Total (w/o gaps)", ID) {
    	@Override
    	public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
    			List<TimeSeriesData> inputData) {
    		EvalCore cec = ((EvalCore)ec);
    		float valveScale = (cec.usingScaledValvePosition != null && cec.usingScaledValvePosition) ? 100 : 1;
    		double val = cec.valveHourTotal.getIntegralOverMilliseconds() / EvalCore.ONE_HOUR / valveScale;
    		if(val > 24)
    			System.out.println("Very large value: "+val);
    		return new SingleValueResultImpl<Float>(rt, (float) (val), inputData);
    	}
    };
    public final static GenericGaRoResultType VALVE_HOURS_TOTAL_NEW = new GenericGaRoResultType("Valve Full Load Hours (gaps filled)",
    		"Valve Full Load Hours Total(Assuming average during gaps)", ID) {
    	@Override
    	public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
    			List<TimeSeriesData> inputData) {
    		EvalCore cec = ((EvalCore)ec);
    		float valveScale = (cec.usingScaledValvePosition != null && cec.usingScaledValvePosition) ? 100 : 1;
    		double val = ((double)(cec.valveHoursNewEstm.getAverage())*cec.fullDuration / EvalCore.ONE_HOUR / valveScale);
    		if(val > 24)
    			System.out.println("Very large value: "+val);
    		return new SingleValueResultImpl<Float>(rt, (float) (val), inputData);
    	}
    };


    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(
    		VALVE_HOURS_TOTAL, VALVE_HOURS_TOTAL_NEW);

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
