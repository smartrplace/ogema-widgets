package de.iwes.timeseries.eval.online.utils;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.extended.util.SpecificEvalBaseImpl;

/**Aggregates the input series of a certain requested input. Usually it provides the average of all
 * values, but can also provide min or max
 */
public class InputSeriesAggregator {
	public enum AggregationMode {
		/**Standard Mode: average over all input time series*/
		AVERAGING,
		/**Use the integral of all input series. This could be used if several energy meter inputs would have
		 * to be summed up
		 */
		INTEGRATING,
		/**Use the minimum value. For Boolean input this can replay an AND operation*/
		MIN,
		/**Use the maximum value. For Boolean input this can replay an OR operation*/
		MAX
	}
	/**indeces from respective {@link SpecificEvalBaseImpl}
	 */
	protected final int nrInput;
    protected final int idxSumOfPrevious;
    protected final InterpolationMode mode;
    protected final AggregationMode aggregationMode;
    protected final long endTime;
    protected final float[] currentValues;
    private final long[] lastNextTimeStamps;
    //protected long previousDuration = -1;
	
    public InputSeriesAggregator(int nrInput, int idxSumOfPrevious, long endTime) {
    	this(nrInput, idxSumOfPrevious, endTime, null, AggregationMode.AVERAGING);
    }
    public InputSeriesAggregator(int nrInput, int idxSumOfPrevious, long endTime, InterpolationMode interpolationMode,
    		AggregationMode aggregationMode) {
		this.nrInput = nrInput;
		this.idxSumOfPrevious = idxSumOfPrevious;
		this.mode = interpolationMode;
		this.aggregationMode = aggregationMode;
		this.endTime = endTime;
		this.currentValues = new float[nrInput];
		for(int i=0; i<nrInput; i++) currentValues[i] = Float.NaN;
		this.lastNextTimeStamps = new long[nrInput];
		for(int i=0; i<nrInput; i++) lastNextTimeStamps[i] = -1;
	}
    public InputSeriesAggregator(int[] nrInput, int[] idxSumOfPrevious, int inputIdx, long endTime) {
    	this(nrInput, idxSumOfPrevious, inputIdx, endTime, null, AggregationMode.AVERAGING);
    }
    public InputSeriesAggregator(int[] nrInput, int[] idxSumOfPrevious, int inputIdx, long endTime,
    		InterpolationMode interpolationMode, AggregationMode aggregationMode) {
		this(nrInput[inputIdx], idxSumOfPrevious[inputIdx], endTime, interpolationMode, aggregationMode);
	}

    /**Usually not usable*/
    @Deprecated
    public float getCurrentValue(SampledValue sv, SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
    	if(nrInput == 1) return sv.getValue().getFloatValue();
    	return getCurrentValue(dataPoint, ignoreMissingPoints);
    }
    
    /**Use this to get value for secondary time series*/
    @Deprecated
    public float getCurrentValue(SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
    	float sum = initValue();
    	for(int i=0; i<nrInput; i++) {
			SampledValue svl;
			if(mode == null)
				svl = dataPoint.getElement(getTotalInputIdx(i));
			else
				svl = dataPoint.getElement(getTotalInputIdx(i), mode);
			if (svl == null || svl.getQuality() == Quality.BAD) {
				if (!ignoreMissingPoints)
					return Float.NaN;
				continue;
			}
			sum = processSingleInputValue(sum, svl.getValue().getFloatValue());
     	}
    	if(aggregationMode == AggregationMode.AVERAGING)
    		return sum / nrInput;
    	else return sum;
    }

    @Deprecated
    public float getCurrentValue(int idxOfEvaluationInput, SampledValue sv, SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
    	if(sv.getQuality() == Quality.BAD && (!ignoreMissingPoints)) currentValues[idxOfEvaluationInput] = Float.NaN;
    	else currentValues[idxOfEvaluationInput] = sv.getValue().getFloatValue();
    	float sum = initValue();
    	int count = 0;
    	for(int i=0; i<nrInput; i++) {
			if (Float.isNaN(currentValues[i])) {
				if (!ignoreMissingPoints)
					return Float.NaN;
				continue;
			}
			sum = processSingleInputValue(sum, currentValues[i]);
			count++;
     	}
		if(count == 0) return Float.NaN; 
    	if(aggregationMode == AggregationMode.AVERAGING)
    		return sum / count;
    	else return sum;
    }

