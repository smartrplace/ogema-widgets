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
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.generictype.GenericAttribute;
import org.ogema.generictype.GenericAttributeImpl;
import org.ogema.generictype.GenericDataTypeDeclaration.TypeCardinality;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesResultImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI.Level;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProvider;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator;
import de.iwes.timeseries.eval.online.utils.BaseOnlineEstimator.AverageMode;
import de.iwes.timeseries.eval.online.utils.QuantileEstimator;
import de.iwes.timeseries.eval.online.utils.TimeSeriesOnlineBuilder;

/**
 * Calculate the comfort temperature that was defined by the user.
 */
@Service(EvaluationProvider.class)
@Component
public class OutsideTempGenericEvalProvider extends GenericGaRoSingleEvalProvider {
	
    public final static String ID = "outsidetemp_eval_provider";
    public final static String LABEL = "Outside Temperature evaluation provider";
    public final static String DESCRIPTION = "Evaluates the outside temperature measured by the gateways provided";
    
    public OutsideTempGenericEvalProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	        	GaRoDataType.TemperatureMeasurementRoomSensor
		};
	}
	public static final int TEMPSETP_IDX = 0; 
        
	@Override
	public int[] getRoomTypes() {
		return new int[] {0};
	}

 	public class OutsideTempEvalData extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
    	//private final static long ONE_HOUR = 60 * 60 * 1000;
    	public final QuantileEstimator upperEstimator = new QuantileEstimator(MAX_QUANTILE_DATATOHOLD_PER_SET,
    			0.9f);
    	public final BaseOnlineEstimator baseEstimator = new BaseOnlineEstimator(false, AverageMode.AVERAGE_ONLY);
    	public final TimeSeriesOnlineBuilder tsBuilder = new TimeSeriesOnlineBuilder();
  
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		switch(idxOfRequestedInput) {
    		case TEMPSETP_IDX:// temperature sensor value
    			final float val = sv.getValue().getFloatValue();
    			if(evalInstance.isRequested(OUTSIDE_TEMP_AVERAGE))
    				baseEstimator.addValue(val, duration);
    			if(evalInstance.isRequested(OUTSIDE_TEMP_AVERAGE90))
    				upperEstimator.addValue(val, duration);
    			if(evalInstance.isRequested(OUTSIDE_TEMP_TIMESERIES)) {
    				tsBuilder.addValue(sv);
    				evalInstance.callListeners(OUTSIDE_TEMP_TIMESERIES, timeStamp, val);
    			}
    		}
    	}
    	
    	@Override
    	protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput, int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		tsBuilder.addValue(new SampledValue(new FloatValue(Float.NaN), sv.getTimestamp(), Quality.BAD));
    	}
    }
    
    public final static GenericGaRoResultType OUTSIDE_TEMP_TIMESERIES = new GenericGaRoResultType("Outside_Temperature_TimeSeries",
    		"Outside temperature of the respective gateway", TemperatureResource.class,
    		TypeCardinality.TIME_SERIES, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new TimeSeriesResultImpl(rt, ((OutsideTempEvalData)ec).tsBuilder.getTimeSeries(), inputData);
		}
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}
    };
    public final static GenericGaRoResultType OUTSIDE_TEMP_AVERAGE = new GenericGaRoResultType("Average_Outside_Temperature",
    		TemperatureResource.class, ID)  {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((OutsideTempEvalData)ec).baseEstimator.getAverage(), inputData);
		}
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}
    };
    public final static GenericGaRoResultType OUTSIDE_TEMP_AVERAGE90 = new GenericGaRoResultType("Average_Lower90PerCent_Outside_Temperature",
    		TemperatureResource.class, ID)  {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return new SingleValueResultImpl<Float>(rt, ((OutsideTempEvalData)ec).upperEstimator.getQuantileMean(true, true), inputData);
		}
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE,
					GenericAttributeImpl.TEST});
		}
    };
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(OUTSIDE_TEMP_TIMESERIES,
    		 OUTSIDE_TEMP_AVERAGE, OUTSIDE_TEMP_AVERAGE90);
    
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}

	public final static GenericGaRoResultType OUTSIDE_TEMP_TIMESERIES_OVERALL = new GenericGaRoResultType("Outside_Temperature_TimeSeries_Overall",
    		"Outside temperature of the respective gateway over entire evaluation period", TemperatureResource.class,
    		TypeCardinality.TIME_SERIES, Level.OVERALL, ID) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return null;
		}
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}
    };
    public final static GenericGaRoResultType OUTSIDE_TEMP_AVERAGE_OVERALL = new GenericGaRoResultType("Average_Outside_Temperature_Overall",
    		"Average outside temperature over entire evaluation period", TemperatureResource.class, Level.OVERALL, ID)  {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return null;
		}
		@Override
		public List<GenericAttribute> attributes() {
			return Arrays.asList(new GenericAttribute[] {GenericAttributeImpl.OUTSIDE});
		}
    };
    @Override
    protected List<GenericGaRoResultType> resultTypesGaRoOverall() {
    	return Arrays.asList(OUTSIDE_TEMP_TIMESERIES_OVERALL, OUTSIDE_TEMP_AVERAGE_OVERALL);
    }
    
	@Override
	public Class<? extends GaRoMultiResultExtended> extendedResultDefinition() {
		return OutsideTempGenericMultiResult.class;
	}
	@Override
	public Class<? extends GaRoSuperEvalResult<?>> getSuperResultClassForDeserialization() {
		return OutsideTempGaRoSuperEvalResult.class;
	}

	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		return new OutsideTempEvalData();
	}
}
