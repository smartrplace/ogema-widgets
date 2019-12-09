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
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.TimeSeriesDataOffline;
import de.iwes.timeseries.eval.api.TimeSeriesDataOnline;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;

public class RoomBaseEvaluation extends EvaluationBaseImpl {

    private final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources

//    private final MultiResultManagement resultMgmt;
    
    private final String id;
    // nrtempSens + nrWindowSens + nrValves = size
    private final int nrTempSens;
    private final int nrWindowSens;
    // state variables:
    private final RoomBaseValueContainer values;
    private final boolean valveRequested;
    //private final boolean windowRequested;

    
//    ResultProviderFloatDirect valveTotalPro = new ResultProviderFloatDirect();
//    ResultProviderFloatDirect valveWindow = new ResultProviderFloatDirect();
//    ResultProviderLongDirect windowOpenAv = new ResultProviderLongDirect();
//    ResultProviderIntDirect windowOpenCount = new ResultProviderIntDirect();

    public RoomBaseEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
            Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time) {
//            MultiResultsOffered resultsOffered) {
        super(input, requestedResults, configurations, listener, time);
        if (input.size() != 3)
        	throw new IllegalArgumentException("Expecting exactly three types of input time series, got " + input.size());
        this.id = "RoomBaseEvaluation_" + idcounter.incrementAndGet();
        this.nrTempSens = input.get(0).getInputData().size();
        this.nrWindowSens = input.get(1).getInputData().size();
        
        final long[] startEnd = EvaluationUtils.getStartAndEndTime(configurations, input, false);
        final boolean windowOpenInitially = isWindowOpenInitially(startEnd, input.get(1));
        
        this.values = new RoomBaseValueContainer(size, size-nrTempSens-nrWindowSens, requestedResults, input, windowOpenInitially ? startEnd[0] : null);
        this.valveRequested = requestedResults.contains(RoomBaseEvalProvider.VALVE_HOURS_TOTAL) || requestedResults.contains(RoomBaseEvalProvider.VALVE_HOURS_WINDOWPEN);
        //this.windowRequested =  requestedResults.contains(RoomBaseEvalProvider.WINDOWPEN_DURATION_AV) || 
        //		requestedResults.contains(RoomBaseEvalProvider.VALVE_HOURS_WINDOWPEN) || 
        //		requestedResults.contains(RoomBaseEvalProvider.WINDOWPEN_DURATION_NUM);
//        this.resultMgmt = resultsOffered.getEvaluationInstanceResultMgmt(
//        		valveTotalPro, valveWindow, windowOpenAv, windowOpenCount);
//        integralValues = new float[size];
//        Arrays.fill(integralValues, Float.NaN);
//        integralSize = new long[size];
//        Arrays.fill(integralSize, -1);
//        counter = new int[size];
//        isInGap = new boolean[size];
//        Arrays.fill(isInGap, true);
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
    public String id() {
        return id;
    }

    @Override
    protected Map<ResultType, EvaluationResult> getCurrentResults() {
    	return values.getCurrentResults();
    }

    @Override
    protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
    	if (requestedResults.isEmpty())
    		return;
    	synchronized (values) {
	        for (Map.Entry<Integer, SampledValue> entry : dataPoint.getElements().entrySet()) {
	            final int idx = entry.getKey();
	            final SampledValue sv = entry.getValue();
	            final boolean quality = sv.getQuality() == Quality.GOOD;
	            if (!quality) {
	                values.isInGap[idx] = true;
	                continue;
	            }	            
	            values.isInGap[idx] = false;
	            final long t = sv.getTimestamp();
	            if (idx < nrTempSens) { // temperature sensor value
	            	// ?
	            } else if (idx < nrTempSens + nrWindowSens) { // window sensor value
//	            	if (windowRequested) { // also relevant for valve
		            	final boolean open = sv.getValue().getBooleanValue();
		            	if (open) {
		            		values.openWindowsCounter++;
		            		if (values.lastOpened == null)
		            			values.lastOpened = t;
		            	} else {
		            		if (values.openWindowsCounter > 0)
		            			values.openWindowsCounter--;
		            		if (values.lastOpened != null && values.openWindowsCounter == 0) {
			            		if (valveRequested) {
			            			for (int vIdx=nrTempSens+nrWindowSens; vIdx < size; vIdx++) {
			            				final SampledValue lastV = dataPoint.previous(vIdx);
			            				if (lastV == null || lastV.getQuality() == Quality.BAD) 
			            					continue;
			            				final long t0 = lastV.getTimestamp();
			            				final float itg2;
			            				if (t0 < values.lastOpened) {
			            					itg2 = integrate(lastV, new SampledValue(lastV.getValue(), values.lastOpened, lastV.getQuality()), sv, sv, InterpolationMode.STEPS);
			            				} else {
			            					itg2 = integrate(lastV, lastV, sv, sv, InterpolationMode.STEPS);
			            				}
			            				if (!Float.isNaN(itg2)) {
			            					values.valveOpenWindowOpenMillis += itg2;
			            				}
			            			}
			            		}
		            			final long diff = t - values.lastOpened;
		            			values.windowOpenDurations.add(diff);
		            			values.lastOpened = null;
		            		}
		            	}
//	            	}
	            } else { // valve position
	            	if (valveRequested) {
	            		final SampledValue last = dataPoint.previous(idx);
	     	            if (last != null) {
	     	            	final float itg = integrate(null, last, sv, null, InterpolationMode.STEPS);
	     	            	if (Float.isNaN(itg))
	     	            		continue;
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
    	}
    }
    
}
