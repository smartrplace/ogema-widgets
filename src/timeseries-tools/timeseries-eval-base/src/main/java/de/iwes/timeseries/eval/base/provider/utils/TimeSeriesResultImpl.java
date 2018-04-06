package de.iwes.timeseries.eval.base.provider.utils;

import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.TimeSeriesResult;

public class TimeSeriesResultImpl extends SingleValueResultImpl<ReadOnlyTimeSeries> implements TimeSeriesResult {
	
	public TimeSeriesResultImpl(ResultType resultType, ReadOnlyTimeSeries value, List<TimeSeriesData> inputData) {
		super(resultType, value, inputData);
	}

}
