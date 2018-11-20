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

import org.ogema.core.model.Resource;

import de.iwes.timeseries.eval.garo.multibase.generic.GenericGaRoEvaluationCore;

public abstract class GenericGaRoSCoreResResult<T extends Resource> extends GenericGaRoEvaluationCore {
	protected T resultResource;
	
	/** @return result resource of evaluation*/
	protected abstract void fillResultResource();

	public GenericGaRoSCoreResResult(T result) {
		this.resultResource = result;
	}
	
	/*@Override
	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput, int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		throw new IllegalStateException("Use other version of processValue!");
		
	}*/
	
	/*T result = null;
	T getResult(T resultResource) {
		if(result == null) {
			finishEvaluation(resultResource);
		}
		return result;
	}*/
}
