package org.smartrplace.system.guiappstore.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Configuration;

/** 
 * System update information to be shared among all sub-appstores
 */
public interface AppstoreSystemUpdates extends Configuration {
	/** Data for apps in appstore*/
	ResourceList<AppData> appData();
	
	ResourceList<SystemUpdate> systemUpdates();
	
	/** Last part of versions generated
	 * TODO: This should be obtained from SystemUpdates or VersionGenerationService in the future
	 */
	IntegerResource currentLastVersionPart();
}
