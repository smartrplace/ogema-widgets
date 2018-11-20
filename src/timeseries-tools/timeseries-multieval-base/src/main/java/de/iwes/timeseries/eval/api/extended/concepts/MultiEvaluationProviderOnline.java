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
package de.iwes.timeseries.eval.api.extended.concepts;

import java.lang.reflect.Field;
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.MultiResult;

public interface MultiEvaluationProviderOnline<T extends MultiResult> extends MultiEvaluationProvider<T> {
	public interface MultiEvaluationOnlineListener {
		/**
		 * 
		 * @param outputIndex index from return value of {@link #onlinOutputFields}
		 * @param value the new value if applicable. If the field is not a numerical
		 *   type or an array, time series etc. of a numerical type, value is not
		 *   relevant and the value has to be obtained from the field directly
		 */
		void newValue(int outputIndex, float value, long timeStamp);
	}
	
	public interface MultiOnlineResultType extends ResultType {
		Field dataField();
	}
	
	/**Fields from T that are part of the listener with additional information
	 * like SingleEvaluation results in ResultType. The fields listed here are
	 * typically overall results as it is not possible to provide
	 * per-gateway or per-room evaluation here*/
	public List<MultiOnlineResultType> onlineOutputFields();
	
	public void registerListener(MultiEvaluationOnlineListener listener);
}
