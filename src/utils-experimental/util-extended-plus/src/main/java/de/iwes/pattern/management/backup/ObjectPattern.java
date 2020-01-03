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

/** interface to use objects as identifiers in widgets
 * 
 * @author dnestle
 */
@Deprecated
public interface ObjectPattern {

	/** Notification from management of object patterns (exact usage to be defined)
	 * @param patternContainer
	 * @return true if the object shall be used, false if it shall not be put in the list of connected objects
	 */
	//boolean init(ResourcePatternContainer patternContainer);
	boolean accept();
	
	/** Notification from management of patterns that a pattern is not accessible anymore*/
	void disconnect();
	
	/** overwrite this if you want patterns with individual ids/names*/
	public String getName();

}
