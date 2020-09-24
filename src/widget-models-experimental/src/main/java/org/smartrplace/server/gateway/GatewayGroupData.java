package org.smartrplace.server.gateway;

import org.ogema.model.prototypes.Data;

/** 
 * Access configuration for gateways and groups of them.
 */
public interface GatewayGroupData extends Data {
	SystemUpdate systemUpdateVersion();
}
