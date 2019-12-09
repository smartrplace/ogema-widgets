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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.administration.FrameworkClock;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.TemperatureResource;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.ResultType.ResultStructure;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.AbstractEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.RequiredInputDefault;
import de.iwes.timeseries.eval.base.provider.utils.ResultTypeDefault;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate thermal valve opening hours for a room and evaulaute separatly openings that are associated with
 * opening a window. Note that all sensors are assumed to be connected to a single room and more than one sensor
 * of a type may be given for that room. A single evaluation instance thus provides the same number of results
 * no matter how many input time series are given.
 */
// FIXME currently average window duration is in fact summed up window opening duration
@Service(EvaluationProvider.class)
@Component
public class RoomBaseEvalProvider extends AbstractEvaluationProvider {
	
    @Reference
    protected FrameworkClock clock;
    public final static String ID = "roombase_eval_provider";
    
//    MultiResultsOffered resultsOffered = new MultiResultsOffered(resultTypes());
    
    public RoomBaseEvalProvider() {
        super(null, ID, "Basic Room evaluation provider",
                "Calculates Window Opening and Heating Characteristics.");
    }

    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new RoomBaseEvaluation(input, requestedResults, configurations, this, time);
//        		resultsOffered);
    }

    @Override
    public List<RequiredInputData> inputDataTypes() {
        return Arrays.asList(INPUT_TEMP_SENS, INPUT_WINDOW_SENS, INPUT_VALVE_SENS);
    }

    @Override
    public List<ResultType> resultTypes() {
        return RESULTS;
    }

    private static final RequiredInputData INPUT_TEMP_SENS = new RequiredInputDefault(
    		"Temperature measurements in the room", "Provide all temperature measurement timesieres that shall be"
    				+ "evaluated. This may include temperature measurements of thermostats if they shall be included.",
    				TemperatureResource.class);    
    private static final RequiredInputData INPUT_WINDOW_SENS = new RequiredInputDefault(
    		"Window opening sensors of the room", "Provide time series for all window opening sensors of the room",
    		BooleanResource.class);    
    private static final RequiredInputData INPUT_VALVE_SENS = new RequiredInputDefault(
    		"Valve positions of the room", "Provide time series for all valves of the room",
    		FloatResource.class);    
    //private static final RequiredInputData CONFIG_ROOMTYPE = new ConfigurationDefault(
    //		"Room type", "Room type according to OGEMA model definition",
    //		IntegerResource.class, 1);
    
    public final static ResultType VALVE_HOURS_TOTAL = new ResultTypeDefault("Valve Full Load Hours",
    		"Valve Full Load Hours Total", ResultStructure.COMBINED);
    public final static ResultType VALVE_HOURS_WINDOWPEN = new ResultTypeDefault("Valve Full Load Hours caused by window openings",
    		"Valve Full Load Hours that occured during window openings or until room temperature recovered", ResultStructure.COMBINED);
    public final static ResultType WINDOWPEN_DURATION_AV = new ResultTypeDefault(
    		"Average window opening duration",
    		"Average window opening duration in ms", ResultStructure.COMBINED);
    public final static ResultType WINDOWPEN_DURATION_NUM = new ResultTypeDefault(
    		"Number of window openings",
    		"Number of window openings", ResultStructure.COMBINED);
    private static final List<ResultType> RESULTS = Arrays.asList(VALVE_HOURS_TOTAL, VALVE_HOURS_WINDOWPEN, WINDOWPEN_DURATION_AV,
            		WINDOWPEN_DURATION_NUM);

    public static void printResults(final String room, final Map<ResultType, EvaluationResult> results, long[] startEnd) {
    	int ll = Integer.getInteger("org.ogema.multieval.loglevel", 10);
		if(ll >= 10) System.out.println("*********************************");
		if(ll >= 10) System.out.println("Room base evaluation results: " + room);
    	if (startEnd!= null)
    		if(ll >= 10) System.out.println("Duration: " + new Date(startEnd[0]) + " - " + new Date(startEnd[1])); // FIXME
    	for (ResultType type : RESULTS) {
    		if (results.containsKey(type)) {
    			final SingleEvaluationResult r = results.get(type).getResults().iterator().next();
    			if (r instanceof SingleValueResult<?>) {
    				final Object value = ((SingleValueResult<?>) r).getValue();
    				if(ll >= 10)  System.out.println(" " + type.description(OgemaLocale.ENGLISH) + ": " + value);
    			}
    		}
    	}
    	if(ll >= 10) System.out.println("*********************************");
    }
    
}
