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
package de.iwes.timeseries.provider.outsideTemperature;

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
import de.iwes.timeseries.provider.genericcollection.OutsideTempGenericEvalProvider;

/**
 * Calculate the comfort temperature that was defined by the user.
 * @deprecated use {@link OutsideTempGenericEvalProvider} instead
 */
//@Service(EvaluationProvider.class)
//@Component
@Deprecated
public class OutsideTemperatureEvalProvider extends AbstractEvaluationProvider implements GaRoSingleEvalProvider {
	
    @Reference
    protected FrameworkClock clock;
    public final static String ID = "outsidetemp_eval_provider";
    public final static String LABEL = "Outside Temperature evaluation provider";
    public final static String DESCRIPTION = "Evaluates the outside temperature measured by the gateways provided";
    
    public OutsideTemperatureEvalProvider() {
        super(null, ID, LABEL,
                DESCRIPTION);
    }

    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new OutsideTemperatureEvaluation(input, requestedResults, configurations, this, time);
//        		resultsOffered);
    }
    
    public static GaRoDataType[] INPUT_TYPES = {
        	GaRoDataType.TemperatureMeasurementRoomSensor
    };
    
	@Override
	public int[] getRoomTypes() {
		return new int[] {0};
	}
        
    @Override
    public List<RequiredInputData> inputDataTypes() {
        return GaRoEvalHelper.getInputDataTypes(INPUT_TYPES);
    }

    @Override
    public List<ResultType> resultTypes() {
        return RESULTS;
    }
    
    public final static ResultType OUTSIDE_TEMP_TIMESERIES = new ResultTypeDefault("Outside_Temperature_TimeSeries",
    		"Outside temperature of the respective gateway", ResultStructure.COMBINED);
    public final static ResultType OUTSIDE_TEMP_AVERAGE = new ResultTypeDefault("Average_Outside_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType OUTSIDE_TEMP_AVERAGE90 = new ResultTypeDefault("Average_Lower90PerCent_Outside_Temperature",
    		ResultStructure.COMBINED);
    public final static ResultType GAP_TIME = new ResultTypeDefault("GAP_TIME",
    		ResultStructure.COMBINED);
     private static final List<ResultType> RESULTS = Arrays.asList(OUTSIDE_TEMP_TIMESERIES,
    		 OUTSIDE_TEMP_AVERAGE, OUTSIDE_TEMP_AVERAGE90, GAP_TIME);

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return INPUT_TYPES;
	}

	@Override
	public Class<? extends GaRoMultiResultExtended> extendedResultDefinition() {
		return OutsideTemperatureMultiResult.class;
	}
	
	@Override
	public IntervalAggregationMode getResultAggregationMode(ResultType resultType) {
		return IntervalAggregationMode.AVERAGING;
	}
}
