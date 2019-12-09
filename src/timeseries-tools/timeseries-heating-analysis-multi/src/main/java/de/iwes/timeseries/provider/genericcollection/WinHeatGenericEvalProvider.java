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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.TimeSeriesDataOnline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProvider;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;
import de.iwes.timeseries.eval.online.utils.InputSeriesMultiAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesMultiAggregator.MultiValueDuration;

/**
 * Calculate the comfort temperature that was defined by the user.
 */
@Service(EvaluationProvider.class)
@Component
public class WinHeatGenericEvalProvider extends GenericGaRoSingleEvalProvider {
	public final static float VALVEHOUR_MIN = 0.24f;
	
    public final static String ID = "winheat_provider_gen";
    public final static String LABEL = "Window Opening Evaluation Provider (Gen)";
    public final static String DESCRIPTION = "Calculates Window Opening and Heating Characteristics.";
    
    public WinHeatGenericEvalProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	    		GaRoDataType.WindowOpen,
	    		GaRoDataType.ValvePosition};
	}
	public static final int WINDOWSENS_IDX = 0; 
	public static final int VALVE_IDX = 1;
        
    public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
    	private final static long ONE_HOUR = 60 * 60 * 1000;
        // state variables
    	final List<Long> windowOpenDurations = new ArrayList<>(20); // in ms
    	// null when window is closed, opening time stamp otherwise; TODO initialize 
    	Long lastOpened; 
        // if true, then the valve position is assumed in the range 0-100, instead of 0-1 as specified
    	// if null, then this has not been determined yet
        Boolean usingScaledValvePosition;
    	
        private final boolean valveRequested;

    	private final InputSeriesAggregator windowSens;
    	private final InputSeriesAggregator valve;
    	//TODO: MultiAggregation not really required here as this is not a sub evaluation.
    	private final InputSeriesMultiAggregator windowValveInput;

    	final BaseOnlineEstimator valveHourTotal = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY); 
    	final BaseOnlineEstimator valveHourWinOpen = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY); 
    	final BaseOnlineEstimator valveHoursNewEstm = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
     	final long fullDuration;
  	
     	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
            this.valveRequested = requestedResults.contains(VALVE_HOURS_TOTAL) || requestedResults.contains(VALVE_HOURS_WINDOWPEN);
    	    windowSens = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    					WINDOWSENS_IDX, startEnd[1], null, AggregationMode.MAX);
    	    valve = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    					VALVE_IDX, startEnd[1]);
    	    windowValveInput = new InputSeriesMultiAggregator(new InputSeriesAggregator[] {windowSens, valve});
    	    this.lastOpened = isWindowOpenInitially(startEnd, input.get(1))? startEnd[0] : null;
    		fullDuration = startEnd[1] - startEnd[0];
     	}
     	
        private boolean isWindowOpenInitially(long[] startEnd, EvaluationInput windowInput) {
        	if (startEnd == null)
        		return false;
        	final long start = startEnd[0];
        	for (TimeSeriesData tsd : windowInput.getInputData()) {
        		if(tsd instanceof TimeSeriesDataOffline) {
    	    		final SampledValue sv = ((TimeSeriesDataOffline)tsd).getTimeSeries().getValue(start);
    	    		if (sv == null || sv.getQuality() == Quality.BAD)
    	    			continue;
    	    		if (sv.getValue().getBooleanValue())
    	    			return true;
    	    	} else {
    	    		final BooleanResource open = (BooleanResource)((TimeSeriesDataOnline)tsd).getResource();
    	    		return open.getValue();
    	    	}
        	}
        	return false;
        }
        
        public static final int MULTIAGG_WINDOWIDX = 0;
        public static final int MULTIAGG_VALVEIDX = 1;
        private void addWindowValveHours(MultiValueDuration vdm) {
        	if(vdm.duration <= 0) return;
        	if(vdm.values[MULTIAGG_WINDOWIDX] > 0) {
        		valveHourWinOpen.addValue(vdm.values[MULTIAGG_VALVEIDX], vdm.duration);
        	}
        }

    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
        	switch(idxOfRequestedInput) {
        	case WINDOWSENS_IDX: // window sensor value
        		ValueDuration vd = windowSens.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
        		
            	MultiValueDuration vdm = windowValveInput.getCurrentValueDuration(sv, dataPoint, true, MULTIAGG_WINDOWIDX, idxOfEvaluationInput);
        		addWindowValveHours(vdm);
        		
        		//This could also be done with a helper, but we keep the example as in the previous version (RoomBaseEvaluation)
        		final boolean open = (vd.value > 0);
        		if (open) {
         			if (lastOpened == null)
        				lastOpened = timeStamp;
        		} else {
        			if (lastOpened != null) {
        				final long diff = timeStamp - lastOpened;
        				windowOpenDurations.add(diff);
        				lastOpened = null;
        			}
        		}
        		//	            	}
        		break;
        	case VALVE_IDX: // valve position
        		if (!valveRequested)
        			return;
        		vd = valve.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
        		
        		valveHourTotal.addValue(vd.value, (vd.duration>0)?vd.duration:0);

            	vdm = windowValveInput.getCurrentValueDuration(sv, dataPoint, true, MULTIAGG_VALVEIDX, idxOfEvaluationInput);
        		addWindowValveHours(vdm);
        		
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
        		if(evalInstance.isRequested(VALVE_HOURS_TOTAL_NEW))
        			valveHoursNewEstm.addValue(vd.value, vd.duration);
        	} //switch
    	} //processValue
    	
    }
    
        public final static GenericGaRoResultType VALVE_HOURS_TOTAL = new GenericGaRoResultType("Valve Full Load Hours",
        		"Valve Full Load Hours Total", ID) {
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
        public final static GenericGaRoResultType VALVE_HOURS_WINDOWPEN = new GenericGaRoResultType("Valve Full Load Hours caused by window openings",
        		"Valve Full Load Hours that occured during window openings or until room temperature recovered", ID) {
    				@Override
    				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
    						List<TimeSeriesData> inputData) {
    					EvalCore cec = ((EvalCore)ec);
    					float valveScale = (cec.usingScaledValvePosition != null && cec.usingScaledValvePosition) ? 100 : 1;
    	    			double val = cec.valveHourWinOpen.getIntegralOverMilliseconds() / EvalCore.ONE_HOUR / valveScale;
    					return new SingleValueResultImpl<Float>(rt, (float) val, inputData);
    				}
        };
        public final static GenericGaRoResultType WINDOWPEN_DURATION_AV = new GenericGaRoResultType("Average window opening duration",
        		"Average window opening duration in ms", ID) {
    				@Override
    				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
    						List<TimeSeriesData> inputData) {
    					EvalCore cec = ((EvalCore)ec);
    	    			long duration = 0;
    	    			for (Long d : cec.windowOpenDurations)
    	    				duration += d;
    	    			long val = duration/(Math.max(1, cec.windowOpenDurations.size()));
    					return new SingleValueResultImpl<Long>(rt, val, inputData);
    				}
        };
        public final static GenericGaRoResultType WINDOWPEN_DURATION_NUM = new GenericGaRoResultType("Number of window openings",
        		"Number of window openings", ID) {
    				@Override
    				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
    						List<TimeSeriesData> inputData) {
    					return new SingleValueResultImpl<Integer>(rt, ((EvalCore)ec).windowOpenDurations.size(), inputData);
    				}
        };
        public final static GenericGaRoResultType VALVE_HOURS_TOTAL_NEW = new GenericGaRoResultType("Valve Full Load Hours (new)",
        		"Valve Full Load Hours Total(New)", ID) {
    				@Override
    				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
    						List<TimeSeriesData> inputData) {
       					EvalCore cec = ((EvalCore)ec);
       					float valveScale = (cec.usingScaledValvePosition != null && cec.usingScaledValvePosition) ? 100 : 1;
    					double val = ((double)(cec.valveHoursNewEstm.getAverage())*cec.fullDuration/(3600*1000)/valveScale);
    	    			if(val > 24)
    	    				System.out.println("Very large value: "+val);
    					return new SingleValueResultImpl<Float>(rt, (float) (val), inputData);
    				}
        };

        
        private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(VALVE_HOURS_TOTAL, VALVE_HOURS_WINDOWPEN, WINDOWPEN_DURATION_AV,
                		WINDOWPEN_DURATION_NUM, VALVE_HOURS_TOTAL_NEW);
        
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
