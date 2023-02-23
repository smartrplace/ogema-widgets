package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Configuration;

/** 
 * The global configuration resource type for this app.
 */
public interface AppstoreConfig extends Configuration {

	/** Data for apps groups in appstore*/
	//ResourceList<AppGroupData> appGroupData();
	
	/** Data for apps in appstore*/
	@Deprecated
	ResourceList<AppData> appData();

	/** Data for known gateways*/
	ResourceList<GatewayData> gatewayData();
	
	/** Data for gateway groups*/
	ResourceList<GatewayGroupData> gatewayGroupData();
	GatewayGroupData testingGroup();
	GatewayGroupData mainGroup();
	
	@Deprecated
	ResourceList<SystemUpdate> systemUpdates();
	
	/** Last part of versions generated
	 * TODO: This should be obtained from SystemUpdates or VersionGenerationService in the future
	 */
	@Deprecated
	IntegerResource currentLastVersionPart();
	
	/** Last time a new version was created locally based on the existing bundles or a resore of a local
	 * version was triggered
	 */
	TimeResource lastVersionCreationOrRestore();
	
	/** Last time a restore of a local version was triggered */
	TimeResource lastVersionRestore();

	/** For local appstore here a head version entry should exist representing a software setup
	 * not represented by a local backup*/
	SystemUpdate headVersion();
}
