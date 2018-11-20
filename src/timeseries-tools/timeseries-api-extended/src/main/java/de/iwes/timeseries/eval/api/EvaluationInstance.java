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
import java.util.Map;
import java.util.concurrent.Future;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;

/**
 * A time series evaluation instance. 
 * 
 * Note to implementers: implementations must inherit from the
 * base class {@link EvaluationBaseImpl}. 
 */
public interface EvaluationInstance extends Future<Status> {
	
	/**
	 * An id, specified by the evaluation provider
	 * @return
	 */
	String id();
	
	/**
	 * Contains the results of the evaulation, ordered by the requested result
	 * types.
	 * @throws IllegalStateException 
	 * 		if called before the evaluation is done, or the evaluation failed
	 * @return
	 */
	Map<ResultType, EvaluationResult> getResults() throws IllegalStateException;
	
	List<TimeSeriesData> getInputData();
	
	boolean isOnlineEvaluation();
	
	List<ResultType> getResultTypes();
	
	long getStartTime();
	
	/**
	 * The listener will be called when the evaluation is done, or 
	 * immediately, if it is already finished.
	 * @param listener
	 */
	void addListener(EvaluationListener listener);

	/**The listener will be called when an intermediate result is available or when such a result is
	 * calculated on termination of the evaluation
	 */
	void addIntermediateResultListener(ResultListener listener);
	public static interface ResultListener {
		void resultAvailable(ResultType type, SampledValue value);
	}
	
	/*
	 * Methods for internal use
	 */
	
	/**
	 * Method for internal use
	 */
	Status finish();

	/**
	 * Method for internal use
	 * @param dataPoint
	 * @throws IllegalStateException
	 * 		if evaluation is already finished
	 * @throws Exception
	 */
	void step(SampledValueDataPoint dataPoint) throws IllegalStateException, Exception;

	/**
	 * A listener that is informed about the evaluation being done.
	 */
	public static interface EvaluationListener {
		
		void evaluationDone(EvaluationInstance evaluation, Status status);
		
	}
	
}
