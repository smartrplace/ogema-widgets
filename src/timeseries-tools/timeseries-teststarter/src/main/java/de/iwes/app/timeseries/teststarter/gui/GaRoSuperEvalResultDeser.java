package de.iwes.app.timeseries.teststarter.gui;

import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;

public class GaRoSuperEvalResultDeser extends GaRoSuperEvalResult<GaRoMultiResultDeser> {
	//constructor for de-serialization
	public GaRoSuperEvalResultDeser() {
		super(null, 0, null);
	}

}
