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
package de.iwes.timeseries.eval.base.provider.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;

public class EvaluationResultImpl implements EvaluationResult {
	
	private final List<SingleEvaluationResult> results;
	private final ResultType resultType;
//	private final List<TimeSeriesData> input;
	
	public EvaluationResultImpl(List<SingleEvaluationResult> results, ResultType resultType) {
		this.results = Collections.unmodifiableList(new ArrayList<>(results));
		this.resultType = resultType;
//		this.input = Collections.unmodifiableList(new ArrayList<>(input));
	}

	@Override
	public List<SingleEvaluationResult> getResults() {
		return results;
	}

	@Override
	public ResultType getResultType() {
		return resultType;
	}

}
