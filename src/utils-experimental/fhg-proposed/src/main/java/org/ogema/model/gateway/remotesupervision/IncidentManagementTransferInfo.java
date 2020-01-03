package org.ogema.model.gateway.remotesupervision;

import org.ogema.model.gateway.incident.IncidentManagement;

/**
* @deprecated use Incident instead
*/
@Deprecated
public interface IncidentManagementTransferInfo extends IncidentManagement {
	/**Settings for a type can be overwritten by an event. But values not overwritten shall
	 * apply to the event instance
	 */
	TransmissionStorageControl transmissionStorageControl();
}