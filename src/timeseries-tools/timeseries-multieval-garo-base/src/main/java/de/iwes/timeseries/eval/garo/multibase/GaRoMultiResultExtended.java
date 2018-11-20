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
package de.iwes.timeseries.eval.garo.multibase;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;

public abstract class GaRoMultiResultExtended extends GaRoMultiResult {

	/** Note that all inherited classes need to have this constructor signature!
	 */
	public GaRoMultiResultExtended(List<MultiEvaluationInputGeneric> inputData, long start, long end,
			Collection<ConfigurationInstance> configurations) {
		super(inputData, start, end, configurations);
	}
	
	/**Only to be used by JSON Deserialization!*/
	public GaRoMultiResultExtended() {
		this(null, 0, 0, null);
	}
	
	public abstract void finishRoom(GaRoMultiResultExtended resultExtended, String roomId);
	public abstract void finishGateway(GaRoMultiResultExtended result, String gw);
	public abstract void finishTimeStep(GaRoMultiResultExtended result);
	public abstract void finishTotal(GaRoSuperEvalResult<?> result);
}
