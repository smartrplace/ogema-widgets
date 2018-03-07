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

package org.ogema.messaging.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.messaging.configuration.localisation.SelectConnectorDictionary;
import org.ogema.messaging.configuration.localisation.SelectConnectorDictionary_de;
import org.ogema.messaging.configuration.localisation.SelectConnectorDictionary_en;
import org.ogema.messaging.configuration.templates.AppSnippet;
import org.ogema.tools.resource.util.ResourceUtils;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.accordion.AccordionItem;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.messaging.MessageReader;
import de.iwes.widgets.messaging.model.MessagingApp;
import de.iwes.widgets.messaging.model.MessagingService;
import de.iwes.widgets.messaging.model.UserConfig;


@Component(specVersion = "1.2")
@Service(Application.class)
public class SelectConnector implements Application {

	private final static long GARBAGE_COLLECTOR_UPDATE_INTERVAL = 30 * 60 * 1000; // every 30 min
	private OgemaLogger logger;
	private WidgetApp wApp;
	private WidgetPage<SelectConnectorDictionary> page;
	private final static AllMessagingApps allApps = new AllMessagingApps(); 
	private final Set<String> users = new HashSet<>();
	private final Map<String,MessageListener> listeners = new HashMap<>();

	private ResourceList<MessagingApp> appList;

	@Reference
	private OgemaGuiService guiService;
	
	@Reference 
	private MessageReader messageReader;
	
//	@Reference(bind="addMessageListener",unbind="removeMessageListener")

	
	@Override
	public void stop(AppStopReason reason) {
		if (wApp != null){
			wApp.close();
		}
		wApp = null;
		page = null;
		logger = null;
		appList = null;
		// there is a potential risk of concurrentmodification for the lists below...
		// but since the widget app has been unregistered already, the lists should not be modified any more
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		users.clear();
		listeners.clear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(ApplicationManager appManager) {
		this.logger = appManager.getLogger();
        this.logger.debug("{} started", getClass().getName());
		this.wApp = guiService.createWidgetApp("/de/iwes/ogema/apps/select-connector", appManager);
		this.page = wApp.createStartPage();
		page.registerLocalisation(SelectConnectorDictionary_de.class).registerLocalisation(SelectConnectorDictionary_en.class);
		this.appList = appManager.getResourceManagement().createResource("messagingApps", ResourceList.class);
		appList.setElementType(MessagingApp.class);
		
		Header header =  new Header(page, "header", "Message forwarding configurations") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(SelectConnector.this.page.getDictionary(req).header(),req);
			}
			
		};
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		page.append(header).linebreak();
		
	    Alert info = new Alert(page, "description","Explanation") {

			private static final long serialVersionUID = 1L;

			@Override
	    	public void onGET(OgemaHttpRequest req) {
	    		setHtml(SelectConnector.this.page.getDictionary(req).description(), req);
	    		allowDismiss(true, req);
	    		autoDismiss(-1, req);
	    	}
	    	
	    };
	    page.append(info).linebreak();
	    info.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
	    info.setDefaultVisibility(true);
	    
		final Accordion appAccordion = new Accordion(page, "appAccordion", true){

			private static final long serialVersionUID = 1L;
			private AtomicLong lastUpdate = new AtomicLong(0);
			private AtomicBoolean running = new AtomicBoolean(false);

			@Override
			public void onGET(OgemaHttpRequest req) {
				// avoid too frequent updates
				if (System.currentTimeMillis() - lastUpdate.get() < 5000)
					return;
				if (running.getAndSet(true))
					return;
				// need to write lock during app refresh, so that readers must wait for it to finish!
				writeLock(req);
				try {
					refreshMessagingApps(this, req);
				} finally {
					lastUpdate.set(System.currentTimeMillis());
					running.set(false);
					writeUnlock(req);
				}
			}
		};
		appAccordion.addDefaultStyle(AccordionData.BOOTSTRAP_GREEN);
		page.append(appAccordion);
		appManager.createTimer(GARBAGE_COLLECTOR_UPDATE_INTERVAL, garbageCollector);
		
	}
	
	private static final boolean contains(List<de.iwes.widgets.messaging.MessagingApp> list, MessagingApp app) {
		for (de.iwes.widgets.messaging.MessagingApp entry : list) {
			if (entry.getMessagingId().equals(app.appId().getValue())) {
				return true;
			}
		}
		return false;
 	}
	
	public void refreshMessagingApps(Accordion appAccordion, OgemaHttpRequest req) {
		Map<String,MessageListener> newListeners = new HashMap<>();
		Set<String> listenersToBeRemoved = new HashSet<>();
		Set<String> newUsers = new HashSet<>();
		Set<String> usersToBeRemoved= new HashSet<>();
		
		Map<String, MessageListener> localListeners = messageReader.getMessageListeners();
		Set<String> localUsers = new HashSet<>();
		Iterator<Map.Entry<String,MessageListener>> it = listeners.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,MessageListener> entry = it.next();
			String key = entry.getKey();
			if (!localListeners.containsKey(key)) {
				listenersToBeRemoved.add(key);
				it.remove();
			}
		}
		for (Map.Entry<String, MessageListener> entry : localListeners.entrySet()) {
			if (!listeners.containsKey(entry.getKey())) {
				newListeners.put(entry.getKey(),entry.getValue());
				listeners.put(entry.getKey(),entry.getValue());
			}
			try {
				for (String user : entry.getValue().getKnownUsers()) {
					localUsers.add(user);
					if (!users.contains(user)) {
						users.add(user);
						newUsers.add(user);
					}
						
				}	
			} catch (Throwable e) {
				LoggerFactory.getLogger(Util.class).error("",e);
			}
		}
		Iterator<String> userIt = users.iterator();
		while (userIt.hasNext()) {
			String user = userIt.next();
			if (!localUsers.contains(user)) {
				userIt.remove();
				usersToBeRemoved.add(user);
			}
		}
		boolean changedAtAll = !newUsers.isEmpty() || !newListeners.isEmpty() || !listenersToBeRemoved.isEmpty() || !usersToBeRemoved.isEmpty();
		List<de.iwes.widgets.messaging.MessagingApp> appIds = new ArrayList<>();
		appIds.add(allApps);	
		appIds.addAll(messageReader.getMessageSenders());
		List<MessagingApp> apps = appList.getAllElements();
		for(int i = 0 ; i < apps.size() ; i++) {
			MessagingApp app = apps.get(i);
			String appId = app.appId().getValue();
//			if(appIds.contains(appId)) {
			if (contains(appIds, app)) {
				continue;
			} else {
				app.delete();
				try { 
					appAccordion.removeItem(appId, req);
				} catch (Exception e) {}
			}
		}
