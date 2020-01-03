package org.ogema.model.gateway.remotesupervision;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.gateway.incident.Incident;
	
/** Incident to be transferred from client to master. Note that the transfer interval is
 * determined by the RemoteRESTConnector configuration of the entire {@link GatewayTransferInfo} resource.
*/
public interface IncidentTransferInfo extends Incident {
	/**Client resource is not replicated in resource structure of master, so we give the resource
	 * location on client here*/
	StringResource clientLocation();
	
	/**Settings for a type can be overwritten by an event. But values not overwritten shall
	 * apply to the event instance
	 */
	TransmissionStorageControl transmissionStorageControl();
}