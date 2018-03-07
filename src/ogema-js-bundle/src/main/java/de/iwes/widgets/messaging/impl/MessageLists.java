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
