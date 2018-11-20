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

import org.ogema.core.model.simple.SingleValueResource;

public interface RequiredInputData extends LabelledItem {

	public int cardinalityMin();
	public int cardinalityMax();
	
	/**
	 * Define what kind of input data is required. E.g.
	 * <ul>
	 *  <li>SingleValueResource.class for arbitrary input data
	 *  <li>TemperatureResource.class for temperature values
	 *  <li>BooleanResource.class for boolean values, etc.
	 * </ul>
	 * @return
	 */
	public Class<? extends SingleValueResource> requestedInputType();
	
}
