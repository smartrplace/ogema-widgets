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
	 * 			if app sends too many messages (> 25 per 5 min), or there are overall too many unread messages in the queue
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
	 * 			if app sends too many messages (> 25 per 5 min), or there are overall too many unread messages in the queue
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
	
	/**
	 * Unregister an app
	 * @param appId
	 */
	void unregisterMessagingApp(AppID appId);
	
}
