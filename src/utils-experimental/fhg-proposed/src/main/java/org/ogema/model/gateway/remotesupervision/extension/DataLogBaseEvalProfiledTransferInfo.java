package org.ogema.model.gateway.remotesupervision.extension;

import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.gateway.remotesupervision.EvaluationTransferInfo;
import org.ogema.model.gateway.remotesupervision.ScheduleTransferInfo;
	
/** Basic evaluation using schedules to provide evaluation for intervals. Note that the
 * elements may also be put as separate schedules directly into {@link ScheduleTransferInfo}.
*/
public interface DataLogBaseEvalProfiledTransferInfo extends EvaluationTransferInfo {
	/** Average*/
	StatisticalAggregation average();
	/** Root mean square (if this exists usually average is not provided)*/
	StatisticalAggregation rms();
	/** Standard deviation*/
	StatisticalAggregation std();
	/** Minimum*/
	StatisticalAggregation min();
	/** Maximum*/
	StatisticalAggregation max();
	/**For standard deviation, minimum, maximum and rms a pre-filtering like pre-avering may be done.
	 * In this case the preFilterTime gives an indication of the filter period (the exact implementation
	 * of the filter has implications on the actual effect also, of course)
	 */
	TimeResource preFilterTime();
}