package org.ogema.model.gateway.master;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.gateway.remotesupervision.DataLogTransferInfo;
import org.ogema.model.gateway.remotesupervision.GatewayTransferInfo;
import org.ogema.model.gateway.remotesupervision.HousekeepingConfig;
import org.ogema.model.prototypes.Data;
	
/** Note that data log information is directly provided by clients via {@link DataLogTransferInfo}. This
 * mode is intended for the master's own data administration and for log data that might come in via
 * different sources than {@link GatewayTransferInfo}. 
*/
public interface DataLogImport extends Data {
	StringResource remoteLocation();
	StringResource localDirectory();

	HousekeepingConfig houseKeeping();
}