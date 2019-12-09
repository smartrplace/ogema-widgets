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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Application;
import org.ogema.core.model.ResourceList;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.messaging.listener.MessageStatus;
import de.iwes.widgets.api.messaging.listener.ReceivedMessage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.messaging.MessageReader;
import de.iwes.widgets.messaging.MessagingApp;
import de.iwes.widgets.messaging.model.UserConfig;

/**
 * Service intended for an app that displays all messages, or allows configuration of 
 * message forwarding to {@link MessageListener}s.
 */
//@Service(MessageReader.class) // now registered by MessagingServiceImpl
//@Component
class MessageReaderImpl extends HttpServlet implements MessageReader, Application {//, ResourceDemandListener<UserConfig> {
	
	private static final long serialVersionUID = 1L;
	private volatile ResourceList<UserConfig> configResources;
	private static final Logger logger = LoggerFactory.getLogger(MessageReader.class);
	private volatile ServiceTracker<MessageListener, MessageListener> tracker;
	private final MessageLists messages;
	private final ApplicationManager appMan;
	
	MessageReaderImpl(BundleContext ctx, ApplicationManager appMan, MessageLists messages) {
		this.appMan = appMan;
		this.messages = messages;
		activate(ctx);
		start(appMan);
	}
	
	void close() {
		stop(null);
		deactivate();
	}
	
	private final ServiceTrackerCustomizer<MessageListener, MessageListener> trackerCustomizer = new ServiceTrackerCustomizer<MessageListener, MessageListener>() {

		@Override
		public MessageListener addingService(ServiceReference<MessageListener> reference) {
			BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
			MessageListener listener = context.getService(reference);
			if (listener == null) {
				logger.warn("Got a null service object from service reference {}, bundle {}", reference, reference.getBundle());
				return null;
			}
			String id = listener.getId();
			if (id.equals("ALL")) {
				logger.warn("MessageListener id must not be equal to \"ALL\"");
				return null;
			}
			MessageListener oldListener = messages.listeners.putIfAbsent(id, listener);
			if (oldListener !=  null) 
				return null;
			return listener;
		}

		@Override
		public void modifiedService(ServiceReference<MessageListener> reference,MessageListener service) {}

		@Override
		public void removedService(ServiceReference<MessageListener> reference,	MessageListener service) {
			messages.listeners.remove(service.getId());
		}

	};
	
	private void activate(final BundleContext context) {
		this.tracker = new ServiceTracker<MessageListener, MessageListener>(context, MessageListener.class, trackerCustomizer);
		tracker.open();
		try {
			context.getServiceReferences(MessageListener.class, null).forEach(ref -> tracker.addingService(ref));
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void start(ApplicationManager am) {
		configResources = am.getResourceManagement().createResource("messageReaderImplConfigurations", ResourceList.class);
		configResources.setElementType(UserConfig.class);
		am.getWebAccessManager().registerWebResource("/ogema/messaging/service", this); 
	}
	
	public void stop(AppStopReason reason) {
		try {
			configResources = null;
			appMan.getWebAccessManager().unregisterWebResource("/ogema/messaging/service");
		} catch (Exception e) {}
	}
	
	private void deactivate() {
		final ServiceTracker<MessageListener, MessageListener> tracker = this.tracker;
		this.tracker = null;
		if (tracker != null) {
			try {
				tracker.close();
			} catch (Exception ignore) {}
		}
	}
	
	@Override
	public NavigableMap<Long, ReceivedMessage> getMessages(long startTime) {
		NavigableMap<Long, ReceivedMessage> submap = new TreeMap<Long, ReceivedMessage>();
		submap.putAll(messages.unreadMessages.tailMap(startTime));
		submap.putAll(messages.readMessages.tailMap(startTime));
		submap.putAll(messages.deletedMessages.tailMap(startTime));
		return submap;
	}

	@Override
	public NavigableMap<Long, ReceivedMessage> getMessages(MessageStatus status) {
		switch(status) {
		case CREATED:
			return Collections.unmodifiableNavigableMap(messages.unreadMessages);
		case READ:
			return Collections.unmodifiableNavigableMap(messages.readMessages);
		case DELETED:
			return Collections.unmodifiableNavigableMap(messages.deletedMessages);
		default: 
			return Collections.emptyNavigableMap();
		}
	}

	@Override
	public NavigableMap<Long, ReceivedMessage> getMessages(long startTime, MessagePriority priority) {
		NavigableMap<Long, ReceivedMessage> submap = new TreeMap<Long, ReceivedMessage>();
		Iterator<Entry<Long,ReceivedMessageImpl>> it = messages.unreadMessages.tailMap(startTime).entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long,ReceivedMessageImpl> entry = it.next();
			ReceivedMessageImpl msg = entry.getValue();
			if (msg.getOriginalMessage().priority() != priority) continue;
			submap.put(entry.getKey(), msg);
		}
	    it = messages.readMessages.tailMap(startTime).entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long,ReceivedMessageImpl> entry = it.next();
			ReceivedMessageImpl msg = entry.getValue();
			if (msg.getOriginalMessage().priority() != priority) continue;
			submap.put(entry.getKey(), msg);
		}
		it = messages.deletedMessages.tailMap(startTime).entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long,ReceivedMessageImpl> entry = it.next();
			ReceivedMessageImpl msg = entry.getValue();
			if (msg.getOriginalMessage().priority() != priority) continue;
			submap.put(entry.getKey(), msg);
		}
		return submap;
	}


	@Override
	public void setMessageStatus(ReceivedMessage message, MessageStatus status) {
		if (message == null || status == null) return;
		synchronized(message) {
			if (status.getStatus() < MessageStatus.SENT.getStatus()) throw new IllegalArgumentException("Illegal message status: " + status.name());
			if (message.getStatus() == status) return;
			if (!(message instanceof ReceivedMessageImpl)) throw new RuntimeException("Received invalid message.");
			ReceivedMessageImpl msgImpl = (ReceivedMessageImpl) message;
			switch(message.getStatus()) {
			case SENT:
				messages.unreadMessages.remove(message.getTimestamp());
				break;
			case READ:
				messages.readMessages.remove(message.getTimestamp());
				break;
			case DELETED:
				messages.deletedMessages.remove(message.getTimestamp());
				break;
			default:
			}
			switch(status) {
			case SENT: 
				messages.unreadMessages.put(message.getTimestamp(),msgImpl);
				break;
			case READ:
				messages.readMessages.put(message.getTimestamp(),msgImpl);
				break;
			case DELETED:
				messages.deletedMessages.put(message.getTimestamp(),msgImpl);
				break;
			default:
			}			
			msgImpl.setStatus(status);		
		}
	}

