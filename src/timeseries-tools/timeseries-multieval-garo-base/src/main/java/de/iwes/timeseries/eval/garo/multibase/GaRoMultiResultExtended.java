package de.iwes.timeseries.eval.garo.multibase;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;

public abstract class GaRoMultiResultExtended<R> extends GaRoMultiResult<R> {

	/** Note that all inherited classes need to have this constructor signature!
	 */
	public GaRoMultiResultExtended(List<MultiEvaluationInputGeneric<R>> inputData, long start, long end,
			Collection<ConfigurationInstance> configurations) {
		super(inputData, start, end, configurations);
	}
	
	public abstract void finishRoom(GaRoMultiResultExtended<R> resultExtended, R room);
	public abstract void finishGateway(GaRoMultiResultExtended<R> result, String gw);
	public abstract void finishTimeStep(GaRoMultiResultExtended<R> result);
	public abstract void finishTotal(GaRoSuperEvalResult<R, ?> result);
}
