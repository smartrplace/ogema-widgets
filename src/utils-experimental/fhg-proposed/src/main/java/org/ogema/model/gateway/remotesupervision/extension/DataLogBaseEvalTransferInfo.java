package org.ogema.model.gateway.remotesupervision.extension;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.gateway.remotesupervision.EvaluationTransferInfo;
	
/** Basic evaluation for a certain time range
*/
public interface DataLogBaseEvalTransferInfo extends EvaluationTransferInfo {
	/** Average*/
	FloatResource average();
	/** Root mean square (if this exists usually average is not provided)*/
	FloatResource rms();
	/** Standard deviation*/
	FloatResource std();
	/** Minimum*/
	FloatResource min();
	/** Maximum*/
	FloatResource max();
	/**For standard deviation, minimum, maximum and rms a pre-filtering like pre-avering may be done.
	 * In this case the preFilterTime gives an indication of the filter period (the exact implementation
	 * of the filter has implications on the actual effect also, of course)
	 */
	TimeResource preFilterTime();
}