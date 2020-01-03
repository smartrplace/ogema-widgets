/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
/**
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.pattern.management.backup;

import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.pattern.ContextSensitivePattern;

/** Replacement of ResourcePattern to be used with ResourcePatternManagement
 * 
 * @author dnestle
 */
@Deprecated
public abstract class ManagedResourcePattern<DemandedModel extends Resource, C extends Object> extends ContextSensitivePattern<DemandedModel, C>
		implements ObjectPattern {

	public ManagedResourcePattern(Resource match) {
		super(match);
	}
	
	/** Notification from ResourcePatternManagement that new fitting patternContainer object is available
	 * @param patternContainer
	 * @return true if the object shall be used, false if it shall not be put in the list of connected objects
	 */
	//public boolean init(ResourcePatternContainer patternContainer) {
	//	return true;
	//}
	
	/** Notification from ResourcePatternManagement that a pattern is not accessible anymore*/
	public void disconnect() {}
	
	/** overwrite this if you want patterns with individual ids/names*/
	public String getName() {
		return model.getLocation();
	}
	
	@SuppressWarnings("unchecked")
	/**only relevant for shadowing*/
	public DemandedModel checkForShadowResource() {
		Resource shadowResource = model.getSubResource("shadowResource");
		if((shadowResource == null) ||(!shadowResource.exists())) return null;
		if(!shadowResource.getResourceType().equals(model.getResourceType())) return null;
		return (DemandedModel)shadowResource;
	}
}
