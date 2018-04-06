package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;

public class GenericGaRoSingleEvaluation extends SpecificEvalBaseImpl<GenericGaRoSingleEvalValueContainer> {
	public static final long MAX_DATA_INTERVAL = 3600*1000;
	
	private List<ResultListener> interMediateListeners = new ArrayList<>();
	private final GenericGaRoEvaluationCore evalCore;
	
	@Override
	protected GenericGaRoSingleEvalValueContainer initValueContainer(List<EvaluationInput> input) {
		List<GenericGaRoResultType> requestedResultsGaRo = new ArrayList<>();
		for(ResultType r: requestedResults) {
			requestedResultsGaRo.add((GenericGaRoResultType) r);
		}
		//requestedResultsGaRo.addAll((Collection<? extends GenericGaRoResultType>) requestedResults);
		return new GenericGaRoSingleEvalValueContainer(size, requestedResultsGaRo , requestedResults, input);
	}

	public GenericGaRoSingleEvaluation(final List<EvaluationInput> input, final List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			GenericGaRoSingleEvalProvider provider) {
		super(input, requestedResults, configurations, listener, time,
				provider.id(), provider.inputDataTypes().size()); //"RoomBaseEvaluation", 3);
		this.evalCore = provider.initEval(input, requestedResults, configurations, listener, time, size,
				nrInput, idxSumOfPrevious, startEnd);
		evalCore.evalInstance = this;
		values.setEvalContainer(evalCore);
	}

	@Override
	protected long maximumGapTimeAccepted(int idxOfRequestedInput) {
		return MAX_DATA_INTERVAL;
	}
	
	@Override
	protected void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
			int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		evalCore.processValue(idxOfRequestedInput, idxOfEvaluationInput, totalInputIdx,
				timeStamp, sv, dataPoint, duration);
	}
	
	@Override
	protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput, int totalInputIdx, long timeStamp,
			SampledValue sv, SampledValueDataPoint dataPoint, long duration) {
		evalCore.gapNotification(idxOfRequestedInput, idxOfEvaluationInput, totalInputIdx, timeStamp, sv, dataPoint, duration);
	}

	@Override
	public void addIntermediateResultListener(ResultListener listener) {
		interMediateListeners.add(listener);
	}
	
	public void callListeners(ResultType type, long timeStamp, float value) {
		SampledValue sv = new SampledValue(new FloatValue(value), timeStamp, Quality.GOOD);
		for(ResultListener listen: interMediateListeners) {
			listen.resultAvailable(type, sv);
		}
	}

	public boolean isRequested(GenericGaRoResultType resultType) {
		return requestedResults.contains(resultType);
	}
}
