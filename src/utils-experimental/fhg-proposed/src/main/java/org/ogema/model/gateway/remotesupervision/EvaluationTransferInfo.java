package org.ogema.model.gateway.remotesupervision;

import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;
	
/** Information on log data evaluation that is calculated on client and shall be transferred to master.
 * This avoids recalculation of the same data on the master even if the raw data is also transferred to
 * the master. For different evaluation types usually derived models shall be defined such as
*/
public interface EvaluationTransferInfo extends Data {
	/** Start of the interval used for this evaluation*/
	TimeResource startTime();
	
	/** Start of the interval used for this evaluation*/
	TimeResource endTime();
}