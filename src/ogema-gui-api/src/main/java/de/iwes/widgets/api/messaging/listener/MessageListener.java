/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.api.messaging.listener;

import java.util.List;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

/**
 * Service that should be registered by an app that conveys messages to the user
 * e.g. through a user interface, or any other channel (email, SMS, ...).
 */
public interface MessageListener {
	
	/**
	 * A unique, human-readable id (e.g. "Email","SMS")
	 * @return
	 */
	String getId();
	
	/**
	 * A human-readable description
	 * @return
	 */
	String getDescription(OgemaLocale locale);

	/**
	 * Callback when a new message is available
	 * @param message
	 * @param recipients
	 */
	void newMessageAvailable(ReceivedMessage message, List<String> recipients);
	
	/**
	 * Return a list of possible recipients (ids)
	 * @return
	 */
	List<String> getKnownUsers();
	
}
