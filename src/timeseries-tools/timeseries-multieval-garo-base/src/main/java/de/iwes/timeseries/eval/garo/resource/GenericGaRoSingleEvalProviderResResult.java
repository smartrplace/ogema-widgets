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
package de.iwes.timeseries.eval.garo.resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance.EvaluationListener;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoResultType;
import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoSingleEvalProvider;

/**
 * Generic GaRoSingleEvalProvider that allows to defined eval providers without
 * separate EvaluationInstance classes.
 */
public abstract class GenericGaRoSingleEvalProviderResResult<T extends Resource> extends GenericGaRoSingleEvalProvider {
	protected abstract Class<T> resultResType();
	protected abstract T getResultResource(String roomLocation, String roomName, String gwId);
	/** Currently this list just contains Room.class typically for GaRo. Still in the
	 * GaRo scheme also resources for relevant devices could be provided here. An abstract method
	 * choosing and collecting resources for each gateway and each room would have to be inserted
	 * into the GaRo evaluation logic to provide this. As a first step we just transfer the
	 * room.<br>
	 * This would only work if all input was collected from OGEMA resource. This is currently not
	 * possible as we have JAXB resources. So we have to provide references to resources via
	 * location and name Strings.*/
	//protected abstract List<Class<? extends Resource>> inputResTypes();
	
	@Override
	protected List<GenericGaRoResultType> resultTypesGaRo() {
		return BASE_RESULT_LIST;
	}
	@Override
	public GenericGaRoEvaluationCore initEval(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd) {
		throw new IllegalStateException("In ResEval use only extended initEval!");
	}
	protected abstract GenericGaRoSCoreResResult<T> initEval(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time, int size,
			int[] nrInput, int[] idxSumOfPrevious, long[] startEnd,
			T resultResource, String roomLocation, String roomName, String gwId);


	
	public GenericGaRoSingleEvalProviderResResult(String id, String label, String description) {
        super(id, label, description);
    }
    
    @Override
    protected OnlineEvaluation createEvaluation(List<EvaluationInput> input, List<ResultType> requestedResults, Collection<ConfigurationInstance> configurations) {
        long time = clock != null ? clock.getExecutionTime() : System.currentTimeMillis();
        return new GenericGaRoSingleEvaluationResResult<T>(input, requestedResults, configurations, this, time,
        		this);
//        		resultsOffered);
    }
        
    public final static GenericGaRoResultType BASE_RESULT = new GenericGaRoResultType("Dummy Result",
    		FloatResource.class, null) {
				@Override
				public SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore ec, ResultType rt,
						List<TimeSeriesData> inputData) {
					@SuppressWarnings("unchecked")
					GenericGaRoSCoreResResult<Resource> cec = (GenericGaRoSCoreResResult<Resource>)ec;
					cec.fillResultResource();
					return new SingleValueResultImpl<Float>(rt, (float) 0, inputData);
				}
    };
    private static final List<GenericGaRoResultType> BASE_RESULT_LIST = Arrays.asList(BASE_RESULT);

}
