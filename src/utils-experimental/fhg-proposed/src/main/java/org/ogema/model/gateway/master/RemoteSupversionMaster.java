package org.ogema.model.gateway.master;

import org.ogema.core.model.ResourceList;
import org.ogema.model.prototypes.Data;
	
/** Data of remote supervision master. Note: The ResourceList<GatewayTransferInfo> has to
 * be a toplevel resource list currently, so this is not included here (yet)
 */
public interface RemoteSupversionMaster extends Data {
	/**Remote gateway data*/
	ResourceList<GatewayInfo> gatewayInfos();
	
	GatewayInfo aggregatedData();
}