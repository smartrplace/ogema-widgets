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
