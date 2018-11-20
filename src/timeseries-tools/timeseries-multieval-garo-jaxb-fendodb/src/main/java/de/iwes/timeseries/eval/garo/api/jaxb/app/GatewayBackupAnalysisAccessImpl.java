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
