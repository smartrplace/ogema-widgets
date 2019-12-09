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
package de.iwes.timeseries.eval.garo.api.jaxb.app;

import java.util.List;

import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;

import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvalDataProvider;
import de.iwes.timeseries.eval.garo.api.jaxb.GenericGaRoMultiProviderJAXB;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;
import de.iwes.timeseries.eval.generic.gatewayBackupAnalysis.GatewayBackupAnalysisAccess;

public class GatewayBackupAnalysisAccessImpl implements GatewayBackupAnalysisAccess{
	private final GaRoMultiEvalDataProvider<?> dataProvider;
	private final GatewayBackupAnalysis gba;
	
	public GatewayBackupAnalysisAccessImpl(GaRoMultiEvalDataProvider<?> dataProvider, GatewayBackupAnalysis gba) {
		this.dataProvider = dataProvider;
		this.gba = gba;
	}

	@Override
	public GaRoMultiEvalDataProvider<?> getDataProvider() {
		return dataProvider;
	}

	@Override
	public <P extends GaRoSingleEvalProvider> GenericGaRoMultiProvider<P> getMultiEvalProvider(P singleProvider,
			boolean doBasicEval) {
		return new GenericGaRoMultiProviderJAXB<P>(singleProvider, doBasicEval);
	}

	@Override
	public List<String> getGatewayIds() {
		return gba.getGatewayIds();
	}

}
