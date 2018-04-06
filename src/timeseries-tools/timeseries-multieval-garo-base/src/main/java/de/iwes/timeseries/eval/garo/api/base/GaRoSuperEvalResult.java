package de.iwes.timeseries.eval.garo.api.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;

public class GaRoSuperEvalResult<R, T extends GaRoMultiResult<R>> extends AbstractSuperMultiResult<R, T> {

	public GaRoSuperEvalResult(List<MultiEvaluationInputGeneric<R>> inputData, long startTime,
			Collection<ConfigurationInstance> configurations) {
		super(inputData, startTime, configurations);
		intervalResults = new ArrayList<>();
	}

	//for now we do have any additional elements here besides super class elements
}
