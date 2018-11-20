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
/**
 * Copyright 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IEE
 *
 * All Rights reserved
 */
package de.iwes.widgets.messaging.event.converter;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ogema.core.application.AppID;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

@Component(
		service= {Application.class, EventHandler.class},
		property=EventConstants.EVENT_TOPIC + "=ogema/*"
)
/**
 * Experimental converter: receives OSGi events with topic "ogema/*", and
 * forwards them as a user message.
 */
public class EventConverter implements EventHandler, Application {

	@Reference
	private MessagingService messaging;
	
	private volatile AppID appId; 
	
	private final ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
	
	@Override
	public void handleEvent(final Event event) {
		final AppID appID = this.appId;
		if (appID != null)
			send(appID, event); 
		else
			queue.add(event);
	}

	@Override
	public void start(ApplicationManager appManager) {
		final AppID appId = appManager.getAppID();
		this.appId = appId;
		messaging.registerMessagingApp(appId, "OGEMA Events");
		drainQueue(appId);
	}

	@Override
	public void stop(AppStopReason reason) {
		final AppID appId = this.appId;
		this.appId = null;
		try { // the app id might still be in use for sending messages, avoid race condition
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (appId != null)
			messaging.unregisterMessagingApp(appId);
	} 
	
	private void drainQueue(final AppID appId) {
		for (Event event = queue.poll(); event != null; event = queue.poll()) {
			send(appId, event);
		}
	}
	
	private void send(final AppID appId, final Event event) {
		messaging.sendMessage(appId, new EventMessage(event));
	}
	
	private static class EventMessage implements Message {
	
		private final Event event;
		
		EventMessage(Event event) {
			this.event = event;
		}

		@Override
		public String link() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String message(OgemaLocale arg0) {
			return Objects.toString(event.getProperty(EventConstants.MESSAGE));
		}

		@Override
		public MessagePriority priority() {
			return MessagePriority.LOW; // TODO
		}

		@Override
		public String title(OgemaLocale arg0) {
			final String topic = event.getTopic();
			final Object title = event.getProperty("title");
			if (title instanceof String)
				return topic + ": " + title;
			return topic;
		}
		
		@Override
		public String toString() {
			return "EventMessage[" + title(OgemaLocale.ENGLISH) + "]";
		}
		
	}

}
