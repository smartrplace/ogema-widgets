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
package de.iwes.timeseries.roomeval.provider;

import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.TimeSeriesDataOnline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;

public class RoomBaseEvaluation extends SpecificEvalBaseImpl<RoomBaseValueContainer> {
	public static final int TEMPSENS_IDX = 0; 
	public static final int WINDOWSENS_IDX = 1; 
	public static final int VALVE_IDX = 2;
	public static final int INPUT_NUM = 3;
    //private final int nrTempSens;
    //private final int nrWindowSens;
    // state variables:
    private final boolean valveRequested;
    
    @Override
    protected RoomBaseValueContainer initValueContainer(List<EvaluationInput> input) {
        final boolean windowOpenInitially = isWindowOpenInitially(startEnd, input.get(1));
    	return new RoomBaseValueContainer(size, size-nrInput[0]-nrInput[1], requestedResults, input, windowOpenInitially ? startEnd[0] : null);
    }
    
    public RoomBaseEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
            Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
        super(input, requestedResults, configurations, listener, time,
        		RoomBaseEvalProvider.ID, INPUT_NUM, null); //"RoomBaseEvaluation", 3);
        //this.nrTempSens = nrInput[0];
        //this.nrWindowSens = nrInput[1];
        
        this.valveRequested = requestedResults.contains(RoomBaseEvalProvider.VALVE_HOURS_TOTAL) || requestedResults.contains(RoomBaseEvalProvider.VALVE_HOURS_WINDOWPEN);
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
    
    @Override
    protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx, long timeStamp,
    		SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    	switch(idxOfRequestedInput) {
    	case TEMPSENS_IDX:// temperature sensor value
    		// ?
    		break;
    	case WINDOWSENS_IDX: // window sensor value
    		//if (windowRequested) { // also relevant for valve
    		final boolean open = sv.getValue().getBooleanValue();
    		if (open) {
    			values.openWindowsCounter++;
    			if (values.lastOpened == null)
    				values.lastOpened = timeStamp;
    		} else {
    			if (values.openWindowsCounter > 0)
    				values.openWindowsCounter--;
    			if (values.lastOpened != null && values.openWindowsCounter == 0) {
    				if (valveRequested) {
    					//for (int vIdx=nrTempSens+nrWindowSens; vIdx < size; vIdx++) {
    					for (int vsIdx=0; vsIdx < nrInput[VALVE_IDX]; vsIdx++) {
    						final SampledValue lastV = dataPoint.previous(getTotalInputIdx(VALVE_IDX, vsIdx));
    						final SampledValue svv = dataPoint.getElement(getTotalInputIdx(VALVE_IDX, vsIdx));
    						if (lastV == null || lastV.getQuality() == Quality.BAD) 
    							continue;
    						final long t0 = lastV.getTimestamp();
    						final float itg2;
    						if (t0 < values.lastOpened) {
    							itg2 = integrate(lastV, new SampledValue(lastV.getValue(), values.lastOpened, lastV.getQuality()), svv, svv, InterpolationMode.STEPS);
    						} else {
    							itg2 = integrate(lastV, lastV, svv, svv, InterpolationMode.STEPS);
    						}
    						if (!Float.isNaN(itg2)) {
    							values.valveOpenWindowOpenMillis += itg2;
    						}
    					}
    				}
    				final long diff = timeStamp - values.lastOpened;
    				values.windowOpenDurations.add(diff);
    				values.lastOpened = null;
    			}
    		}
    		//	            	}
    		break;
    	case VALVE_IDX: // valve position
    		if (!valveRequested)
    			return;
    		final SampledValue last = dataPoint.previous(totalInputIdx);
    		if (last != null) {
    			final float itg = integrate(null, last, sv, null, InterpolationMode.STEPS);
    			if (Float.isNaN(itg))
    				return;
    			if (values.usingScaledValvePosition == null) {
    				final float val = sv.getValue().getFloatValue();
    				if (val > 1)
    					values.usingScaledValvePosition = true;
    				else if (0 < val && val < 1)
    					values.usingScaledValvePosition = false;
    			}
    			values.valveOpenMillis += itg;
    			if (values.lastOpened != null) {
    				final long t0 = last.getTimestamp();
    				if (t0 < values.lastOpened) {
    					final float itg2 = integrate(last, new SampledValue(last.getValue(), values.lastOpened, last.getQuality()), sv, sv, InterpolationMode.STEPS);
    					if (!Float.isNaN(itg2))
    						values.valveOpenWindowOpenMillis += itg2;
    				} else {
    					values.valveOpenWindowOpenMillis += itg;
    				}
    			}
    		}
    	}
    }

}
