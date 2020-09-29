package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Configuration;

/** 
 * The global configuration resource type for this app.
 */
public interface AppstoreConfig extends Configuration {

	/** Data for apps in appstore*/
	ResourceList<AppData> appData();
	
	/** Data for apps groups in appstore*/
//	ResourceList<AppGroupData> appGroupData();

	/** Data for known gateways*/
	ResourceList<GatewayData> gatewayData();
	
	/** Data for gateway groups*/
	ResourceList<GatewayGroupData> gatewayGroupData();
	GatewayGroupData testingGroup();
	GatewayGroupData mainGroup();
	
	ResourceList<SystemUpdate> systemUpdates();
	
	/** Last part of versions generated
	 * TODO: This should be obtained from SystemUpdates or VersionGenerationService in the future
	 */
	IntegerResource currentLastVersionPart();
}
