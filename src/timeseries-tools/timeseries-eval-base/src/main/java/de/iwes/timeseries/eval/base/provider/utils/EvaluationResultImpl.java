package de.iwes.timeseries.eval.base.provider.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;

public class EvaluationResultImpl implements EvaluationResult {
	
	private final List<SingleEvaluationResult> results;
	private final ResultType resultType;
//	private final List<TimeSeriesData> input;
	
	public EvaluationResultImpl(List<SingleEvaluationResult> results, ResultType resultType) {
		this.results = Collections.unmodifiableList(new ArrayList<>(results));
		this.resultType = resultType;
//		this.input = Collections.unmodifiableList(new ArrayList<>(input));
	}

	@Override
	public List<SingleEvaluationResult> getResults() {
		return results;
	}

	@Override
	public ResultType getResultType() {
		return resultType;
	}

}
