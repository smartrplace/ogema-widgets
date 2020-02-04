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

/** Action that copies a file or a folder to a destination defined by the parent resource*/
public interface FixDestinationCopyParametersNonP extends FixDestinationCopyParameters {
	/**If source is a directory and this is true, the directory and the entire content are copied*/
	@NonPersistent
	@Override
	BooleanResource recursive();
	
	/**The results may be zipped by the operation or they may be just transferred to the
	 * destination. The Zip file may be placed at the parent directory (standard)
	 * or in the same directory (depending on application)
	 */
	@NonPersistent
	@Override
	BooleanResource doZip();
}