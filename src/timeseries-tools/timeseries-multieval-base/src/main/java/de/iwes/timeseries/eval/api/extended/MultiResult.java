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

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;

/** Interface defines basic result information that shall be returned by every {@link MultiEvaluationProvider}.
 * The actual result values shall be provided by the class implementing MultiResultType defined
 * by each {@link MultiEvaluationProvider}.<br>
 * Note that all public fields are exported JSON/CSV by default.
  */
public interface MultiResult {
	public long getStartTime();
	public long getEndTime();
	/**not to be added to JSON file, usually added when data is read from file as the 
	 * file time. Default value is null meaning that no such information is available.
	 */
	public Long timeOfCalculation();
	
	/**Provide summary for printing into a log file or to console*/
	public String getSummary();

	/**Input to the evaluation that created result*/
	public Collection<ConfigurationInstance> getConfigurations();
	
	/**Input to the evaluation that created result*/
	public abstract List<MultiEvaluationInputGeneric> getInputData();
	
}
