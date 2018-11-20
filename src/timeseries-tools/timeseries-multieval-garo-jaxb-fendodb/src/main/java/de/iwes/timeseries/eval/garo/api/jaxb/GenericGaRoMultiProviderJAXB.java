package de.iwes.timeseries.eval.garo.api.jaxb;

import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;
import de.iwes.timeseries.eval.garo.api.base.GaRoEvalProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiResult;
import de.iwes.timeseries.eval.garo.multibase.GaRoSingleEvalProvider;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiEvaluation;
import de.iwes.timeseries.eval.garo.multibase.GenericGaRoMultiProvider;

@SuppressWarnings("unchecked")
public class GenericGaRoMultiProviderJAXB<P extends GaRoSingleEvalProvider> extends GenericGaRoMultiProvider<P>{

	public GenericGaRoMultiProviderJAXB(P singleProvider, boolean doBasicEval) {
		super(singleProvider, doBasicEval);
	}

	@Override
	protected GenericGaRoMultiEvaluation<P> newGenericGaRoMultiEvaluation(List<MultiEvaluationInputGeneric> input,
			Collection<ConfigurationInstance> configurations, GaRoEvalProvider<GaRoMultiResult> dataProviderAccess,
			TemporalUnit resultStepSize, GaRoDataTypeI[] inputTypesFromRoom, GaRoDataTypeI[] inputTypesFromGw,
			P singleProvider, boolean doBasicEval, List<ResultType> resultsRequested) {
		return new GenericGaRoMultiEvaluationJAXB<P>(input, configurations, dataProviderAccess, resultStepSize, inputTypesFromRoom, inputTypesFromGw, singleProvider, doBasicEval,
				resultsRequested);
	}
}
