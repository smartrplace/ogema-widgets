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
package org.ogema.model.protectedaction;

import org.ogema.core.model.simple.StringResource;
import org.ogema.model.action.Action;

/** Action that copies a file or a folder to a destination defined by the parent resource*/
public interface FixDestinationCopyAction extends Action {
	/**destination directory or file name*/
	StringResource source();
	FixDestinationCopyParameters parameters();
}
