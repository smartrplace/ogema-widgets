package org.smartrplace.server.gateway;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;
import org.smartrplace.apps.hw.install.config.InstallAppDevice;

/** 
 * Access configuration for gateways and groups of them.
 */
public interface GatewayData extends Data {
	//@Deprecated
	//ResourceList<GatewayGroupData> groups();
	
	GatewayGroupData installationLevelGroup();
	
	StringResource customer();
	StringResource comment();
	StringResource guiLink();
	
	/** See {@link InstallAppDevice#installationStatus()}
	 */
	IntegerResource installationStatus();
	
	/** GatewayId used to read remote slotsDb data*/
	StringResource remoteSlotsGatewayId();
}
