package org.smartrplace.gateway.device;

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/** Device network interface information.
 *
 */
public interface InterfaceInfo extends Data {
	
	/**
	 * @return current addresses of this interface, or last addresses if interface is down.
	 */
	StringArrayResource addresses();
	
	BooleanResource up();
	
	StringResource hardwareAddress();
	
}
