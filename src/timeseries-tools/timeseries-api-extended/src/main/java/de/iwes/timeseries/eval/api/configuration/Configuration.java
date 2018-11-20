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
package de.iwes.timeseries.eval.api.configuration;

import java.util.Collection;

import de.iwes.timeseries.eval.api.LabelledItem;
import de.iwes.timeseries.eval.api.ResultType;

/**
 * A configuration type that can be supported by an evaluation provider. The configuration values
 * for specific evaluations are provided by {@link ConfigurationInstance}s. 
 * 
 * @param <C>
 */
public interface Configuration<C extends ConfigurationInstance> extends LabelledItem {

	Class<C> configurationType();
	
	/**
	 * Check if the passed instance is allowed. Otherwise an exception
	 * shall be thrown.
	 * @param instance
	 * @throws IllegalArgumentException
	 */
	void filter(C instance) throws IllegalArgumentException;
	
	/**
	 * 
	 * @return
	 * 		null to indicate that this applies to all result types
	 */
	Collection<ResultType> getApplicableResultTypes();
	
	/**
	 * @return
	 * 		null to indicate that a default value does not exist
	 */
	C defaultValues();
	
	boolean isOptional();
	
}
