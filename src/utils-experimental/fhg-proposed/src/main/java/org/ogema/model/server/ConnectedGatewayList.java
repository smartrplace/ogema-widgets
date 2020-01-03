package org.ogema.model.server;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;

/**
 *
 * @author jlapp
 */
public interface ConnectedGatewayList extends Resource {
    
    ResourceList<ConnectedGateway> gateways();
    
}
