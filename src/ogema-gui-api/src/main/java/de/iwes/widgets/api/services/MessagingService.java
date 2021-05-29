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
package de.iwes.widgets.api.services;

import java.util.concurrent.RejectedExecutionException;

import org.ogema.core.application.AppID;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.messaging.Message;

/**
 * Messaging service, allows an app to send messages to user for display in a user interface
 */
public interface MessagingService {

	/**
	 * @param am
	 * @param message
	 * @throws RejectedExecutionException
	 * 			if app sends too many messages (&gt; 25 per 5 min), or there are overall too many unread messages in the queue
	 * @throws IllegalStateException
	 * 			if app has not been registered to send messages. See {@link #registerMessagingApp(AppID, String)}.
	 * @deprecated use {@link #sendMessage(AppID, Message)} instead
	 */
	@Deprecated
	void sendMessage(ApplicationManager am, Message message) throws RejectedExecutionException, IllegalStateException;
	
	/**
	 * @param appId
	 * @param message
	 * @throws RejectedExecutionException
	 * 			if app sends too many messages (&gt; 25 per 5 min), or there are overall too many unread messages in the queue
	 * @throws IllegalStateException
	 * 			if app has not been registered to send messages. See {@link #registerMessagingApp(AppID, String)}.
	 */
	void sendMessage(AppID appId, Message message) throws RejectedExecutionException, IllegalStateException;
	
	/**
	 * Before an app can send messages, it must register itself with the service
	 * @param appId
	 * @param humanReadableId
	 * @throws IllegalArgumentException if the app id is already registered, or another app with the same human readable name exists, 
	 * 			or the String argument is shorter than 5 characters 
	 */
	void registerMessagingApp(AppID appId, String humanReadableId) throws IllegalArgumentException;
	
	/** Like {@link #registerMessagingApp(AppID, String)}, but allow to set name of Alarming application explicitly.
	 * Otherwise the name of the bundle declared in the pom.xml is used.
	 * @param appId
	 * @param humanReadableId
	 * @param name
	 * @throws IllegalArgumentException
	 */
	void registerMessagingApp(AppID appId, String humanReadableId, String name) throws IllegalArgumentException;
	
	/**
	 * Unregister an app
	 * @param appId
	 */
	void unregisterMessagingApp(AppID appId);
	
}
