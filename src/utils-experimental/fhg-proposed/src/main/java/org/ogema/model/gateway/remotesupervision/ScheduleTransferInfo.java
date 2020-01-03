package org.ogema.model.gateway.remotesupervision;

import org.ogema.core.model.simple.FloatResource;
	
/** Schedule to be transferred continuously from client to master. Note that both the master and the client can
 * initiate to delete old values from the schedule. Note that very large schedules can cause heavy community
 * load.
*/
public interface ScheduleTransferInfo extends ValueTransferInfo {
	/**Use schedule below. Typically use historicalData, forecast or program depending on the type of data.
	 * For evaluation data typically historicalData should be used.*/
	FloatResource scheduleHolder();
}