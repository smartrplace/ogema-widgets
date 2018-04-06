package de.iwes.timeseries.eval.garo.multibase;

import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
//import de.iwes.timeseries.server.timeseries.source.ServerTimeseriesSource;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Calculate basic field test evaluations
 */
public abstract class GenericGaRoMultiProvider<R, P extends GaRoSingleEvalProvider> extends GaRoEvalProvider<R, GaRoMultiResult<R>> {
	private final P roomEval;
	public final boolean doBasicEval;
	
	public GenericGaRoMultiProvider(P singleProvider, boolean doBasicEval) {
		super(singleProvider.getGaRoInputTypes(), null);
		this.roomEval = singleProvider;
		this.doBasicEval = doBasicEval;
	}
	
	protected abstract GenericGaRoMultiEvaluation<R, P> newGenericGaRoMultiEvaluation(
			List<MultiEvaluationInputGeneric<R>> input, Collection<ConfigurationInstance> configurations,
			GaRoEvalProvider<R, GaRoMultiResult<R>> dataProviderAccess, TemporalUnit resultStepSize,
			GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested);

	@Override
	public String id() {
		return "Multi_"+roomEval.id();
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Multi-Eval for "+roomEval.label(locale);
	}

	@Override
	public String description(OgemaLocale locale) {
		return "Multi-eval for provider:"+roomEval.description(locale);
	}

	@Override
	public MultiEvaluationInstance<R, GaRoMultiResult<R>> createEvaluation(List<MultiEvaluationInputGeneric<R>> input,
			Collection<ConfigurationInstance> configurations, TemporalUnit resultStepSize,
			List<ResultType> resultsRequested) {
		final GenericGaRoMultiEvaluation<R, P> instance = newGenericGaRoMultiEvaluation(input, configurations, this, 
				resultStepSize, inputTypesFromRoom, inputTypesFromGw,
				roomEval, doBasicEval, resultsRequested);
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<GaRoMultiResultUntyped> resultType() {
		return GaRoMultiResultUntyped.class;
	}
}
