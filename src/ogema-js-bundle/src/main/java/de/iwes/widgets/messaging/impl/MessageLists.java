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

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.ogema.core.application.AppID;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.MessageStatus;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.messaging.MessagingApp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

class MessageLists {

	private final ConcurrentNavigableMap<Long, ReceivedMessageImpl> unreadMessages = new ConcurrentSkipListMap<Long, ReceivedMessageImpl>();
	private final ConcurrentNavigableMap<Long, ReceivedMessageImpl> readMessages = new ConcurrentSkipListMap<Long, ReceivedMessageImpl>();
	private final ConcurrentNavigableMap<Long, ReceivedMessageImpl> deletedMessages = new ConcurrentSkipListMap<Long, ReceivedMessageImpl>();
	final ConcurrentMap<String, MessageListener> listeners = new ConcurrentHashMap<String, MessageListener>();
	final ConcurrentMap<AppID, MessagingApp> registeredMessageSenders = new ConcurrentHashMap<>();
	//final static List<UserConfig> forwardConfigList = Collections.synchronizedList(new ArrayList<UserConfig>()); 

	void clear() {
		listeners.clear();
		registeredMessageSenders.clear();
		//forwardConfigList.clear();
	}

	int getUnreadCount() {
		return unreadMessages.size();
	}

	int getReadCount() {
		return readMessages.size();
	}

	int getDeletedCount() {
		return deletedMessages.size();
	}

	void trimRead(int maxSize) {
		clean(readMessages, maxSize);
	}

	void trimUnread(int maxSize) {
		clean(unreadMessages, maxSize);
	}

	void trimDeleted(int maxSize) {
		clean(deletedMessages, maxSize);
	}

	void addUnread(long ts, ReceivedMessageImpl msg) {
		unreadMessages.put(ts, msg);
	}

	NavigableMap<Long, ReceivedMessage> getMessages(long startTime) {
		NavigableMap<Long, ReceivedMessage> submap = new TreeMap<Long, ReceivedMessage>();
		submap.putAll(unreadMessages.tailMap(startTime));
		submap.putAll(readMessages.tailMap(startTime));
		submap.putAll(deletedMessages.tailMap(startTime));
		return submap;
	}

	NavigableMap<Long, ReceivedMessage> getMessages(MessageStatus status) {
		switch (status) {
			case CREATED:
				return Collections.unmodifiableNavigableMap(unreadMessages);
			case READ:
				return Collections.unmodifiableNavigableMap(readMessages);
			case DELETED:
				return Collections.unmodifiableNavigableMap(deletedMessages);
			default:
				return Collections.emptyNavigableMap();
		}
	}

