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

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.prototypes.Data;

/** Intended to be provided by an application that can be linked based on
 * certain topics and linking resources */
public interface GUILinkData extends Data {
	/** Commonly known topics are:
	 * Heating
	 * Electricity
	 * Electricity Metering
	 * Building Security
	 * Messaging
	 */
	StringResource topic();

	/** The linking resource type. If not specified are an empty String contained then no
	 * linking resource is applied. In this case only a single element is expected in
	 * linkOffers. Usually the class given here should be a full OGEMA model class name,
	 * but also other Java class names can be used if necessary.*/
	StringResource linkingResourceType();
	
	/**The links that are actually offered by the application*/
	ResourceList<GUILinkOffer> linkOffers();
}
