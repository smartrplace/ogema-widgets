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
package org.ogema.model.action.guilink;

import org.ogema.core.model.Resource;
import org.ogema.model.action.GUIExtensionElement;

/** Information for the extension of a certain page, e.g. a room */
public interface GUILinkOffer extends GUIExtensionElement {
	/**The linking resource for which the link is provided, e.g. a room. If the link is more
	 * general and not connected to a certain resource the element should not be present
	 */
	Resource item();
}
