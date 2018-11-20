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
package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ogema.core.model.simple.TimeResource;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.RequiredInputData;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;
import de.iwes.timeseries.eval.base.provider.utils.AbstractEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;
import de.iwes.timeseries.eval.garo.multibase.GaRoMultiResultExtended;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;

/**
 * Generic GaRoSingleEvalProvider that allows to defined eval providers without
 * separate EvaluationInstance classes.
 */
public abstract class GenericGaRoSingleEvalProvider extends AbstractEvaluationProvider implements GaRoSingleEvalProvider {
	protected String currentGwId;
	protected String currentRoomId;
	protected long currentStartTime;
	protected AbstractSuperMultiResult<GaRoMultiResult> currentSuperResult;

	protected abstract List<GenericGaRoResultType> resultTypesGaRo();
	/**Overall results are overall gateways and all time intervals. Overwrite this if overall
	 * results are available.*/
	protected List<GenericGaRoResultType> resultTypesGaRoOverall() {
		return Collections.emptyList();
	};
	protected abstract GenericGaRoEvaluationCore initEval(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd);
    
	@Override
	public Class<? extends GaRoMultiResultExtended> extendedResultDefinition() {
		return null;
	}
 	@Override
	public int[] getRoomTypes() {
		return null;
	}

 	/** See {@link SpecificEvalBaseImpl#maximumGapTimeAccepted(int)}.
 	 * 
 	 * @return For each index parameter of the
 	 * method in SpecificEvalBaseImpl a value in the array returned shall be given. If not overriden
 	 * or returning null the standard maximum gap time is used.
 	 */
 	protected long[] getMaximumGapTimes() {
 		return null;
 	}
	
	public GenericGaRoSingleEvalProvider(String id, String label, String description) {
        super(null, id, label, description);
    }
    
    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        long[] maxGapTimes = getMaximumGapTimes();
        if(maxGapTimes == null)
        	return new GenericGaRoSingleEvaluation(input, requestedResults, configurations, this, time,
        		this);
//        		resultsOffered);
        else return new GenericGaRoSingleEvaluation(input, requestedResults, configurations, this, time,
        		this, maxGapTimes);
    }
        
    @Override
    public List<RequiredInputData> inputDataTypes() {
        return GaRoEvalHelper.getInputDataTypes(getGaRoInputTypes());
    }

	@Override
	public IntervalAggregationMode getResultAggregationMode(ResultType resultType) {
		return IntervalAggregationMode.AVERAGING;
	}
	
    public final static ResultType GAP_TIME = new GenericGaRoResultType("GAP_TIME", TimeResource.class, null) {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) { //never called
			return null;}
    };
    
    @Override
    public List<ResultType> resultTypes() {
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ResultType> result = new ArrayList<>((List) resultTypesGaRo());
    	result.add(GAP_TIME);
        return result;
    }
    
	@Override
	public void provideCurrentValues(String gwId, String roomId, long startTime, AbstractSuperMultiResult<GaRoMultiResult> superResult) {
		currentGwId = gwId;
		if(roomId == null)
			currentRoomId = GaRoMultiEvalDataProvider.BUILDING_OVERALL_ROOM_ID;
		else
			currentRoomId = roomId;
		currentStartTime = startTime;
		currentSuperResult = superResult;
	}
}
