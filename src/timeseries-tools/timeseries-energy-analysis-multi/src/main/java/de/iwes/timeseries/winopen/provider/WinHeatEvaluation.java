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
package de.iwes.timeseries.winopen.provider;

import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.TimeSeriesDataOnline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;
import de.iwes.timeseries.eval.online.utils.InputSeriesMultiAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesMultiAggregator.MultiValueDuration;

public class WinHeatEvaluation extends SpecificEvalBaseImpl<WinHeatValueContainer> {
    private final boolean valveRequested;

	private final InputSeriesAggregator windowSens;
	private final InputSeriesAggregator tempSensor;
	private final InputSeriesAggregator valve;
	private final InputSeriesMultiAggregator windowValveInput;
    
    @Override
    protected WinHeatValueContainer initValueContainer(List<EvaluationInput> input) {
        final boolean windowOpenInitially = isWindowOpenInitially(startEnd, input.get(1));
    	return new WinHeatValueContainer(size, requestedResults, input, windowOpenInitially ? startEnd[0] : null);
    }
    
    public static final int MULTIAGG_WINDOWIDX = 0;
    public static final int MULTIAGG_VALVEIDX = 1;
    public WinHeatEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
            Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
        super(input, requestedResults, configurations, listener, time,
        		WinHeatEvalProvider.ID, WinHeatEvalProvider.INPUT_NUM, null); //"RoomBaseEvaluation", 3);
        
        this.valveRequested = requestedResults.contains(WinHeatEvalProvider.VALVE_HOURS_TOTAL) || requestedResults.contains(WinHeatEvalProvider.VALVE_HOURS_WINDOWPEN);
        
       tempSensor = new InputSeriesAggregator(nrInput, getIdxSumOfPrevious(),
				WinHeatEvalProvider.TEMPSENS_IDX, startEnd[1]);
       //if one window is open, then status is "open" => OR-condition
       windowSens = new InputSeriesAggregator(nrInput, getIdxSumOfPrevious(),
				WinHeatEvalProvider.WINDOWSENS_IDX, startEnd[1], null, AggregationMode.MAX);
       valve = new InputSeriesAggregator(nrInput, getIdxSumOfPrevious(),
				WinHeatEvalProvider.VALVE_IDX, startEnd[1]);
       windowValveInput = new InputSeriesMultiAggregator(new InputSeriesAggregator[] {windowSens, valve});
    }
    
    private static boolean isWindowOpenInitially(long[] startEnd, EvaluationInput windowInput) {
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
    
    private void addWindowValveHours(MultiValueDuration vdm) {
    	if(vdm.duration <= 0) return;
    	if(vdm.values[MULTIAGG_WINDOWIDX] > 0) {
    		values.valveHourWinOpen.addValue(vdm.values[MULTIAGG_VALVEIDX], vdm.duration);
    	}
    }
    
    @Override
    protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx, long timeStamp,
    		SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    	//System.out.println("New value["+idxOfRequestedInput+" / "+idxOfEvaluationInput+"]: "+timeStamp+" "+TimeUtils.getDateAndTimeString(timeStamp));
    	switch(idxOfRequestedInput) {
    	case WinHeatEvalProvider.TEMPSENS_IDX:// temperature sensor value
    		//TODO: This input is currently not used at all
    		tempSensor.getCurrentValue(sv, dataPoint, true);
    		break;
    	case WinHeatEvalProvider.WINDOWSENS_IDX: // window sensor value
    		ValueDuration vd = windowSens.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    		
        	MultiValueDuration vdm = windowValveInput.getCurrentValueDuration(sv, dataPoint, true, MULTIAGG_WINDOWIDX, idxOfEvaluationInput);
    		addWindowValveHours(vdm);
    		
    		//This could also be done with a helper, but we keep the example as in the previous version (RoomBaseEvaluation)
    		final boolean open = (vd.value > 0);
    		if (open) {
     			if (values.lastOpened == null)
    				values.lastOpened = timeStamp;
    		} else {
    			if (values.lastOpened != null) {
    				final long diff = timeStamp - values.lastOpened;
    				values.windowOpenDurations.add(diff);
    				values.lastOpened = null;
    			}
    		}
    		//	            	}
    		break;
    	case WinHeatEvalProvider.VALVE_IDX: // valve position
    		if (!valveRequested)
    			return;
    		vd = valve.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    		
    		values.valveHourTotal.addValue(vd.value, (vd.duration>0)?vd.duration:0);

        	vdm = windowValveInput.getCurrentValueDuration(sv, dataPoint, true, MULTIAGG_VALVEIDX, idxOfEvaluationInput);
    		addWindowValveHours(vdm);
    		
    		//Special workaround for faulty driver
    		final SampledValue last = dataPoint.previous(totalInputIdx);
    		if (last != null) {
    			if (values.usingScaledValvePosition == null) {
    				final float val = sv.getValue().getFloatValue();
    				if (val > 1)
    					values.usingScaledValvePosition = true;
    				else if (0 < val && val < 1)
    					values.usingScaledValvePosition = false;
    			}
     		}
    	}
    }

}
