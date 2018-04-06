package de.iwes.timeseries.eval.api.extended.util;

import java.util.Collection;
import java.util.List;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiResult;

public class AbstractSuperMultiResult<R, T extends MultiResult<R>> extends AbstractMultiResult<R> {
	public AbstractSuperMultiResult(List<MultiEvaluationInputGeneric<R>> inputData, long startTime, Collection<ConfigurationInstance> configurations) {
		super(inputData, startTime, configurations);
	}

	public List<T> intervalResults;
}
