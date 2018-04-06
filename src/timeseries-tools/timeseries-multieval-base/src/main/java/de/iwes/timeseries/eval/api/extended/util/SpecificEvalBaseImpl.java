package de.iwes.timeseries.eval.api.extended.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.util.SpecificEvalValueContainer.EvalDataPointInfo;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationBaseImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;

/** Base class for specific base evaluations requesting a fixed number of input types
 *
 */
public abstract class SpecificEvalBaseImpl<T extends SpecificEvalValueContainer> extends EvaluationBaseImpl {

    protected final static AtomicLong idcounter = new AtomicLong(0); // TODO initialize from existing stored eval resources
    protected final String id;
    protected final int[] nrInput;
    protected final int[] idxSumOfPrevious;
    protected final long[] startEnd;

    //state variables
    protected final T values;
    /** Provide value container, will be called from super-constructor*/
    protected abstract T initValueContainer(List<EvaluationInput> input);
    /** Process new value
     * 
     * @param idxOfRequestedInput index of requested input
     * @param idxOfEvaluationInput index of time series within the requested input
     * @param totalInputIdx required for some access methods of EvaluationBaseImpl
     * @param timeStamp time of the current value
     * @param sv current SampledValue
     * @param dataPoint access to the input data structure
     * @param duration duration of the current value based on the assumption of InterpolationMode.STEPS.
     * 		See {@link #gapNotification()} for details.
     */
    protected abstract void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx,
    		long timeStamp, SampledValue sv, SampledValueDataPoint dataPoint, long duration);
    /** If the maximum gap time for the respective input is exceeded, the time series is considered in
     * gap and the respective value is considered not available. This may highly depend on the input type.
     * For temperature sensors usually a certain rate of measurement data can be expected whereas a 
     * window sensor may only send data when the state is changed, so no gap detection based on the
     * interval between two values can be performed. This is also the standard value if the method is
     * not overwritten.
     */
    protected long maximumGapTimeAccepted(int idxOfRequestedInput) {
    	return Long.MAX_VALUE;
    }
    
    /** The concept of gap notification is based on the concept that the incoming values are treated as
     * InterpolationMode.STEPS. Although for some input data LINEAR may be much more appropriate, this
     * mode is much more difficult to process correctly in a general case than STEPS. So if the evaluation
     * needs to process gap notification and duration correctly based on LINEAR this currently has to
     * be implemented in the application.<br>
     * Note also that gaps are processed for each input time series individually, so if gaps in one input
     * should lead to an gap in output this has to be organized by the inheriting evaluation. Only the
     * calculation of the gapTime takes into account gaps from all time series together and the value
     * SpecificEvalValueContainer.gapStart can be used to check if any gap is active.
     * @param duration note that the duration of a gap may not be foreseeable, especially in an online
     * 		evaluation. So check for negative values indicating that the duration is not known when
     * 		using this value
     */
    protected void gapNotification(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx,
    		long timeStamp, SampledValue sv, SampledValueDataPoint dataPoint, long duration) {}
    
    public SpecificEvalBaseImpl(List<EvaluationInput> input, List<ResultType> requestedResults,
			Collection<ConfigurationInstance> configurations, EvaluationListener listener, long time,
			String evalId, int requestedInputNum) {
		super(input, requestedResults, configurations, listener, time);
	    this.id = evalId + "_" + idcounter.incrementAndGet();
        if (input.size() != requestedInputNum)
        	throw new IllegalArgumentException("Expecting exactly "+requestedInputNum+" types of input time series, got " + input.size());
        nrInput = new int[requestedInputNum];
        idxSumOfPrevious = new int[requestedInputNum+1];
        int sum = 0;
        for(int i=0; i<requestedInputNum; i++) {
        	this.nrInput[i] = input.get(i).getInputData().size();
        	this.idxSumOfPrevious[i] = sum;
        	sum += nrInput[i];
        }
        this.idxSumOfPrevious[requestedInputNum] = sum;
        startEnd = EvaluationUtils.getStartAndEndTime(configurations, input, false);
        values = initValueContainer(input);
	}
    
    @Override
    public String id() {
        return id;
    }

    @Override
    protected Map<ResultType, EvaluationResult> getCurrentResults() {
    	return values.getCurrentResults();
    }
    
    protected int getTotalInputIdx(int idxOfRequestedInput, int idxOfEvaluationInput) {
    	return idxSumOfPrevious[idxOfRequestedInput] + idxOfEvaluationInput;
    }
    
    private int getRequiredInputIdx(int totalIdx) {
        for(int i=0; i<nrInput.length; i++) {
        	if(totalIdx < idxSumOfPrevious[i+1]) {
        		return i;
        	}
        }
        throw new IllegalStateException("TotalIdx out of range!");
    }
    
    @Override
    protected void stepInternal(SampledValueDataPoint dataPoint) throws Exception {
    	if (requestedResults.isEmpty())
    		return;
    	synchronized (values) {
	        for (Map.Entry<Integer, SampledValue> entry : dataPoint.getElements().entrySet()) {
	            final int idx = entry.getKey();
              	final SampledValue sv = entry.getValue();
            	final long t = sv.getTimestamp();
 	            if(values.lastValueBeforeIntervalBoundaryInterchanged != null) {
	            	if(t < values.lastValidTimeStamp) continue;
	            	final SampledValueDataPoint dp = values.lastValueBeforeIntervalBoundaryInterchanged.dataPoint;
            		stepInternal(values.lastValueBeforeIntervalBoundaryInterchanged.inputIdx,
            				dp, t-dp.getTimestamp(), t);
	            	values.lastValueBeforeIntervalBoundaryInterchanged = null;
	            }
 	           final long tnext = dataPoint.getNextTimestamp();
 	           final long duration;
 	           if(!dataPoint.hasNext(idx) || tnext == Long.MAX_VALUE)
 	        	   duration = t - dataPoint.getPreviousTimestamp();
 	           else {
 	        	   duration = dataPoint.getNextTimestamp() - t;
 	        	   if(duration < 0) {
 	        		   System.out.println("TODO: Interval boundaries interchanged (3) !!");
 	        		   values.lastValueBeforeIntervalBoundaryInterchanged = new EvalDataPointInfo(idx, dataPoint);
 	        		   values.lastValidTimeStamp =t;
 	        		   continue;
 	        	   }
 	           }
 	           stepInternal(idx, dataPoint, duration, tnext);
	        }
    	}
    }
    
    private void stepInternal(int idx, SampledValueDataPoint dataPoint, long duration, long tnext) throws Exception {
        
        final SampledValue sv = dataPoint.getElement(idx);//entry.getValue();
        final boolean quality = sv.getQuality() == Quality.GOOD;
        final long  t = sv.getTimestamp();
//System.out.println("Data point of idx "+idx+" qu:"+quality+" val:"+sv.getValue().getFloatValue());	            
        
        //dataPoint.getElement(0, InterpolationMode.LINEAR);
        
        //Check if we have an initial gap here
        if((values.gapStart < 0) && (!values.allInitDone)) {
            boolean found = false;
        	int allIdxForInitCheck=0;
            for(boolean b: values.valueInitDone) {
        		if(!b) {
    	            int i= getRequiredInputIdx(allIdxForInitCheck);
    	            if(((t -startEnd[0]) > maximumGapTimeAccepted(i) )) {
    	            	if(!values.isInGap[allIdxForInitCheck]) {
    	            		gapNotification(i, allIdxForInitCheck - idxSumOfPrevious[i], allIdxForInitCheck, t,
    	    	            		sv, dataPoint, -1);	
    	            	}
    	                values.isInGap[allIdxForInitCheck] = true;
    	                values.gapStart = t;
    	                //we do not look for any further initial gaps
    	                values.allInitDone = true;
    	            }
            		found = true;
            		break;
        		}
        		allIdxForInitCheck++;
        	}
        	if(!found) {
        		values.allInitDone = true;
        	}	            	
        }
        values.valueInitDone[idx] = true;
        
        if (!quality) {
        	if(!values.isInGap[idx]) {
	            int i= getRequiredInputIdx(idx);
        		gapNotification(i, idx - idxSumOfPrevious[i], idx, t,
	            		sv, dataPoint, -1);	
        	}
            values.isInGap[idx] = true;
            if(values.gapStart < 0) values.gapStart = t;
            return; //continue;
        }	            

		values.isInGap[idx] = false;
        if(values.gapStart >= 0) {
        	boolean found = false;
        	for(boolean b: values.isInGap) if(b) {
        		found = true;
        		break;
        	}
        	if(!found) {
        		values.gapTime += (t - values.gapStart);
        		values.gapStart = -1;
        	}
        }
        int i= getRequiredInputIdx(idx);
        boolean newGap = false;
		if((tnext != Long.MAX_VALUE) && (duration > maximumGapTimeAccepted(i))) {
            values.isInGap[idx] = true;
            newGap = true;
            if(values.gapStart < 0) values.gapStart = t;
        }
		processValue(i, idx - idxSumOfPrevious[i], idx, t, sv, dataPoint,
				newGap?maximumGapTimeAccepted(i):duration);
		if(newGap)
			gapNotification(i, idx - idxSumOfPrevious[i], idx, t+maximumGapTimeAccepted(i),
					sv, dataPoint, duration-maximumGapTimeAccepted(i));
     }

    @Override
    public Status finishInternal() {
         if(values.gapStart >= 0) {
      		values.gapTime += (startEnd[1] - values.gapStart);
      		values.gapStart = -1;
        }
    	return super.finishInternal();
    }
}
