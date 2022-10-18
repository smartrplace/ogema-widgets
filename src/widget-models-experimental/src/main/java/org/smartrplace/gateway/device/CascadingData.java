package org.smartrplace.gateway.device;

import org.ogema.core.model.simple.BooleanResource;
import org.ogema.model.prototypes.Data;

public interface CascadingData extends Data {
	/** Example data field: trigger local backup creation that can be collected
	 * via sync.sh from subgateway
	 */
	BooleanResource triggerBackupCreation();
}
