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
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.units.TemperatureResource;
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
import de.iwes.timeseries.eval.online.utils.QuantileEstimator;

/**
 * Calculate the comfort temperature that was defined by the user.
 */
@Service(EvaluationProvider.class)
@Component
public class ComfortTemperatureGenericEvalProvider extends GenericGaRoSingleEvalProvider {
	
    public final static String ID = "comforttemp_eval_provider";
    public final static String LABEL = "Comfort Temperature evaluation provider";
    public final static String DESCRIPTION = "Calculates the comfort temperature for each room defined by the user.";
    
    public ComfortTemperatureGenericEvalProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	        	GaRoDataType.TemperatureSetpointFeedback
		};
	}
	public static final int TEMPSETP_IDX = 0; 
        
    public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
     	public InputSeriesAggregator tempSetpts;
    	
    	public final QuantileEstimator upperEstimator = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
    			0.9f);
    	//public final QuantileEstimator upperEstimator999 = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
    	//		0.975f);
    	public final QuantileEstimator upperEstimator975 = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
    			0.975f);
    	public final QuantileEstimator upperEstimator950 = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
    			0.95f);
    	public final QuantileEstimator upperEstimator850 = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
    			0.85f);
    	public final QuantileEstimator lowerEstimator = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
    			0.1f);
    	public final BaseOnlineEstimator maxEstimator = new BaseOnlineEstimator(true, AverageMode.AVERAGE_ONLY);
 
    	private boolean upperQuantileRequired; 
    	
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		switch(idxOfRequestedInput) {
    		case TEMPSETP_IDX:// temperature sensor value
    			//final float val = sv.getValue().getFloatValue();
    			final ValueDuration val = tempSetpts.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
    			if(evalInstance.isRequested(COMFORT_TEMP1))
    				lowerEstimator.addValue(val.value, val.duration);
    			if(upperQuantileRequired)
    				upperEstimator.addValue(val.value, val.duration);
    			if(evalInstance.isRequested(COMFORT_TEMP2_850))
    				upperEstimator850.addValue(val.value, val.duration);
    			if(evalInstance.isRequested(COMFORT_TEMP2_950))
    				upperEstimator950.addValue(val.value, val.duration);
    			if(evalInstance.isRequested(COMFORT_TEMP2_975))
    				upperEstimator975.addValue(val.value, val.duration);
    			//if(evalInstance.isRequested(COMFORT_TEMP2_999))
    			//	upperEstimator999.addValue(val, duration);
    			if(evalInstance.isRequested(COMFORT_TEMP3) ||
    					evalInstance.isRequested(SETP_TEMP_AV) ||
    					evalInstance.isRequested(COMFORT_TEMP2_999))
    				maxEstimator.addValue(val.value, val.duration);
     		}
    	}
    	
    	Float comfortTemp2 = null;
    	float getComfortTemp2() {
    		if(comfortTemp2 == null) comfortTemp2 = upperEstimator.getQuantileMean(false, false);
    		return comfortTemp2;
    	}
    	
    }
    
    public final static GenericGaRoResultType COMFORT_TEMP1 = new GenericGaRoResultType("Lower_Comfort_Temperature",
    		TemperatureResource.class, ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).lowerEstimator.getQuantileMean(false, true), inputData);
				}
    };
    public final static GenericGaRoResultType COMFORT_TEMP2 = new GenericGaRoResultType("Upper_Comfort_Temperature",
    		TemperatureResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).getComfortTemp2(), inputData);
		}
    };
    public final static GenericGaRoResultType COMFORT_TEMP2_975 = new GenericGaRoResultType("Upper_Comfort_Temperature975Diff",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = (EvalCore)ec;
			return new SingleValueResultImpl<Float>(rt, cec.upperEstimator975.getQuantileMean(false, false) -
					cec.getComfortTemp2(), inputData);
		}
    };
    public final static GenericGaRoResultType COMFORT_TEMP2_999 = new GenericGaRoResultType("Upper_Comfort_TemperatureMaxDiff",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = (EvalCore)ec;
			return new SingleValueResultImpl<Float>(rt, cec.maxEstimator.getMaximum() -
					cec.getComfortTemp2(), inputData);
		}
    };
    public final static GenericGaRoResultType COMFORT_TEMP2_950 = new GenericGaRoResultType("Upper_Comfort_Temperature950Diff",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = (EvalCore)ec;
			return new SingleValueResultImpl<Float>(rt, cec.upperEstimator950.getQuantileMean(false, false) -
					cec.getComfortTemp2(), inputData);
		}
    };
    public final static GenericGaRoResultType COMFORT_TEMP2_850 = new GenericGaRoResultType("Upper_Comfort_Temperature850Diff",
    		FloatResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			EvalCore cec = (EvalCore)ec;
			return new SingleValueResultImpl<Float>(rt, cec.upperEstimator850.getQuantileMean(false, false) -
					cec.getComfortTemp2(), inputData);
		}
    };
    public final static GenericGaRoResultType COMFORT_TEMP3 = new GenericGaRoResultType("Maximum_Comfort_Temperature",
    		TemperatureResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).maxEstimator.getMaximum(), inputData);
		}
    };
    public final static GenericGaRoResultType SETP_TEMP_AV = new GenericGaRoResultType("Average_Setpoint_Temperature",
    		TemperatureResource.class, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((EvalCore)ec).maxEstimator.getAverage(), inputData);
		}
    };
     
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(COMFORT_TEMP1, COMFORT_TEMP2, COMFORT_TEMP3,
   		 SETP_TEMP_AV,
   		COMFORT_TEMP2_850, COMFORT_TEMP2_950, COMFORT_TEMP2_975, COMFORT_TEMP2_999);
    
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}

	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		EvalCore result = new EvalCore();
		result.tempSetpts = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
				TEMPSETP_IDX, startEnd[1]);
		result.upperQuantileRequired = requestedResults.contains(COMFORT_TEMP2);
		return result;
	}
}