	/** If two input values have the same time stamp the duration for the first one will be zero. So you could
	 * check for a duration zero and not perform further calculations if this has no effect anyways

	 */
   public class ValueDuration {
    	public float value;
    	public long duration;
    	public long nextTimeStamp;
    }
    /** Use this usually*/
    public ValueDuration getCurrentValueDuration(int idxOfEvaluationInput, SampledValue sv, SampledValueDataPoint dataPoint, boolean ignoreMissingPoints) {
    	ValueDuration result = new ValueDuration();

    	if(nrInput == 1) {
    		result.value = sv.getValue().getFloatValue();
    		int totIdx = getTotalInputIdx(0);
			SampledValue svNext = dataPoint.getNextElement(totIdx);
			long now = sv.getTimestamp();
	    	if(svNext == null)
				result.nextTimeStamp = getNextTimeStamp(now, Long.MAX_VALUE);
	    	else
	    		result.nextTimeStamp = getNextTimeStamp(now, svNext.getTimestamp());
	    	result.duration = result.nextTimeStamp - now;
        	return result;
    	}
    	
    	long now = sv.getTimestamp();
    	result.nextTimeStamp = Long.MAX_VALUE;
    	if(sv.getQuality() == Quality.BAD && (!ignoreMissingPoints)) currentValues[idxOfEvaluationInput] = Float.NaN;
    	else currentValues[idxOfEvaluationInput] = sv.getValue().getFloatValue();
    	float sum = initValue();
    	int count = 0;
    	for(int i=0; i<nrInput; i++) {
			if(i == idxOfEvaluationInput) {
				int totIdx = getTotalInputIdx(i);
				SampledValue svNext = dataPoint.getNextElement(totIdx);
				if(svNext != null) {
					long nextTimeLoc = svNext.getTimestamp();
					lastNextTimeStamps[i] = getNextTimeStamp(now, nextTimeLoc);
				} else lastNextTimeStamps[i] = getNextTimeStamp(now, Long.MAX_VALUE);
			} else {
				if(lastNextTimeStamps[i] <= 0) {
					int totIdx = getTotalInputIdx(i);
					SampledValue svNext = dataPoint.getNextElement(totIdx);
					if(svNext != null) {
						long nextTimeLoc = svNext.getTimestamp();
						lastNextTimeStamps[i] = getNextTimeStamp(now, nextTimeLoc);
					}
				}
			}
			if (Float.isNaN(currentValues[i])) {
				if (!ignoreMissingPoints) {
					result.value = Float.NaN;
					break;
				}
				continue;
			}
			if((lastNextTimeStamps[i] > 0) && (lastNextTimeStamps[i] < result.nextTimeStamp))
				result.nextTimeStamp = lastNextTimeStamps[i];
			
			sum = processSingleInputValue(sum, currentValues[i]);
			count++;
     	}
		if(count == 0) {
			result.value = Float.NaN; 
		} else 	if(aggregationMode == AggregationMode.AVERAGING) {
    		result.value = sum / count;
    	} else result.value = sum;

    	//result.value = getCurrentValue(idxOfEvaluationInput, sv, dataPoint, ignoreMissingPoints);
    	
    	//result.nextTimeStamp = getNextTimeStamp(now, nextTime);
    	result.duration = result.nextTimeStamp - now;
    	
    	return result;

    	/*float sum = initValue();
    	for(int i=0; i<nrInput; i++) {
    		int totIdx = getTotalInputIdx(i);
			SampledValue svNext = dataPoint.getNextElement(totIdx);
			if((svNext != null) && (svNext.getTimestamp() < nextTime))
					nextTime = svNext.getTimestamp();
			SampledValue svl;
			if(mode == null)
				svl = dataPoint.getElement(totIdx);
			else
				svl = dataPoint.getElement(totIdx, mode);
			if (svl == null || svl.getQuality() == Quality.BAD) {
				if (!ignoreMissingPoints)
					result.value = Float.NaN;
				continue;
			}
			sum = processSingleInputValue(sum, svl.getValue().getFloatValue());
     	}
    	if(Float.isNaN(result.value)) return result;
    	result.value = sum / nrInput; */  	
    }
    
    long getNextTimeStamp(long currentTime, long nextTimeStepIdentified) {
    	if(nextTimeStepIdentified > endTime) {
    		return endTime;
    	} else if(nextTimeStepIdentified == Long.MAX_VALUE) {
    		//no more values found
    		return endTime;
    	} else if(nextTimeStepIdentified < currentTime) {
    		System.out.println("TODO: Interval boundaries interchanged (2) !!");
    		return currentTime; //duration zero
    		//return Long.MIN_VALUE;
     		//throw new IllegalStateException("next time stamp is before current time, iterator or evaluation "
     		//		+ "did not give to the Aggregator!");
    		//if(previousDuration > 0) {
    		//	result = previousDuration;
    		//}
    	} else {
    		return nextTimeStepIdentified;
    		//previousDuration = result;
    	}
    }

	public int getTotalInputIdx(int idxOfEvaluationInput) {
    	return idxSumOfPrevious + idxOfEvaluationInput;
    }
	
	private float initValue() {
		return initValue(aggregationMode);
	}
    public static float initValue(AggregationMode aggregationMode) {
    	switch(aggregationMode) {
    	case AVERAGING:
    		return 0;
    	case INTEGRATING:
    		return 0;
    	case MIN:
    		return Float.MAX_VALUE;
    	case MAX:
    		return -Float.MAX_VALUE;
    	default:
    		throw new IllegalStateException("unknown enum option");
    	}
    }
    private float processSingleInputValue(float currentValue, float newValue) {
    	return processSingleInputValue(currentValue, newValue, aggregationMode);
    }
    public static float processSingleInputValue(float aggregationValue, float newValue, AggregationMode aggregationMode) {
    	switch(aggregationMode) {
    	case AVERAGING:
    		return aggregationValue + newValue;
    	case INTEGRATING:
    		return aggregationValue + newValue;
    	case MIN:
    		if(newValue < aggregationValue) return newValue;
    		else return aggregationValue;
    	case MAX:
    		if(newValue > aggregationValue) return newValue;
    		else return aggregationValue;
    	default:
    		throw new IllegalStateException("unknown enum option");
    	}
    }
    public static float getFinalValue(float aggregationValue, int count, AggregationMode aggregationMode) {
    	if(count == 0) {
			return Float.NaN; 
		} else 	if(aggregationMode == AggregationMode.AVERAGING) {
    		return aggregationValue / count;
    	} else return aggregationValue;   	
    }
}
