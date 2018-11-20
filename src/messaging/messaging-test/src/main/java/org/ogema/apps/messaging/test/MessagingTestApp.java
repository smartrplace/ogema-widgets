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
package org.ogema.apps.messaging.test;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

@Component(specVersion = "1.2", immediate=true)
@Service(Application.class)
public class MessagingTestApp implements Application {

	private ApplicationManager am;
	private OgemaLogger logger;
	private WidgetApp wApp;
//	private Timer timer;
//	private int counter = 0;
	
	private MessagingService ms;
	
	@Reference
	private OgemaGuiService guiService;
	
	@Override
	public void start(ApplicationManager am) {
		this.am = am;
		this.logger = am.getLogger();
		ms = guiService.getMessagingService();
		ms.registerMessagingApp(am.getAppID(), "Messaging test app");
		this.wApp = guiService.createWidgetApp("/de/iwes/messaging/test", am);
		WidgetPage<?> page = wApp.createStartPage();
		PageBuilder pb= new PageBuilder(page, ms, am);
//		timer = am.createTimer(20000L, this);
	}

	@Override
	public void stop(AppStopReason reason) {
		if (wApp != null)
			wApp.close();
		wApp = null;
		if (ms != null)
			ms.unregisterMessagingApp(am.getAppID());
		if (logger!= null)
			logger.info("{} being stopped.", getClass().getName());
		logger = null;
		am = null;
		ms = null;
//		timer.destroy();
	}

//	@Override
//	public void timerElapsed(Timer timer) {
//		counter++;
//		Message msg = new MessageImpl(counter);
//		logger.debug("New message being sent!!!");
//		ms.sendMessage(am, msg);
//		// FIXME
//		timer.destroy();
//	}

//	public class MessageImpl implements Message {
//
//		private final int id;
//		
//		public MessageImpl(int id) {
//			this.id = id;
//		}
//		
//		@Override
//		public String title(OgemaLocale locale) {
//			if (locale != null && locale.equals(OgemaLocale.GERMAN))
//				return "Neue Nachricht, id: " + id;
//			else if (locale != null &&locale.equals(OgemaLocale.FRENCH)) 
//				return "Nouveau message, id: " + id;
//			else
//				return "New message, id: " + id;	
//		}
//		
//		@Override
//		public MessagePriority priority() {
//			switch(id % 3) {
//			case 0: 
//				return MessagePriority.HIGH;
//			case 1: 
//				return MessagePriority.MEDIUM;
//			default:
//				return MessagePriority.LOW;
//			}
//		}
//		
//		@Override
//		public String message(OgemaLocale locale) {
//			if (locale != null && locale.equals(OgemaLocale.GERMAN))
//				return "Dies ist eine zuf�llig erzeugte Nachricht. " + id;
//			else if (locale != null && locale.equals(OgemaLocale.FRENCH)) 
//				return "C'est un message cr�e par hasard. " + id;
//			else
//				return "This is some random message. " + id;	
//		}
//
//		@Override
//		public String link() {
//			return "/ogema/test/index.html";
//		}
//	}
	

}