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

import org.ogema.core.model.ModelModifiers.NonPersistent;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;

/** Configuration for copying a file to the backup collection*/
public interface FixDestinationCopyActionNonP extends FixDestinationCopyAction {

	/**destination directory or file name*/
	@NonPersistent
	@Override
	StringResource source();

	FixDestinationCopyParameters parameters();
}