	NavigableMap<Long, ReceivedMessage> getMessages(long startTime, MessagePriority priority) {
		NavigableMap<Long, ReceivedMessage> submap = new TreeMap<Long, ReceivedMessage>();
		Iterator<Map.Entry<Long, ReceivedMessageImpl>> it = unreadMessages.tailMap(startTime).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, ReceivedMessageImpl> entry = it.next();
			ReceivedMessageImpl msg = entry.getValue();
			if (msg.getOriginalMessage().priority() != priority) {
				continue;
			}
			submap.put(entry.getKey(), msg);
		}
		it = readMessages.tailMap(startTime).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, ReceivedMessageImpl> entry = it.next();
			ReceivedMessageImpl msg = entry.getValue();
			if (msg.getOriginalMessage().priority() != priority) {
				continue;
			}
			submap.put(entry.getKey(), msg);
		}
		it = deletedMessages.tailMap(startTime).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, ReceivedMessageImpl> entry = it.next();
			ReceivedMessageImpl msg = entry.getValue();
			if (msg.getOriginalMessage().priority() != priority) {
				continue;
			}
			submap.put(entry.getKey(), msg);
		}
		return submap;
	}

	void setMessageStatus(ReceivedMessage message, MessageStatus status) {
		if (message == null || status == null) {
			return;
		}
		synchronized (message) {
			if (status.getStatus() < MessageStatus.SENT.getStatus()) {
				throw new IllegalArgumentException("Illegal message status: " + status.name());
			}
			if (message.getStatus() == status) {
				return;
			}
			if (!(message instanceof ReceivedMessageImpl)) {
				throw new RuntimeException("Received invalid message.");
			}
			ReceivedMessageImpl msgImpl = (ReceivedMessageImpl) message;
			switch (message.getStatus()) {
				case SENT:
					unreadMessages.remove(message.getTimestamp());
					break;
				case READ:
					readMessages.remove(message.getTimestamp());
					break;
				case DELETED:
					deletedMessages.remove(message.getTimestamp());
					break;
				default:
			}
			switch (status) {
				case SENT:
					unreadMessages.put(message.getTimestamp(), msgImpl);
					break;
				case READ:
					readMessages.put(message.getTimestamp(), msgImpl);
					break;
				case DELETED:
					deletedMessages.put(message.getTimestamp(), msgImpl);
					break;
				default:
			}
			msgImpl.setStatus(status);
		}
	}

	void removeMessage(ReceivedMessage message) throws IllegalArgumentException {
		synchronized (message) {
			if (message.getStatus() != MessageStatus.DELETED) {
				throw new IllegalArgumentException("Message status is not DELETED, cannot remove message");
			}
			deletedMessages.remove(message.getTimestamp());
			((ReceivedMessageImpl) message).setStatus(MessageStatus.CREATED); // not nice, but effectively hides the message; replace by status REMOVED?
		}
	}

	ReceivedMessageImpl getUnread(long ts) {
		return unreadMessages.get(ts);
	}

	ReceivedMessageImpl getRead(long ts) {
		return readMessages.get(ts);
	}

	ReceivedMessageImpl getDeleted(long ts) {
		return deletedMessages.get(ts);
	}

	NavigableMap<Long, ReceivedMessage> getMessages(long tm, MessageStatus status) {
		switch (status) {
			case SENT:
				return Collections.unmodifiableNavigableMap(unreadMessages.tailMap(tm));
			case READ:
				return Collections.unmodifiableNavigableMap(readMessages.tailMap(tm));
			case DELETED:
				return Collections.unmodifiableNavigableMap(deletedMessages.tailMap(tm));
			default:
				return Collections.emptyNavigableMap();
		}
	}

	ReceivedMessage getLastHighPriorityMsg() {
		NavigableMap<Long, ReceivedMessageImpl> map = unreadMessages.descendingMap(); // start with latest entry
		ReceivedMessage lastM = null;
		int lastP = -1;
		Iterator<Map.Entry<Long, ReceivedMessageImpl>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, ReceivedMessageImpl> entry = it.next();
			ReceivedMessage msg = entry.getValue();
			int prio = msg.getOriginalMessage().priority().getPriority();
			if (prio > lastP) {
				if (prio == MessagePriority.HIGH.getPriority()) {
					return msg;
				}
				lastP = prio;
				lastM = msg;
			}
		}
		return lastM;
	}

	private static void clean(Map<Long, ReceivedMessageImpl> map, int maxSize) {  // while this implementation is simple and fast, it deletes up to half of the messages marked as 'deleted'
		Iterator<Map.Entry<Long, ReceivedMessageImpl>> it = map.entrySet().iterator();
		int counter = 0;
		try {
			while (it.hasNext() && counter++ < maxSize / 2) {
				it.next();
				it.remove();
			}
		} catch (UnsupportedOperationException e) {
			return;
		}
	}
	
	public static class MapMessage implements Message, Serializable {

		private static final long serialVersionUID = 1L;
		
		Map<OgemaLocale, String> titles = new HashMap<>();
		Map<OgemaLocale, String> messages = new HashMap<>();
		String link;
		MessagePriority prio;
		
		private MapMessage() {}
		
		public MapMessage(Message orig, OgemaLocale ... locales) {
			link = orig.link();
			prio = orig.priority();
			for (OgemaLocale l: locales) {
				String t = orig.title(l);
				if (t != null) {
					titles.put(l, t);
				}
				String m = orig.message(l);
				if (m != null) {
					messages.put(l, m);
				}
			}
		}

		@Override
		public String title(OgemaLocale locale) {
			if (titles.isEmpty()) {
				return null;
			}
			return titles.getOrDefault(locale,
					titles.getOrDefault(OgemaLocale.ENGLISH, titles.values().iterator().next()));
		}

		@Override
		public String message(OgemaLocale locale) {
			if (messages.isEmpty()) {
				return null;
			}
			return messages.getOrDefault(locale,
					messages.getOrDefault(OgemaLocale.ENGLISH, messages.values().iterator().next()));
		}

		@Override
		public String link() {
			return link;
		}

		@Override
		public MessagePriority priority() {
			return prio;
		}
	}

	private ReceivedMessageImpl readMessage(ObjectInputStream in, AppID appId) throws IOException, ClassNotFoundException {
		long ts = in.readLong();
		MessageStatus status = (MessageStatus) in.readObject();
		//AppID appId = (AppID) in.readObject();
		String appName = in.readUTF();
		MapMessage msg = (MapMessage) in.readObject();
		ReceivedMessageImpl rm = new ReceivedMessageImpl(msg, ts, appId, appName);
		rm.setStatus(status);
		return rm;
	}
	
	private void writeMessage(ReceivedMessageImpl m, ObjectOutputStream out, OgemaLocale ... locales) throws IOException {
		out.writeLong(m.getTimestamp());
		out.writeObject(m.getStatus());
		//out.writeObject(m.getAppId());
		out.writeUTF(m.getAppName());
		Message orig = m.getOriginalMessage();
		MapMessage mm = new MapMessage(orig, locales);
		out.writeObject(mm);
	}
	
	private void writeMessageMap(Map<Long, ReceivedMessageImpl> m, ObjectOutputStream out, OgemaLocale ... locales) throws IOException {
		out.writeInt(m.size());
		for (ReceivedMessageImpl msg: m.values()) {
			writeMessage(msg, out, locales);
		}
	}
	
	private void writeMessageMap(Map<Long, ReceivedMessageImpl> m, Path p) throws IOException {
		try (OutputStream out = Files.newOutputStream(p, StandardOpenOption.CREATE);
				BufferedOutputStream bos = new BufferedOutputStream(out);
				ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			writeMessageMap(m, oos);
		}
	}
	
	private ConcurrentNavigableMap<Long, ReceivedMessageImpl> readMessageMap(ObjectInputStream in, AppID appId) throws IOException, ClassNotFoundException {
		long size = in.readInt();
		ConcurrentNavigableMap<Long, ReceivedMessageImpl> map = new ConcurrentSkipListMap<>();
		for (int i = 0; i < size; i++) {
			ReceivedMessageImpl msg = readMessage(in, appId);
			map.put(msg.getTimestamp(), msg);
		}
		return map;
	}
	
	private ConcurrentNavigableMap<Long, ReceivedMessageImpl> readMessageMap(Path p, AppID appId) throws IOException, ClassNotFoundException {
		try (InputStream fis = Files.newInputStream(p) ;
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream in = new ObjectInputStream(bis)) {
			return readMessageMap(in, appId);
		}
	}
	
	void load(Path p, AppID appId) throws IOException, ClassNotFoundException {
		if (!Files.exists(p)) {
			return;
		}
		try (InputStream fis = Files.newInputStream(p) ;
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream in = new ObjectInputStream(bis)) {
			readMessages.putAll(readMessageMap(in, appId));
			unreadMessages.putAll(readMessageMap(in, appId));
			deletedMessages.putAll(readMessageMap(in, appId));
		}
	}
	
	void write(Path p, OgemaLocale ... locales) throws IOException {
		try (OutputStream out = Files.newOutputStream(p, StandardOpenOption.CREATE);
				BufferedOutputStream bos = new BufferedOutputStream(out);
				ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			writeMessageMap(readMessages, oos, locales);
			writeMessageMap(unreadMessages, oos, locales);
			writeMessageMap(deletedMessages, oos, locales);
		}
	}

}
