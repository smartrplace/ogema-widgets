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
package de.iwes.widgets.messaging.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.ogema.core.application.AppID;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.messaging.MessagingApp;

class MessageLists {
	
	final ConcurrentNavigableMap<Long, ReceivedMessageImpl> unreadMessages = new ConcurrentSkipListMap<Long, ReceivedMessageImpl>();
	final ConcurrentNavigableMap<Long, ReceivedMessageImpl> readMessages = new ConcurrentSkipListMap<Long, ReceivedMessageImpl>();
	final ConcurrentNavigableMap<Long, ReceivedMessageImpl> deletedMessages = new ConcurrentSkipListMap<Long, ReceivedMessageImpl>();
	final ConcurrentMap<String, MessageListener> listeners = new ConcurrentHashMap<String, MessageListener>();
	final ConcurrentMap<AppID, MessagingApp> registeredMessageSenders = new ConcurrentHashMap<>();
	//final static List<UserConfig> forwardConfigList = Collections.synchronizedList(new ArrayList<UserConfig>()); 
	
	void clear() {
		listeners.clear();
		registeredMessageSenders.clear();
		//forwardConfigList.clear();
	}
	
}
