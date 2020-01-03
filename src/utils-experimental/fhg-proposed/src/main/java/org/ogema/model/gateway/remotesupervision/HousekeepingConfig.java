package org.ogema.model.gateway.remotesupervision;

import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;
	
/** TODO: make this real
*/
public interface HousekeepingConfig extends Data {
	TimeResource maxStorageTime();
	
}