package org.ogema.model.gateway.master;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.eval.EvaluationConfig;
import org.ogema.model.gateway.incident.IncidentProvider;
import org.ogema.model.gateway.remotesupervision.GatewayTransferInfo;
import org.ogema.model.gateway.remotesupervision.IncidentManagementTransferInfo;
import org.ogema.model.prototypes.Data;
import org.ogema.model.prototypes.PhysicalElement;
import org.ogema.model.server.ConnectedGateway;
	
/** Collection of data received by a remote supervision master for the own data administration of the master
 * Use {@link PhysicalElement#name()} for a human readable name 
 */
public interface GatewayInfo extends Data {
	/**backwards link*/
	GatewayTransferInfo remoteData();
	
	/** 
	 * Subresources for information provided or read by remote supervision master App
	 * If available you should get this from remoteData
	 */
	StringResource id();
	
	ConnectedGateway wANGatewayInfo();
	
	/**Imported data types*/
	ResourceList<DataLogImport> dataLogging();
	
	ResourceList<EventLogImport> eventLogging();
	
	/**Information on various backup sources. The first entry typically is the information on the
	 * gateway backup itself*/
	ResourceList<BackupData> backupData();
	
	/**Evaluation*/
	ResourceList<EvaluationConfig> evaluations();

	StringResource slotsDBReceiverStatus();
	
	/**Incidents generated on master for the device*/
	IncidentManagementTransferInfo incidents();
	ResourceList<IncidentProvider> incidentSpace();

}