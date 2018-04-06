package de.iwes.timeseries.eval.garo.resource;

import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

import org.ogema.core.model.Resource;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;

@SuppressWarnings("unchecked")
public class GenericGaRoMultiProviderResource<P extends GaRoSingleEvalProvider> extends GenericGaRoMultiProvider<Resource, P>{

	public GenericGaRoMultiProviderResource(P singleProvider, boolean doBasicEval) {
		super(singleProvider, doBasicEval);
	}

	@Override
	protected GenericGaRoMultiEvaluation<Resource, P> newGenericGaRoMultiEvaluation(List<MultiEvaluationInputGeneric<Resource>> input,
			Collection<ConfigurationInstance> configurations, GaRoEvalProvider<Resource, GaRoMultiResult<Resource>> dataProviderAccess,
			TemporalUnit resultStepSize, GaRoDataType[] inputTypesFromRoom, GaRoDataType[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		return new GenericGaRoMultiEvaluationResource<P>(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw,
				singleProvider, doBasicEval, resultsRequested);
	}

}
