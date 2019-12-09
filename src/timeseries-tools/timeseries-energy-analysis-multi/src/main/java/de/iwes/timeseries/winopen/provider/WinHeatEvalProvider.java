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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.administration.FrameworkClock;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.ResultType.ResultStructure;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.AbstractEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.ResultTypeDefault;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider.IntervalAggregationMode;

/**
 * Calculate thermal valve opening hours for a room and evaluate separately openings that are associated with
 * opening a window. Note that all sensors are assumed to be connected to a single room and more than one sensor
 * of a type may be given for that room. A single evaluation instance thus provides the same number of results
 * no matter how many input time series are given.
 */
@Service(EvaluationProvider.class)
@Component
public class WinHeatEvalProvider extends AbstractEvaluationProvider implements GaRoSingleEvalProvider {
	
    @Reference
    protected FrameworkClock clock;
    public final static String ID = "winheat_provider";
    public final static String LABEL = "Window Opening Evaluation Provider";
    public final static String DESCRIPTION = "Calculates Window Opening and Heating Characteristics.";
    
//    MultiResultsOffered resultsOffered = new MultiResultsOffered(resultTypes());
    
    public WinHeatEvalProvider() {
        super(null, ID, LABEL,
                DESCRIPTION);
    }

    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new WinHeatEvaluation(input, requestedResults, configurations, this, time);
//        		resultsOffered);
    }
    
    public static GaRoDataType[] INPUT_TYPES = {
        	GaRoDataType.TemperatureMeasurementRoomSensor,
    		GaRoDataType.WindowOpen,
    		GaRoDataType.ValvePosition};
    
	public static final int TEMPSENS_IDX = 0; 
	public static final int WINDOWSENS_IDX = 1; 
	public static final int VALVE_IDX = 2;
	public static final int INPUT_NUM = 3;
       
    @Override
    public List<RequiredInputData> inputDataTypes() {
        return GaRoEvalHelper.getInputDataTypes(INPUT_TYPES);
    }

    @Override
    public List<ResultType> resultTypes() {
        return RESULTS;
    }
    
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

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return INPUT_TYPES;
	}

	@Override
	public int[] getRoomTypes() {
		return null;
	}

	@Override
	public Class<? extends GaRoMultiResultExtended> extendedResultDefinition() {
		return null;
	}
	
	@Override
	public IntervalAggregationMode getResultAggregationMode(ResultType resultType) {
		return IntervalAggregationMode.AVERAGING;
	}
}
