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
package de.iwes.timeseries.eval.api;

import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Evaluation result for a specific result type and specific input data. 
 * Actual results must implement one of the subinterfaces specified here, such as
 * {@link SingleValueResult}, {@link ArrayResult}, or {@link TimeSeriesResult}.
 */
public interface SingleEvaluationResult {
	
//	/**
//	 * Reference to the result this belongs to
//	 * @return
//	 */
//	EvaluationResult result();
	
	ResultType getResultType();
	
	/**
	 * The time series this has been calculated for. Must contain exactly one value for
	 * result type PER_INPUT.
	 * @return
	 */
	List<TimeSeriesData> getInputData();
	
	public static interface SingleValueResult<T> extends SingleEvaluationResult {
		
		T getValue();
		
	}
	
	public static interface ArrayResult extends SingleEvaluationResult {
		
		List<SingleEvaluationResult> getValues();
		
		/**
		 * may either return null, or a list of the same size as {@link #getValues()}
		 * @param locale
		 * @return
		 */
		default List<String> getLabels(OgemaLocale locale) {
			return null;
		};
		
	}
	
	public static interface TimeSeriesResult extends SingleValueResult<ReadOnlyTimeSeries> {
	}
	
}