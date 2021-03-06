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

public interface ResultType extends LabelledItem {
	
	public enum ResultStructure {
		
		/**
		 * One evaluation result per time series
		 */
		PER_INPUT,
		
		/**
		 * A single result for a combination of time series 
		 */
		COMBINED
		
	}
	
	public enum ValueType {
		
		NUMERIC,
		TIME_SERIES
		
	}
	
	/*public enum Cardinality {
		
		SINGLE_VALUE,
		ARRAY
		
	}*/
	
	ResultStructure resultStructure();
	ValueType valueType();
	//Cardinality cardinality();
	/**If true the result is a single value, otherwise it is an array*/
	Boolean isSingleValueOrArray();

}
