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
package org.ogema.timeseries.provider.windowquality;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.model.simple.IntegerResource;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI.Level;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult.RoomData;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProviderPreEval;
import de.iwes.timeseries.provider.genericcollection.WinHeatGenericEvalProvider;

/**
 * Evaluates number of window events per room for entire evaluation
 */
@Service(EvaluationProvider.class)
@Component
public class WindowSuperEval extends GenericGaRoSingleEvalProviderPreEval {
	public static final float MINIMUM_WINDOW_OPENINGS_PER_DAY = 1;
	
    public final static String ID = "basic-quality-room-windowsens";
    public final static String LABEL = "Basic Quality per Room: Window sensors";
    public final static String DESCRIPTION = "Estimates basic window sensor event relevance per room";
    
    public WindowSuperEval() {
        super(ID, LABEL, DESCRIPTION);
    }

	@Override
	public GaRoDataType[] getGaRoInputTypes() {
		return new GaRoDataType[] {
	    		GaRoDataType.WindowOpen};
	}
        
    private static final List<GenericGaRoResultType> RESULTS = Arrays.asList();
    
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return RESULTS;
	}
	
    public final static GenericGaRoResultType GOOD_ROOM_NUM_OVERALL = new GenericGaRoResultType("GOOD_ROOM_NUM_OVERALL",
    		"Number of rooms with at least one window opening per day in average", IntegerResource.class, Level.OVERALL, ID)  {
		@Override
		public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
				List<TimeSeriesData> inputData) {
			return null;
		}
    };

	//TODO: Versions here must declare OVERALL as Level !
	private static final List<GenericGaRoResultType> OVERALL_RESULTS = Arrays.asList(
	    		GOOD_ROOM_NUM_OVERALL);
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRoOverall() {
		return OVERALL_RESULTS;
	}

	
	@Override
	protected GenericGaRoEvaluationCore initEval(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			int size, int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		return null;
	}
	
	//We expect that we do not need special result format here

	@Override
	public List<PreEvaluationRequested> preEvaluationsRequested() {
		return Arrays.asList(new PreEvaluationRequested(WinHeatGenericEvalProvider.ID));
	}
	
	@Override
	public boolean executeSuperLevelOnly() {
		return true;
	}
	@Override
	protected void performSuperEval(AbstractSuperMultiResult<?> destination,
			List<AbstractSuperMultiResult<?>> preEvalSources) {
		//Implement evaluation here
		GaRoSuperEvalResult<?> winGen = (GaRoSuperEvalResult<?>) preEvalSources.get(0);
		GaRoSuperEvalResult<?> result = (GaRoSuperEvalResult<?>)destination;
		result.evalResults = new HashMap<String, String>();
		
		Map<String, Integer> countWindowOpening = new HashMap<>();
		int countDay = 0;
		int countWindowOpeningTotal = 0;
		for(GaRoMultiResult intervalData: winGen.intervalResults) {
			countDay++;
			for(RoomData room: intervalData.roomEvals) {
				if(room.evalResults == null) continue;
				String val = room.evalResults.get(WinHeatGenericEvalProvider.WINDOWPEN_DURATION_NUM.id());
				if(val == null) continue;
				try {
					int fval = Integer.parseInt(val);
					String fullRoomId = room.gwId+"_"+room.id;
					int sum = 0;
					if(countWindowOpening.containsKey(fullRoomId)) {
						sum = countWindowOpening.get(fullRoomId) + fval;
					} else {
						sum = fval;
					}
					countWindowOpening.put(fullRoomId, sum);
					countWindowOpeningTotal += fval;
				} catch(NumberFormatException e) {
					continue;
				}
			}
		}
		if(countDay == 0) {
			putResult("GOOD_ROOM_NUM_OVERALL", Float.NaN, result);
			System.out.println("NO DAYS COUNTED!!!");
			return;
		} else
			putResult("GOOD_ROOM_NUM_OVERALL", (float)countWindowOpeningTotal/countDay, result);
		float limit = MINIMUM_WINDOW_OPENINGS_PER_DAY*countDay;
		for(Entry<String, Integer> ct: countWindowOpening.entrySet()) {
			if(ct.getValue() >= limit)
				System.out.println("Room "+ct.getKey()+" has "+ct.getValue()+" window openings ("+((float)(ct.getValue())/countDay)+" per day)");
		}
	}
	
	void putResult(String id, float value, GaRoSuperEvalResult<?> result) {
		result.evalResults.put(id, String.format("%f", value));
	}
}
