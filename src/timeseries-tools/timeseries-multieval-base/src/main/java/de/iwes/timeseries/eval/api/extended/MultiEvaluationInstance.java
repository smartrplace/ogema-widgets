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
package de.iwes.timeseries.eval.api.extended;

import java.util.List;
import java.util.concurrent.Future;

import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;

/**
 * A time series evaluation instance. 
 * 
 * Note to implementers: implementations must inherit from the
 * base class {@link EvaluationBaseImpl}. 
 */
public interface MultiEvaluationInstance<T extends MultiResult> extends Future<Status> {
	
	/**
	 * An id, specified by the evaluation provider
	 * @return
	 */
	String id();
	
	/**
	 * Contains the results of the evaulation
	 * @throws IllegalStateException 
	 * 		if called before the evaluation is done, or the evaluation failed
	 * @return
	 */
	AbstractSuperMultiResult<T> getResult() throws IllegalStateException;
	
	List<MultiEvaluationInputGeneric> getInputData();
	
	boolean isOnlineEvaluation();
	
	<X extends MultiResult> Class<X> getResultType();
	<S extends AbstractSuperMultiResult<T>> Class<S> getSuperResultType();
	
	/**
	 * The listener will be called when the evaluation is done, or 
	 * immediately, if it is already finished.
	 * @param listener
	 */
	void addListener(MultiEvaluationListener<T> listener);
	
	/**When this is called no further evaluation steps are started and the current results are
	 * saved to file (if saving is configured)
	 */
	void stopExecution();
	
	/*
	 * Methods for internal use
	 */
	
	/**
	 * Method for internal use
	 */
	Status finish();

	/**
	 * A listener that is informed about the evaluation being done.
	 */
	public static interface MultiEvaluationListener<T extends MultiResult> {
		
		void evaluationDone(MultiEvaluationInstance<?> evaluation, Status status);
		
	}

	public Status execute();
	
}
