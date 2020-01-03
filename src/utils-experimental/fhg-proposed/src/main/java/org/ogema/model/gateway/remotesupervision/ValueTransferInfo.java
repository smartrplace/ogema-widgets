package org.ogema.model.gateway.remotesupervision;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.alignedinterval.StatisticalAggregation;
import org.ogema.model.eval.EvaluationConfig;
import org.ogema.model.prototypes.Data;
	
/** Single value or schedule to be transferred continuously from client to master. Note that the transfer interval is
 * determined by the RemoteRESTConnector configuration of the entire {@link GatewayTransferInfo} resource.
*/
public interface ValueTransferInfo extends Data {
	/**Client resource is not replicated in resource structure of master, so we just give the resource
	 * location on client here*/
	StringResource clientLocation();

	/**Note that schedule-based evaluation results can either be provided here or as separate
	 * {@link ScheduleTransferInfo} objects. The first case requires to transfer the SingleValueData also
	 * and requires to transmit the entire {@link StatisticalAggregation} data, so in many cases using
	 * just a transfer of the minimal relevant resulting schedule in {@link GatewayTransferInfo}.valueData
	 * is the option to use. In this case the {@link EvaluationConfig} object is stored private (like the
	 * master does with his evaluations on client data) and the resulting the schedule is linked
	 * into {@link GatewayTransferInfo}.
	 */
	ResourceList<EvaluationTransferInfo> evaluations();
	
	TransmissionStorageControl transmissionStorageControl();
}