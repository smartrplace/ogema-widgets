package de.iwes.timeseries.eval.api.extended;

import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;

/** A PreEvaluationProvider can transfer the result of one (Multi-)evaluation to another.
 * In offline evaluation typically the pre-evaluation is acqiured by the destination evaluation
 * before the destination evaluation is started, e.g. directly in the constructor. In online
 * evaluations the destination evaluation may have to check whether a certain PreEvaluationData is
 * available already.<br>
 * Typically a PreEvaluationProvider should provide its data from a JSON result file of the
 * source evaluation or from an AbstractSuperMultiResult object directly obtained from the
 * source application.<br>
 * TODO: A listener concept for online evaluation may be helpful, but is not provided by this
 * interface yet. It could also be discussed whether PreEvaluation should be made available via
 * a specific DataProvider. It would be quite difficult to transfer entire result structs in this
 * case, though. So from the current perspective is seems justified to define a separate mechanism
 * here apart from the DataProvider mechanism used to provide the sensor data and similar data to
 * the evaluations. PreEvaluation Data could also be transferred by a configuration, but this
 * would not allow for updates during online evaluation at all and the configuration API seems not very suitable
 * for an efficient implementation of this use case also.
 *
 * @param <T>
 * @param <S>
 */
public interface PreEvaluationProvider<R> {
	/**return null if no multi-evaluation*/
	<T extends MultiResult<R>, S extends AbstractSuperMultiResult<R, T>> S getSuperEvalData();
	<T extends MultiResult<R>> T getIntervalData(long startTime);
}
