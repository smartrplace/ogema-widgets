package de.iwes.app.timeseries.teststarter.config;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Configuration;

/** 
 * The global configuration resource type for this app.
 */
public interface TeststarterConfig extends Configuration {

	ResourceList<TeststarterProgramConfig> availablePrograms();
	
	StringResource sampleElement();
	
	// TODO add global settings

}
