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
package de.iwes.timeseries.provider.heatingloss;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.felix.scr.annotations.Reference;
import org.ogema.core.administration.FrameworkClock;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.ResultType.ResultStructure;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.base.provider.utils.AbstractEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.ResultTypeDefault;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProviderPreEvalRequesting;
import de.iwes.timeseries.provider.comfortTemp.ComfortTemperatureEvalProvider;
import de.iwes.timeseries.provider.outsideTemperature.OutsideTemperatureEvalProvider;

/**
 * Calculate heating losses of each room.
 * @Deprecated never fully implemented
 */
//@Service(EvaluationProvider.class)
//@Component
@Deprecated
public class HeatLossEvalProvider extends AbstractEvaluationProvider implements GaRoSingleEvalProviderPreEvalRequesting {
	
    @Reference
    protected FrameworkClock clock;
    public final static String ID = "heatloss_eval_provider";
    public final static String LABEL = "Heating Loss evaluation provider";
    public final static String DESCRIPTION = "Evaluates the heating losses of the rooms. Note that"
    		+ "currently only works with Stepsize DAY";
    
    public HeatLossEvalProvider() {
        super(null, ID, LABEL,
                DESCRIPTION);
    }

    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new HeatLossEvaluation(input, requestedResults, configurations, this, time,
        		this);
//        		resultsOffered);
    }
    
    public static GaRoDataType[] INPUT_TYPES = {
        	GaRoDataType.TemperatureMeasurementRoomSensor,
         	GaRoDataType.TemperatureSetpointFeedback,
   };
	public static final int TEMP_M_IDX = 0; 
	public static final int TEMPSETP_IDX = 1; 
	public static final int INPUT_NUM = 2;
   
    @Override
    public List<RequiredInputData> inputDataTypes() {
        return GaRoEvalHelper.getInputDataTypes(INPUT_TYPES);
    }

    @Override
    public List<ResultType> resultTypes() {
        return RESULTS;
    }
    
    public final static ResultType DAILY_TEMPERATURE_FIGURE = new ResultTypeDefault("Daily_Temperature_Figure",
    		"according to VDI 2067", ResultStructure.COMBINED);
    public final static ResultType HEATING_DEGREE_DAYS = new ResultTypeDefault("Heating_Degree_Days",
    		"according to VDI 3807 with fixed inner heat gain equivalent of 5K and using comfort temperature 2", ResultStructure.COMBINED);
    public final static ResultType HEATING_DEGREE_DAYS_LOWERED = new ResultTypeDefault("Heating_Degree_Days",
    		"according to VDI 3807 with fixed inner heat gain equivalent of 5K and using real setpoint", ResultStructure.COMBINED);
    //public final static ResultType HEATING_DEGREE_DAYS_LOWERED = new ResultTypeDefault("Heating_Degree_Days_Lowered",
    //		"like heating degree days, but with measured temperature", ResultStructure.COMBINED);
    //public final static ResultType HEATING_DEGREE_DAYS_PER_YEAR = new ResultTypeDefault("Heating_Degree_Days_Per_Year",
    //		"Upscaling of heating degree days to entire year", ResultStructure.COMBINED);
    public final static ResultType SETPOINT_RELATIVE_AV = new ResultTypeDefault("Setpoint_Relative_Av",
    		"Average reduction of setpoint below comfort temperature2. Setpoints above count negative.", ResultStructure.COMBINED);
    public final static ResultType SETPOINT_REDUCTION_AV = new ResultTypeDefault("Setpoint_Reduction_Av",
    		"Average reduction of setpoint below comfort temperature2. Only reductions are considered, so"
    		+ "differences equal or greater zero are ignored", ResultStructure.COMBINED);
    public final static ResultType GAP_TIME = new ResultTypeDefault("GAP_TIME",
    		ResultStructure.COMBINED);
     private static final List<ResultType> RESULTS = Arrays.asList(DAILY_TEMPERATURE_FIGURE,
    		 HEATING_DEGREE_DAYS, HEATING_DEGREE_DAYS_LOWERED,
    		 SETPOINT_RELATIVE_AV, SETPOINT_REDUCTION_AV,
    		 GAP_TIME);

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return INPUT_TYPES;
	}

	@Override
	public int[] getRoomTypes() {
		return null;
	}
	
	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return Arrays.asList(new PreEvaluationRequested(OutsideTemperatureEvalProvider.ID),
				new PreEvaluationRequested(ComfortTemperatureEvalProvider.ID));
	}

	public GaRoPreEvaluationProvider outsideTempProvider;
	public GaRoPreEvaluationProvider comfortTempProvider;
	
	@Override
	public void preEvaluationProviderAvailable(int requestIdx, String providerId,
			GaRoPreEvaluationProvider provider) {
		switch(requestIdx) {
		case 0:
			outsideTempProvider = (GaRoPreEvaluationProvider) provider;
			break;
		case 1:
			comfortTempProvider = (GaRoPreEvaluationProvider) provider;
			break;
		}
		
	}

	public String currentGwId;
	public String currentRoomId;
	@Override
	public void provideCurrentValues(String gwId, String roomId, long currentStartTime, AbstractSuperMultiResult<GaRoMultiResult> superResult) {
		currentGwId = gwId;
		currentRoomId = roomId;
	}

	@Override
	public Class<? extends GaRoMultiResultExtended> extendedResultDefinition() {
		return null;
	}

	@Override
	public IntervalAggregationMode getResultAggregationMode(ResultType resultType) {
		return IntervalAggregationMode.AVERAGING;
	}

	@Override
	public void performSuperEval(AbstractSuperMultiResult<?> destination) {}
}
