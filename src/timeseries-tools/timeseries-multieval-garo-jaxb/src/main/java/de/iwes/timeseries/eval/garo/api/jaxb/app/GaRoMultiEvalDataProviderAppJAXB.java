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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.garo.api.jaxb.GaRoMultiEvalDataProviderJAXB;
import de.iwes.timeseries.eval.generic.gatewayBackupAnalysis.GatewayBackupAnalysisAccess;

@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class GaRoMultiEvalDataProviderAppJAXB implements Application {

	@Reference
	GatewayBackupAnalysis gatewayParser;
	
	private ApplicationManager appMan;
	private GaRoMultiEvalDataProviderJAXB dataProvider;
	private GatewayBackupAnalysisAccess gbaAcc;
	
	private BundleContext bc;
	@SuppressWarnings("rawtypes")
	protected ServiceRegistration<DataProvider> sr = null;
	protected ServiceRegistration<GatewayBackupAnalysisAccess> srgba = null;
	
	@Activate
    void activate(BundleContext bc) {
		this.bc = bc;
    }
	
	@Override
	public void start(ApplicationManager appManager) {
		this.appMan = appManager;
		
		this.dataProvider = new GaRoMultiEvalDataProviderJAXB(gatewayParser);
		this.gbaAcc = new GatewayBackupAnalysisAccessImpl(dataProvider, gatewayParser);
		
		sr = bc.registerService(DataProvider.class, dataProvider, null);
		srgba = bc.registerService(GatewayBackupAnalysisAccess.class, gbaAcc, null);
	}

	@Override
	public void stop(AppStopReason reason) {
		if(dataProvider != null) dataProvider.close();
		if (sr != null) {
			sr.unregister();
		}
		if (srgba != null) {
			srgba.unregister();
		}
	}

}