////	@Override
//	public void registerMessageListener(String id,MessageListener listener) {
//		listeners.put(id,listener);		
//	}
//
////	@Override
//	public void unregisterMessageListener(String id) {
//		listeners.remove(appId);		
//	}

	@Override
	public void removeMessage(ReceivedMessage message) throws IllegalArgumentException {
		synchronized(message) {
			if (message.getStatus() != MessageStatus.DELETED) throw new IllegalArgumentException("Message status is not DELETED, cannot remove message");
			messages.deletedMessages.remove(message.getTimestamp());
			((ReceivedMessageImpl) message).setStatus(MessageStatus.CREATED); // not nice, but effectively hides the message; replace by status REMOVED?
		}
	}
	
	// check for messages; provide parameter 'startTime' (in ms), 'locale' (typically read from javascript variable 'locale'), and 'status'
	// allowed values for status: SENT, READ, DELETED, ALL
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String startTimeStr = req.getParameter("startTime");
		long startTime = System.currentTimeMillis() - 5*60*1000L; // default start time = now - 5min, if not provided in request
		if (startTimeStr != null) {
			try {
				startTime = Long.parseLong(startTimeStr);
			} catch (Exception e) {}
		}
		String statusStr = req.getParameter("status");
		MessageStatus status = MessageStatus.SENT;
		try {
			status = MessageStatus.valueOf(statusStr);
		} catch (Exception e) {}

		String lang = req.getParameter("locale");
		OgemaLocale locale = OgemaLocale.getLocale(lang);
		if (locale == null) {
			locale = OgemaLocale.ENGLISH; // TODO possibility to register default locale.
		}		
		JSONObject obj = new JSONObject();
		String lastHP = req.getParameter("lastHighPriority");
		boolean lhp = false;
		if (lastHP != null) {
			try {
				lhp = Boolean.parseBoolean(lastHP);
			} catch (Exception e) {}
		}
		if (lhp) {  // return only the latest unread message of highest available priority, plus nr of unread messages overall
			ReceivedMessage msg = getLastHighPriorityMsg();
			if (msg != null) 
				obj.put(String.valueOf(msg.getTimestamp()), getJSONObject(msg, locale));	
			obj.put("-1", getNrOfUnreadMsgs()); // nr of available unread messages
		}
		else {
			NavigableMap<Long,ReceivedMessage> map;
			if (statusStr == "ALL") {
				map = getMessages(startTime);
			} 
			else {
				map = getMessages(startTime, status);
			}
			logger.debug("Request for messages: {}, messages: {}",req, map);
			Iterator<Entry<Long,ReceivedMessage>> it = map.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Long,ReceivedMessage> entry = it.next();
				ReceivedMessage msg = entry.getValue();
				JSONObject msgObj = getJSONObject(msg, locale);
				if (msgObj == null) continue;	
				obj.put(String.valueOf(entry.getKey()), msgObj);
			}
		}
		resp.setContentType("application/json");
        resp.getWriter().write(obj.toString());
        resp.setStatus(200);
	}
	
	// change message status
	// required information: timestamp, oldStatus, newStatus
	// allowed status codes: SENT, READ, DELETED, for newStatus also REMOVED, if oldStatus is DELETED
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = req.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		String request = sb.toString();
//		System.out.println("  new POST request... " + request);
		try {
			JSONObject obj  =new JSONObject(request);
			String timeId = obj.getString("timestamp");
			String newStatus = obj.getString("newStatus");
			String oldStatus = obj.getString("oldStatus");
			if (oldStatus.equals(newStatus)) {
				resp.getWriter().write("Status unchanged.");
				resp.setStatus(200);
				return;
			}			
			MessageStatus statusOld = MessageStatus.valueOf(oldStatus);
			long time = Long.parseLong(timeId);
			ReceivedMessageImpl message;
			switch(statusOld) {
			case SENT:
				message = messages.unreadMessages.get(time);
				break;
			case READ:
				message = messages.readMessages.get(time);
				break;
			case DELETED:
				message = messages.deletedMessages.get(time);
				break;
			default:
				resp.setStatus(400); // bad request
				return;
			}
			if (message == null) {
				resp.setStatus(400); // bad request
				return;				
			}
			if (statusOld == MessageStatus.DELETED && newStatus.equals("REMOVED"))
				removeMessage(message);
			else {
				MessageStatus statusNew = MessageStatus.valueOf(newStatus);
				setMessageStatus(message, statusNew);
			}
			resp.setContentType("application/json");
			resp.getWriter().write("Status changed.");
			resp.setStatus(200);	
		} catch (Exception e) {
			resp.setContentType("application/json");
			e.printStackTrace(resp.getWriter());
			resp.setStatus(500);
		}
	}
	
	private static JSONObject getJSONObject(ReceivedMessage msg, OgemaLocale locale) {
		Message orig = msg.getOriginalMessage();
		JSONObject obj = new JSONObject();
		try {			
			obj.put("title", orig.title(locale));
			obj.put("msg", orig.message(locale));
			obj.put("prio", orig.priority().getPriority());
			obj.put("app", msg.getAppId().getIDString());
			String lk = orig.link();
			if (lk == null || lk.isEmpty()) 
				lk = "#";
			obj.put("link", lk);
		} catch (Exception e) {
			if (locale != OgemaLocale.ENGLISH) {
				return getJSONObject(msg, OgemaLocale.ENGLISH);
			}
			else return null;
		}
		return obj;
	}
	
	private NavigableMap<Long, ReceivedMessage> getMessages(long tm, MessageStatus status) {
		switch(status) {
		case SENT:
			return Collections.unmodifiableNavigableMap(messages.unreadMessages.tailMap(tm));
		case READ:
			return Collections.unmodifiableNavigableMap(messages.readMessages.tailMap(tm));
		case DELETED:
			return Collections.unmodifiableNavigableMap(messages.deletedMessages.tailMap(tm));
		default: 
			return Collections.emptyNavigableMap();
		}
	}
	
	private ReceivedMessage getLastHighPriorityMsg() {
		NavigableMap<Long,ReceivedMessageImpl> map =  messages.unreadMessages.descendingMap(); // start with latest entry
		ReceivedMessage lastM = null;
		int lastP = -1;
		Iterator<Entry<Long,ReceivedMessageImpl>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Long,ReceivedMessageImpl> entry = it.next();
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
	
	private final JSONObject unreadMsgObj = new JSONObject();
	
	private JSONObject getNrOfUnreadMsgs() {
		if (!unreadMsgObj.has("prio")) {
			unreadMsgObj.put("prio", -1);
		}
		unreadMsgObj.put("nrMsg",messages.unreadMessages.size());	
		return unreadMsgObj;
	}

	@Override
	public ReceivedMessage getMessage(long timestamp) {
		ReceivedMessage msg = null;
		msg = messages.unreadMessages.get(timestamp);
		if (msg != null) return msg;
		msg = messages.readMessages.get(timestamp);
		if (msg != null) return msg;
		msg = messages.deletedMessages.get(timestamp);
		return msg;
	}

	@Override
	public Map<String, MessageListener> getMessageListeners() {
		return new HashMap<String, MessageListener>(messages.listeners);
	}

	@Override
	public List<MessagingApp> getMessageSenders() {
		List<MessagingApp> ids = new ArrayList<>(messages.registeredMessageSenders.values());
		return ids;
	}

	@Descriptor("Get the list of configured receivers per messaging transport channel")
	public Map<String, List<String>> getReceivers(
			@Descriptor("Specify the channel id to show only the users for this medium")
			@Parameter(names= {"-c", "--channel"}, absentValue="")
			String channel
			) {
		Stream<MessageListener> listeners = messages.listeners.values().stream();
		if (!channel.isEmpty())
			listeners = listeners.filter(l -> channel.equalsIgnoreCase(l.getId()));
		return listeners
			.collect(Collectors.toMap(MessageListener::getId, MessageListener::getKnownUsers));
	}

	@Descriptor("Get the ids of all apps registered as message senders")
	public List<String> getMessagingApps() {
		return messages.registeredMessageSenders.values().stream()
				.map(MessagingApp::getMessagingId)
				.collect(Collectors.toList());
				
	}
	
	
	/**
	 * pass priority level null to stop forwarding 
	 */

	/* removed for testing, maybe its not important anymore
	@Override
	public void forwardMessages(String sender, MessageListener listener, Map<String,MessagePriority> recipients) {
		if (sender == null || listener == null) 
			throw new IllegalArgumentException("Sender and receiver must not be null");

		UserConfig uc;
		for(int i = 0 ; i < messages.forwardConfigList.size() ; i++) {
			if(messages.forwardConfigList.get(i).<MessagingService> getParent().<MessagingApp> getParent().appId().getValue().equals(sender)) {
				uc = messages.forwardConfigList.get(i);
			}
		}
		String receiver = listener.getId();
//		ForwardingConfiguration fwOld = configs.get(receiver);
		if (recipients == null || recipients.isEmpty()) {
			UserConfig uc = configs.remove(receiver);
			if (uc != null)
				uc.delete();
		}
		else {
			UserConfig uc = configs.get(receiver);
			if (uc == null) {
				uc = createNewConfigResource(sender,listener);//TODO
			}
			setRecipients(fw, recipients);
			fw.activate(true); // trigger listener ?
		}
		
	}
	
	private static void setRecipients(ForwardingConfigurationRes fw, Map<String,MessagePriority> recipients) {
		int sz = recipients.size();
		String[] recip = new String[sz];
		int[] minPrio = new int[sz];
		Iterator<Map.Entry<String, MessagePriority>> it = recipients.entrySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			Map.Entry<String, MessagePriority> entry = it.next();
			recip[count] = entry.getKey();
			minPrio[count] = entry.getValue().getPriority();
			count++;
		}
		fw.recipients().<StringArrayResource> create().setValues(recip);
		fw.minimumPriorities().<IntegerArrayResource> create().setValues(minPrio);
	}
	
	private ForwardingConfigurationRes createNewConfigResource(String sender, MessageListener listener) {
		String send = ResourceUtils.getValidResourceName(sender);
		String listenerId = ResourceUtils.getValidResourceName(listener.getId());
		int cnt = 0;
		while (configResources == null) {
			try {
				Thread.sleep(100);
				cnt++;
				if (cnt > 100) // 10s
					throw new IllegalStateException("Message Reader has not been initialized; this is a framework bug.");
			} catch (InterruptedException e) {}
		}
		ForwardingConfigurationRes resource = configResources.getSubResource(send + "__x__" + listenerId, ForwardingConfigurationRes.class).create();
		resource.sender().<StringResource> create().setValue(sender);
		resource.listener().<StringResource> create().setValue(listener.getId());
		return resource;
	}
	
	@Override
	public void resourceAvailable(UserConfig resource) {
		String listener = resource.<MessagingService> getParent().serviceId().getValue();
		String sender = resource.<MessagingService> getParent().<MessagingApp> getParent().appId().getValue();
		messages.forwardConfigList.add(resource);
	}

	@Override
	public void resourceUnavailable(UserConfig resource) {
		String listener = resource.<MessagingService> getParent().serviceId().getValue();
		String sender = resource.<MessagingService> getParent().<MessagingApp> getParent().appId().getValue();
		Map<String,UserConfig> configs = messages.forwardConfig.get(sender);
		if (configs != null)
			configs.remove(listener);
	}
	*/
	

}
