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
package de.iwes.timeseries.eval.generic.gatewayBackupAnalysis;

import java.util.List;

import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;

/**Note that currently only a single instance of this interface should be registered as OSGi service
 * giving access to the data collected via remote supervision.
 * TODO: Concepts are under development to support also multiple data providers in this domain.
 */
@Deprecated //Should not be required anymore
public interface GatewayBackupAnalysisAccess {
	GaRoMultiEvalDataProvider<?> getDataProvider();
	<P extends GaRoSingleEvalProvider> GenericGaRoMultiProvider<P> getMultiEvalProvider(P singleProvider, boolean doBasicEval);
	List<String> getGatewayIds();
}
