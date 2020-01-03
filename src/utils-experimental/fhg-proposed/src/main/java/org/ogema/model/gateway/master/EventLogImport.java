package org.ogema.model.gateway.master;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.gateway.remotesupervision.HousekeepingConfig;
import org.ogema.model.prototypes.Data;
	
/** External log data
*/
public interface EventLogImport extends Data {
	StringResource localDirectory();
	HousekeepingConfig houseKeeping();
}