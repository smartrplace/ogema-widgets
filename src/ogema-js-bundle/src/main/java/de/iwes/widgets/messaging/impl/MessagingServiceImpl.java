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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.CommandProcessor;
import org.ogema.core.application.AppID;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.messaging.MessageReader;
import de.iwes.widgets.messaging.model.MessagingApp;
import de.iwes.widgets.messaging.model.UserConfig;
import java.io.IOException;
import java.nio.file.Paths;

@Service(Application.class) // MessagingService registered in start method
@Component
public class MessagingServiceImpl implements MessagingService, Application {
	
	private final static int MAX_SIZE_UNREAD = 10000;
	private final static int MAX_SIZE_READ = 5000;
	private final static int MAX_SIZE_DELETED = 1000;
	private final ExecutorService exec = Executors.newCachedThreadPool();
	private final ConcurrentMap<AppID,NavigableSet<Long>> lastMessages = new ConcurrentHashMap<AppID, NavigableSet<Long>>();
	private final static Logger logger = LoggerFactory.getLogger(MessagingService.class);
	// note: this path is shared between select-connector and messaging service, do not change
	private final static String ALL_APPS_PATH = "messagingApps/" + getValidVariableName(de.iwes.widgets.messaging.MessagingApp.ALL_APPS_IDENTIFIER);
	private volatile BundleContext ctx;
	private volatile ServiceRegistration<MessagingService> serviceReg;
	private volatile ServiceRegistration<MessageReader> readerReg;
	private volatile ApplicationManager appMan;
	private MessageReaderImpl reader;
	private final MessageLists messages = new MessageLists();
	
