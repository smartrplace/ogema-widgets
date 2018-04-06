package de.iwes.timeseries.eval.base.provider.utils;

import java.util.List;
import java.util.Objects;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;

public class SingleValueResultImpl<T> implements SingleValueResult<T> {
	
	private final List<TimeSeriesData> inputData;
	private final ResultType resultType;
	private final T value;
	
	public SingleValueResultImpl(ResultType resultType, T value, List<TimeSeriesData> inputData) {
		this.resultType = Objects.requireNonNull(resultType);
		this.value = Objects.requireNonNull(value);
		this.inputData = Objects.requireNonNull(inputData);
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public List<TimeSeriesData> getInputData() {
		return inputData;
	}
	
	@Override
	public ResultType getResultType() {
		return resultType;
	}

}