//		List<MessagingApp> oldApps = appList.getAllElements();
		for(de.iwes.widgets.messaging.MessagingApp app : appIds) {
			String appId = app.getMessagingId();
			String resourceName = ResourceUtils.getValidResourceName(appId);
			MessagingApp messagingApp = appList.getSubResource(resourceName,MessagingApp.class).create();
			StringResource id = messagingApp.appId().create();
			id.setValue(appId);
			if (!messagingApp.active().isActive()) {
				messagingApp.active().<BooleanResource> create().setValue(true);
			}
//			appendServiceResources(messagingApp, messageListeners);  // updates or creates service resource list
//			if (containsAppId(oldApps, appId)) // it is not sufficient to check the resources; on unclean start they are available, but the accordion item no
//				continue;
			AccordionItem item = appAccordion.getItem(resourceName, req);
			boolean isNew = item == null;
			if (isNew) {
				AppSnippet appSnippet = new AppSnippet(page, true, messagingApp, messageReader, app);
				appAccordion.addItem(resourceName, appSnippet, req);
				item = appAccordion.getItem(resourceName, req);
			} 
			boolean tableRefreshed = false;
			AppSnippet snippet = (AppSnippet) item.getWidget();
			if (isNew || changedAtAll) {
				// check... or do we need to do this even for new items?
				tableRefreshed = snippet.update(newUsers, usersToBeRemoved, newListeners, listenersToBeRemoved);
			}
			if (!tableRefreshed) {
				Map<String,MessageListener> unchangedListeners = new HashMap<>(listeners);
				for (String newListener: newListeners.keySet()) {
					unchangedListeners.remove(newListener);
				}
				Set<String> unchangedUsers  = new HashSet<>(users);
				unchangedUsers.removeAll(newUsers);
				snippet.updateDropdowns(unchangedListeners, unchangedUsers);
			}
			
		}

	}
	
	// it is rather difficult to clean up UserConfig resources for services which have lost a user
	// hence we simply do that periodically
	private final TimerListener garbageCollector = new TimerListener() {
		
		@Override
		public void timerElapsed(Timer timer) {
			logger.debug("Running select connector garbage collector, to identify stale user forwarding configurations");
			Map<String, MessageListener> listeners = messageReader.getMessageListeners();
			List<MessagingApp> apps = appList.getAllElements();
			for (MessagingApp app: apps) {
				if (!app.services().isActive())
					continue;
				for (MessagingService ml: app.services().getAllElements()) {
					MessageListener actualListener = listeners.get(ml.serviceId().getValue());
					if (actualListener == null) {
						ml.deactivate(true);
						continue;
					}
					if (!ml.users().isActive())
						continue;
					try {
						List<String> users = actualListener.getKnownUsers();
						for (UserConfig uc : ml.users().getAllElements()) {
							try {
								if (!users.contains(uc.userName().getValue())) {
									logger.info("Found forwarding configuration for inexistent user " + uc.userName().getValue());
									uc.delete();
								}
							} catch (Exception e) {
								logger.info("Could not determine whether user config is real, removing it: " +uc,e);
								uc.delete();
							}
								
						}
					} catch (Throwable e) {
						LoggerFactory.getLogger(getClass()).error("",e);
					} 
				}
			}
		}
	};
	
}