	@Activate
	protected void activate(BundleContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public void start(ApplicationManager appManager) {
		this.appMan = appManager;
		this.serviceReg = ctx.registerService(MessagingService.class, this, null);
		this.reader = new MessageReaderImpl(ctx, appManager, messages);
		final Dictionary<String, Object> props = new Hashtable<>();
		props.put(CommandProcessor.COMMAND_SCOPE, "msg");
		props.put(CommandProcessor.COMMAND_FUNCTION, new String[] {
				"getMessagingApps",
				"getReceivers"
		});
		this.readerReg = ctx.registerService(MessageReader.class, reader, props);
		try {
			messages.load(Paths.get("data/messages.bin"), appManager.getAppID());
		} catch (IOException | ClassNotFoundException ex) {
			logger.warn("reading stored messages failed", ex);
		}
	}
	
	@Override
	public void stop(AppStopReason reason) {
		this.appMan = null;
		try {
			logger.debug("storing messages...");
			messages.write(Paths.get("data/messages.bin"), OgemaLocale.ENGLISH, OgemaLocale.GERMAN);
		} catch (IOException ex) {
			logger.warn("storing messages failed", ex);
		}
		removeService();
	}
	
	@Deactivate
	protected void deactivate() {
		removeService();
		lastMessages.clear();
		messages.clear();
		this.ctx = null;
	}
	
	private void removeService() {
		final ServiceRegistration<MessagingService> sreg = this.serviceReg;
		final ServiceRegistration<MessageReader> rreg = this.readerReg;
		final MessageReaderImpl reader = this.reader;
		this.serviceReg = null;
		this.readerReg = null;
		this.reader = null;
		if (reader == null && sreg == null &&  rreg == null)
			return;
		// blocks sometimes if executed in the deactivate thread... unclear why
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (reader != null) {
					try {
						reader.close();
					} catch (Exception ignore) {}
				}
				if (rreg != null) {
					try {
						rreg.unregister();
					} catch (Exception ignore) {}
				}
				if (sreg != null) {
					try {
						sreg.unregister();
					} catch (Exception ignore) {}
				}
			}
		}, "messaging-service-shutdown").start();
	}
	
	@Override
	public void sendMessage(ApplicationManager am, Message message)	throws RejectedExecutionException, IllegalStateException {
		sendMessage(Objects.requireNonNull(am).getAppID(), message);
	}
	
	@Override
	public void sendMessage(final AppID appId, final Message message) throws RejectedExecutionException, IllegalStateException {
		Objects.requireNonNull(appId);
		Objects.requireNonNull(message);
//		Bundle bundle =  am.getAdministrationManager().getContextBundle(MessagingServiceImpl.class);
//		String appName;
//		try {
//			appName = bundle.getHeaders().get("Bundle-Name");
//		} catch (Exception e) {
//			appName = appId.getIDString();
//		}
//		String senderId = messages.registeredMessageSenders.get(appId);
		
		de.iwes.widgets.messaging.MessagingApp ma = messages.registeredMessageSenders.get(appId);
		if (ma == null) {
			throw new IllegalStateException("App has not been registered to send messages");
		}
		String senderId = ma.getMessagingId();
		if (messages.getUnreadCount() > MAX_SIZE_UNREAD)
			throw new RejectedExecutionException("Too many unread messages.");
		if (messages.getDeletedCount() > MAX_SIZE_DELETED) {  // "garbage collector"; 
			messages.trimDeleted(MAX_SIZE_DELETED);
		}
		if (messages.getReadCount() > MAX_SIZE_READ) {  // "garbage collector"; 
			messages.trimRead(MAX_SIZE_READ);
		}
		long tm = appMan.getFrameworkTime();
		lastMessages.putIfAbsent(appId, new TreeSet<Long>());
		NavigableSet<Long> lastM = lastMessages.get(appId);
		// ensure the app is not sending too many messages
		synchronized(lastM) {
			if (lastM.ceiling(tm - 20*1000) == null) {  // no other messages in the last 20s -> fine
				lastM.clear();
			}
			else if (lastM.tailSet(tm - 30*1000).size() > 10 || lastM.tailSet(tm - 5*60*1000).size() > 25) {  // too many messages!
				logger.info("Too many messages from app {}", appId.getIDString());
				throw new RejectedExecutionException("Too many messages from this application. AppID: " + appId);
			}
			lastM.add(tm);
		}
		String appName = ma.getMessagingId();
		ReceivedMessageImpl msg = new ReceivedMessageImpl(message, tm, appId, appName);
		messages.addUnread(tm, msg);
		// listener callbacks
		
		MessagingApp allApps = null;
		try {
			allApps = appMan.getResourceAccess().getResource(ALL_APPS_PATH);
			if (allApps != null && !allApps.isActive())
				allApps = null;
		} catch (Exception e) {}

		
		List<MessagingApp> apps = appMan.getResourceAccess().getResources(MessagingApp.class); 
		//
		MessagingApp sendingApp = null;
		for(MessagingApp app : apps) {
			if(app.appId().getValue().equals(senderId)) {
				sendingApp = app;
				break;
			}
		}
		// FIXME what if only allApps config exists??
		// resource must be created by external component (e.g. message-forwarding)
		if (sendingApp == null) {
			logger.info("No forwarding configuration for app {}",senderId);
			return;
		}
		if (sendingApp.active().isActive() && !sendingApp.active().getValue()) {
			logger.info("Message received from deactivated messaging app {}", senderId);
			return;
		}
		forwardMessage(msg, sendingApp, allApps);
		
		// remove listeners that caused an exception (e.g. because app has been stopped in the meantime)
//		Iterator<String> failedIt = failedListeners.iterator();
//		while (failedIt.hasNext()) {
//			String appIdLoc = failedIt.next();
//			messages.listeners.remove(appIdLoc);
//		}
		logger.info("New message registered from app {}; nr of messages: {}" , appId.getIDString(), messages.getUnreadCount());
	}
	
	private void forwardMessage(ReceivedMessageImpl msg, MessagingApp app, MessagingApp allAppsConfig) {
		
		List<de.iwes.widgets.messaging.model.MessagingService> services = app.services().getAllElements();
		MessageListener listener;
		for(de.iwes.widgets.messaging.model.MessagingService service : services) {
			List<UserConfig> users = service.users().getAllElements();
			List<String> userNames = new ArrayList<String>();
			listener = messages.listeners.get(service.serviceId().getValue());
			if (listener == null) {
				logger.warn("Listener " + service.serviceId().getValue() + " not found.");
				continue;
			}
			for(UserConfig uc : users) {
				if (logger.isTraceEnabled()) {
					logger.trace("Checking forwarding configuration for " + service.serviceId().getValue() + " : Message Priority " + msg.getOriginalMessage().priority().getPriority() + ", " + 
							uc.userName().getValue() + ", priority " + uc.priority().getValue());
				}
				if(msg.getOriginalMessage().priority().getPriority() >= uc.priority().getValue()) {
//					System.out.println(uc.userName().getValue() + " added to recipients");
					userNames.add(uc.userName().getValue());
				}
			}
			if (userNames.isEmpty())
				continue;
			logger.debug("Submitting message forwarding task for app {}, users {}", service.serviceId().getValue(), userNames);
			exec.submit(new MessageThread(msg, userNames, listener));
		}
		if (allAppsConfig == null)
			return;
		forwardMessage(msg, allAppsConfig, null); // TODO exclude those service/user combinations which already have received the message
	}
	
	private static class MessageThread implements Callable<Void> {
		
		private final ReceivedMessageImpl msg;
		private final List<String> userNames;
		private final MessageListener listener;
		
		public MessageThread(ReceivedMessageImpl msg, List<String> userNames, MessageListener listener) {
			this.msg = msg;
			this.userNames = userNames;
			this.listener = listener;
		}
		
		@Override
		public Void call() throws Exception {
			try {
				listener.newMessageAvailable(msg,userNames);
			} catch (Exception e) {
				MessagingServiceImpl.logger.error("Error forwarding message", e);
			}
			return null;
		}
		
	}
	
	@Override
	public void registerMessagingApp(AppID appId, String humanReadableId) throws IllegalArgumentException {
		registerMessagingApp(appId, humanReadableId, null);
	}
	@Override
	public void registerMessagingApp(AppID appId, String humanReadableId, String name) throws IllegalArgumentException {
		Objects.requireNonNull(appId);
		Objects.requireNonNull(humanReadableId);
		if (humanReadableId.trim().length() < 5)
			throw new IllegalArgumentException("App id too short: " + humanReadableId + ": Need at least five characters.");
		if (getValidVariableName(humanReadableId).equals(getValidVariableName(de.iwes.widgets.messaging.MessagingApp.ALL_APPS_IDENTIFIER)))
			throw new IllegalArgumentException("App id " + humanReadableId + " not admissible");
		// FIXME we should rather check for identity at the level of valid ids
		if (messages.registeredMessageSenders.containsKey(appId))
			throw new IllegalArgumentException("App with appId " + appId.getIDString() + " already registered");
		MessagingAppImpl mai = new MessagingAppImpl(appId, humanReadableId, name);
		messages.registeredMessageSenders.put(appId,mai);
	}

	@Override
	public void unregisterMessagingApp(AppID appId) {
		Objects.requireNonNull(appId);
		messages.registeredMessageSenders.remove(appId);
	}
	
	private static String getValidVariableName(String variableName) {
		if (variableName == null || variableName.isEmpty()) 
			return variableName; 
		char[] str = variableName.toCharArray();
		for (int i =0;i<str.length;i++) { 
			if (!Character.isJavaIdentifierPart(str[i]))
				str[i] = '_';
		}
		String out = new String(str);
		if (!Character.isJavaIdentifierStart(str[0]))
			out = "_" + out;
		return out;
	}


}
