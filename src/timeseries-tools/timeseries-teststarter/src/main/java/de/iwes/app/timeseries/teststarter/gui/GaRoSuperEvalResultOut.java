package de.iwes.app.timeseries.teststarter.gui;

import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.provider.outsideTemperature.OutsideTemperatureMultiResult;

public class GaRoSuperEvalResultOut extends GaRoSuperEvalResult<OutsideTemperatureMultiResult> {
	//constructor for de-serialization
	public GaRoSuperEvalResultOut() {
		super(null, 0, null);
	}

}
