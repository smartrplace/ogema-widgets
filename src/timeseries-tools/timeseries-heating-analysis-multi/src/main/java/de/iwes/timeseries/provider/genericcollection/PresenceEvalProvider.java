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
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProvider;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.AggregationMode;
import de.iwes.timeseries.eval.online.utils.InputSeriesAggregator.ValueDuration;
import de.iwes.timeseries.eval.online.utils.TimeSeriesFixedStepOnlineBuilder;

/**
 * Evaluate user presence
 */
@Service(EvaluationProvider.class)
@Component
public class PresenceEvalProvider extends GenericGaRoSingleEvalProvider {
	
    public final static String ID = "presence_overall_eval_provider";
    public final static String LABEL = "Presence Overall evaluation provider";
    public final static String DESCRIPTION = "Evaluates the overall presence on gateways over day";
    
    public PresenceEvalProvider() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	        	GaRoDataType.MotionDetection
		};
	}
	public static final int MOTION_IDX = 0; 
        
	public static final int TIMESERIES_STEP1 = 60000;
	
 	public class EvalCore extends GenericGaRoEvaluationCore {
    	public final static int MAX_QUANTILE_DATATOHOLD_PER_SET = 2000;
    	
    	//private final static long ONE_HOUR = 60 * 60 * 1000;
    	long presenceTime = 0;
    	long absentTime = 0;
    	final long totalTime;
    	
    	public final InputSeriesAggregator motion;
    	public final TimeSeriesFixedStepOnlineBuilder tsBuilder;

    	public EvalCore(List<EvaluationInput> input, List<ResultType> requestedResults,
    			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
    			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
    		totalTime = startEnd[1] - startEnd[0];
    		motion = new InputSeriesAggregator(nrInput, idxSumOfPrevious,
    				MOTION_IDX, startEnd[1], null, AggregationMode.MAX);
    		tsBuilder = new TimeSeriesFixedStepOnlineBuilder(TIMESERIES_STEP1, startEnd[0], AggregationMode.AVERAGING);
    	}
    	
    	@Override
    	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    			int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		switch(idxOfRequestedInput) {
    		case MOTION_IDX:// temperature sensor value
    			final ValueDuration val = motion.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, true);
   				tsBuilder.addValue(sv, val.duration);
   				evalInstance.callListeners(PRESENCE_FIXED_TIMESERIES, timeStamp, val.value);
    		}
    	}
    	
    	@Override
    	protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput, int totalInputIdx, long timeStamp,
    			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
    		tsBuilder.addValue(new SampledValue(new FloatValue(Float.NaN), sv.getTimestamp(), Quality.BAD), duration);
    	}
    }
    
    public final static GenericGaRoResultType PRESENCE_SHARE = new GenericGaRoResultType("Presence_Timeshare", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					return new SingleValueResultImpl<Float>(rt, (float) ((double)cec.presenceTime/(cec.absentTime+cec.presenceTime)), inputData);
				}
    };
    public final static GenericGaRoResultType TIME_EVALUATED_SHARE = new GenericGaRoResultType("Evaluated_Timeshare", ID) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					EvalCore cec = ((EvalCore)ec);
					return new SingleValueResultImpl<Float>(rt, (float) ((double)(cec.absentTime+cec.presenceTime)/cec.totalTime), inputData);
				}
    };
    public final static GenericGaRoResultType PRESENCE_FIXED_TIMESERIES = new GenericGaRoResultType("Outside_Temperature_TimeSeries",
    		"Outside temperature of the respective gateway") {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					return new TimeSeriesResultImpl(rt, ((EvalCore)ec).tsBuilder.getTimeSeries(), inputData);
				}
    };
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList(PRESENCE_SHARE,
    		TIME_EVALUATED_SHARE, PRESENCE_FIXED_TIMESERIES);
    
	@Override
	public Class<? extends GaRoMultiResultExtended> extendedResultDefinition() {
		return PresenceMultiResult.class;
	}

	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}

	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		return new EvalCore(input, requestedResults, configurations, listener, time, size, nrInput, idxSumOfPrevious, startEnd);
	}
}
