/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
