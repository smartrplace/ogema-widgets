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

import java.util.List;
import java.util.Objects;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;

public class SingleValueResultImpl<T> implements SingleValueResult<T> {
	
	private final List<TimeSeriesData> inputData;
	private final ResultType resultType;
	private final T value;
	
	public SingleValueResultImpl(ResultType resultType, T value, List<TimeSeriesData> inputData) {
		this.resultType = Objects.requireNonNull(resultType);
		this.value = Objects.requireNonNull(value);
		this.inputData = Objects.requireNonNull(inputData);
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public List<TimeSeriesData> getInputData() {
		return inputData;
	}
	
	@Override
	public ResultType getResultType() {
		return resultType;
	}

}
