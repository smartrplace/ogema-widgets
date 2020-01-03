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

/** Replacement of ResourcePattern to be used with ResourcePatternManagement
 * 
 * @author dnestle
 */
@Deprecated
public abstract class ManagedResourcePatternCallbackStyle<DemandedModel extends Resource> extends
			ManagedResourcePattern<DemandedModel, Object> {

	public ManagedResourcePatternCallbackStyle(Resource match) {
		super(match);
	}
	
	/** Notification from ResourcePatternManagement that new fitting patternContainer object is available
	 * @param patternContainer
	 * @return true if the object shall be used, false if it shall not be put in the list of connected objects
	 */
	//public boolean init(Object patternContainer) {
	//	return true;
	//}
}
