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
package de.iwes.timeseries.eval.api.extended.util;

import java.util.Collection;
import java.util.List;

import org.ogema.tools.resource.util.TimeUtils;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiResult;

public class AbstractMultiResult implements MultiResult {
	protected final List<MultiEvaluationInputGeneric> inputData;
	
	/**TODO: make elements private and provide/use getters*/
	public long startTime;
	
	/**End time may only be determinable when the evaluation ends, so this cannot be final*/
	public long endTime;
	public Long timeOfCalculation = null;
	public Collection<ConfigurationInstance> configurations;
	
	@Override
	public List<MultiEvaluationInputGeneric> getInputData() {
		return inputData;
	}

	public AbstractMultiResult(List<MultiEvaluationInputGeneric> inputData, long start, long end, Collection<ConfigurationInstance> configurations) {
		this.inputData = inputData;
		this.startTime = start;
		this.endTime = end;
		this.configurations = configurations;
	}
	
	public AbstractMultiResult(List<MultiEvaluationInputGeneric> inputData, long startTime, Collection<ConfigurationInstance> configurations) {
		this.inputData = inputData;
		this.startTime = startTime;
		this.configurations = configurations;
	}

	@Override
	public String getSummary() {
		return this.getClass().getSimpleName()+": start:"+TimeUtils.getDateAndTimeString(startTime)+
				" end:"+TimeUtils.getDateAndTimeString(endTime);
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public Collection<ConfigurationInstance> getConfigurations() {
		return configurations;
	}

	@Override
	public Long timeOfCalculation() {
		return timeOfCalculation;
	}

}
