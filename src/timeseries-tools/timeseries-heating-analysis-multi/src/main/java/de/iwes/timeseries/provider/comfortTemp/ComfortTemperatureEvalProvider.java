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
package de.iwes.timeseries.provider.comfortTemp;

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

/**
 * Calculate the comfort temperature that was defined by the user.
 */
//@Service(EvaluationProvider.class)
//@Component
@Deprecated
public class ComfortTemperatureEvalProvider extends AbstractEvaluationProvider implements GaRoSingleEvalProvider {
	
    @Reference
    protected FrameworkClock clock;
    public final static String ID = "comforttemp_eval_provider_Bak";
    public final static String LABEL = "Comfort Temperature evaluation provider (V1)";
    public final static String DESCRIPTION = "Calculates the comfort temperature for each room defined by the user. (V1)";
    
    public ComfortTemperatureEvalProvider() {
        super(null, ID, LABEL,
                DESCRIPTION);
    }

    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new ComfortTemperatureEvaluation(input, requestedResults, configurations, this, time);
//        		resultsOffered);
    }
    
    public static GaRoDataType[] INPUT_TYPES = {
        	GaRoDataType.TemperatureSetpointFeedback
    };
        
    @Override
    public List<RequiredInputData> inputDataTypes() {
        return GaRoEvalHelper.getInputDataTypes(INPUT_TYPES);
    }

    @Override
    public List<ResultType> resultTypes() {
        return RESULTS;
    }
    
    public final static ResultType COMFORT_TEMP1 = new ResultTypeDefault("Lower_Comfort_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType COMFORT_TEMP2 = new ResultTypeDefault("Upper_Comfort_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType COMFORT_TEMP3 = new ResultTypeDefault("Maximum_Comfort_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType SETP_TEMP_AV = new ResultTypeDefault("Average_Setpoint_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType PERCENTILE10 = new ResultTypeDefault("Percentile10_Setpoint_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType PERCENTILE90 = new ResultTypeDefault("Percentile90_Setpoint_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType SETPOINTS_USED_NUM = new ResultTypeDefault("Setpoints_found_for_the_room",
    		ResultStructure.COMBINED);
    public final static ResultType MULTI_THERMOSTAT_NUM = new ResultTypeDefault("Thermostats_in_room",
    		"Thermostats in room",
    		ResultStructure.COMBINED);
    public final static ResultType MULTI_THERMOSTAT_DEVIATIONS_FOUND_NUM = new ResultTypeDefault("Inter_Thermostat_Deviations",
    		"Inter-Thermostat-Deviations - can only be non-zero if more than one thermostat in the room",
    		ResultStructure.COMBINED);
    public final static ResultType GAP_TIME = new ResultTypeDefault("GAP_TIME",
    		ResultStructure.COMBINED);
     private static final List<ResultType> RESULTS = Arrays.asList(COMFORT_TEMP1, COMFORT_TEMP2, COMFORT_TEMP3,
    		 SETP_TEMP_AV,
    		 PERCENTILE10, PERCENTILE90, SETPOINTS_USED_NUM, MULTI_THERMOSTAT_NUM,
    		 MULTI_THERMOSTAT_DEVIATIONS_FOUND_NUM, GAP_TIME);

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

    /*
    @Override
    public List<RequiredInputData> inputDataTypes() {
        return Arrays.asList(INPUT_TEMP_SENS, INPUT_WINDOW_SENS, INPUT_VALVE_SENS);
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
    */
}
