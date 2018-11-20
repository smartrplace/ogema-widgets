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
package de.iwes.widgets.messaging;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.MessageStatus;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;

/**
 * Service intended for an app that displays all messages, or allows configuration of 
 * message forwarding to {@link MessageListener}s.
 */
public interface MessageReader {
	
	/**
	 * Recommended method to retrieve messages, best performance. Filters according to status (SENT, READ, DELETED). <br>
	 * Keys: timestamps. Returns an unmodifiable map.
	 */
	NavigableMap<Long, ReceivedMessage> getMessages(MessageStatus status);

	/**
	 * Returns a copy of the underlying map.
	 */
	NavigableMap<Long, ReceivedMessage> getMessages(long startTime);

	/**
	 * Returns a copy of the underlying map.
	 */
	NavigableMap<Long, ReceivedMessage> getMessages(long startTime, MessagePriority priority);
	
	/**
	 * Returns null if no message with the specified timestamp exists
	 */
	ReceivedMessage getMessage(long timestamp);

	/**
	 * MessageStatus must be either SENT, READ, or DELETED
	 */
	void setMessageStatus(ReceivedMessage message, MessageStatus status);
	
	/**
	 * only allowed if message status is DELETED
	 */
	void removeMessage(ReceivedMessage message) throws IllegalArgumentException;
	
	/*
	 * Note: only one listener per application is allowed. Subsequent calls of this method remove listeners
	 * registered previously.
	 */
//	void registerMessageListener(AppID appId, MessageListener listener);
//	
//	void unregisterMessageListener(AppID appId);
	
	/**
	 * Get registered message listeners, that forward messages via other media
	 * Required for a configuration app.
	 * @return
	 */
	Map<String, MessageListener> getMessageListeners();
	
	/**
	 * Get ids of apps registered as message senders.
	 * @return
	 */
	List<MessagingApp> getMessageSenders();
	
	/**
	 * Activate forwarding of messages from <code>sender</code> to the <code>listener</code>
	 * 
	 * @param sender
	 * 		senderId, or "ALL" for all apps
	 * @param listener
	 * @param recipients
	 * 		Map: keys: recipient ids (see {@link MessageListener#getKnownUsers()}), values: minimum 
	 * 		priority level for forwarded messages.<br>
	 * 		Pass null or an empty map to stop all forwarding from the specified sender to the specified listener.
	 */
	//removed for testing, maybe its not important anymore
	//void forwardMessages(String sender, MessageListener listener, Map<String,MessagePriority> recipients);
}
