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
package de.iwes.timeseries.eval.resulthelper.deprecated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationResultImpl;
import de.iwes.timeseries.eval.base.provider.utils.SingleValueResultImpl;

/**Generation of result structure for evaluation with multiple inputs, but
 * only single outputs for each result
 */
public class MultiResultManagement {
	List<SingleResultManagement> resultsOffered = new ArrayList<>();
	final List<TimeSeriesData> inputData = new ArrayList<>();
	
	public void addResultRequested(ResultType resultType) {
		for(SingleResultManagement offer: resultsOffered) {
			if(offer.resultType.id().equals(resultType.id())) {
				offer.isRequested = true;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setInput(List<TimeSeriesData>... input) {
		for(List<TimeSeriesData> in: input) {
			inputData.addAll(in);
		}
	}
	
	/** You have to report input data first*/
	public Map<ResultType, EvaluationResult> getCurrentResults() {
		final Map<ResultType, EvaluationResult> results = new LinkedHashMap<>();

		for(SingleResultManagement singleRes: resultsOffered) {
			synchronized (this) {
				final boolean max = singleRes.isRequested;	        	
				final List<SingleEvaluationResult> maxResults = max ? new ArrayList<SingleEvaluationResult>() : null;
				if (max) {
					Object maxV = singleRes.resultProvider.getCurrentResult();
					if (maxV != null) {
//						final SingleEvaluationResult maxResult = new SingleValueResultImplFlex(singleRes.resultType, maxV, inputData);
						final SingleEvaluationResult maxResult = new SingleValueResultImpl<Object>(singleRes.resultType, maxV, inputData);
						maxResults.add(maxResult);
					}
				}
				if (max) {
					results.put(singleRes.resultType, new EvaluationResultImpl(maxResults, singleRes.resultType));
				}
			}
		}

		return Collections.unmodifiableMap(results);
	}
	
}
