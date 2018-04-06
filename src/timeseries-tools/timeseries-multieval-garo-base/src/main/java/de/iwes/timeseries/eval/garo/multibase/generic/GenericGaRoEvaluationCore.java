package de.iwes.timeseries.eval.garo.multibase.generic;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.tools.timeseries.iterator.api.SampledValueDataPoint;

public abstract class GenericGaRoEvaluationCore {
	protected GenericGaRoSingleEvaluation evalInstance;
    /** Process new value
     * 
     * @param idxOfRequestedInput index of requested input
     * @param idxOfEvaluationInput index of time series within the requested input
     * @param totalInputIdx required for some access methods of EvaluationBaseImpl
     * @param timeStamp time of the current value
     * @param sv current SampledValue
     * @param dataPoint access to the input data structure
     * @param duration duration of the current value based on the assumption of InterpolationMode.STEPS.
     * 		See {@link #gapNotification()} for details. The duration is the time difference to the next SampledValue
     * 		in any input time series, so the time difference to the next call of processValue. If two values have
     * 		exactly the same time stamp for both calls of processValue the duration to the next different time
     * 		stamp is used.
     */
    protected abstract void processValue(int idxOfRequestedInput, int idxOfEvaluationInput,
    		int totalInputIdx,
    		long timeStamp, SampledValue sv, SampledValueDataPoint dataPoint, long duration);

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
}
