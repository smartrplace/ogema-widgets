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
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;

/**To be added to EvalProvider*/
public class MultiResultsOffered {
	List<ResultType> resultsOffered = new ArrayList<>();
	
	public MultiResultsOffered(List<ResultType> resultTypes) {
		for(ResultType rt: resultTypes) addResult(rt);
	}

	public void addResult(ResultType resultType) {
		resultsOffered.add(resultType);
	}
	
	public MultiResultManagement getEvaluationInstanceResultMgmt(ResultProvider...providers) {
		if(resultsOffered.size() != providers.length)
			throw new IllegalStateException("Number of ResultProviders must match number of results offered!");
		MultiResultManagement result = new MultiResultManagement();
		int i = 0;
		for(ResultType rt: resultsOffered) {
			result.resultsOffered.add(new SingleResultManagement(rt, providers[i]));
			i++;
		}
		return result ;
	}

}
