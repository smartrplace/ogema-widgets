package de.iwes.timeseries.eval.online.utils;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

public abstract class InputBooleanValueCleaner extends InputSeriesAggregator {
	protected final long minOnTime;
	protected abstract void processValue(ValueDuration val, int idxOfEvaluationInput, SampledValue sv, SampledValueDataPoint dataPoint);
	
    public InputBooleanValueCleaner(int nrInput, int idxSumOfPrevious, long endTime, long minOnTime) {
    	this(nrInput, idxSumOfPrevious, endTime, null, AggregationMode.AVERAGING, minOnTime);
    }
    public InputBooleanValueCleaner(int nrInput, int idxSumOfPrevious, long endTime, InterpolationMode interpolationMode,
    		AggregationMode aggregationMode, long minOnTime) {
    	super(nrInput, idxSumOfPrevious, endTime, interpolationMode, AggregationMode.AVERAGING);
       	this.minOnTime = minOnTime;
	}
    public InputBooleanValueCleaner(int[] nrInput, int[] idxSumOfPrevious, int inputIdx, long endTime, long minOnTime) {
    	this(nrInput, idxSumOfPrevious, inputIdx, endTime, null, AggregationMode.AVERAGING, minOnTime);
     }
    public InputBooleanValueCleaner(int[] nrInput, int[] idxSumOfPrevious, int inputIdx, long endTime,
    		InterpolationMode interpolationMode, AggregationMode aggregationMode, long minOnTime) {
		this(nrInput[inputIdx], idxSumOfPrevious[inputIdx], endTime, interpolationMode, aggregationMode, minOnTime);
	}
	
	boolean isPresent = false;
	long presenceConfirmedTime = 0;
	class InputCallData {
		public InputCallData(ValueDuration val, int idxOfEvaluationInput, SampledValue sv,
				SampledValueDataPoint dataPoint) {
			this.val = val;
			this.idxOfEvaluationInput = idxOfEvaluationInput;
			this.sv = sv;
			this.dataPoint = dataPoint;
		}
		ValueDuration val;
		int idxOfEvaluationInput;
		SampledValue sv;
		SampledValueDataPoint dataPoint;
	}
	InputCallData firstHiddenOff = null;

	@Override
    public ValueDuration getCurrentValueDuration(int idxOfEvaluationInput, SampledValue sv,
    		SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
		throw new IllegalStateException("Use only newValue and implement processValue here!");
	}
    public void newValue(int idxOfEvaluationInput, SampledValue sv,
	    		SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
    	ValueDuration currentVal = super.getCurrentValueDuration(idxOfEvaluationInput, sv, dataPoint, ignoreMissingPoints);

		boolean bval = currentVal.value > 0.5;
		if(isPresent && (firstHiddenOff != null) && ((presenceConfirmedTime + minOnTime) <= sv.getTimestamp())) {
			processValue(firstHiddenOff.val, firstHiddenOff.idxOfEvaluationInput, firstHiddenOff.sv,
					firstHiddenOff.dataPoint);
			firstHiddenOff = null;
			isPresent = false;
		}
		if(bval) {
			presenceConfirmedTime = sv.getTimestamp();
		}
		if(bval == isPresent) return;
		if(isPresent &&
				((presenceConfirmedTime + minOnTime) > sv.getTimestamp())) {
			if(firstHiddenOff == null) firstHiddenOff = new InputCallData(currentVal, idxOfEvaluationInput, sv, dataPoint);
			return;
		}
		isPresent = bval;
		processValue(currentVal, idxOfEvaluationInput, sv, dataPoint);
    
    }
}
