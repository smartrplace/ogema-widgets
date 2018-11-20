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
package org.ogema.messaging.basic.services.config;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.messaging.basic.services.config.localisation.MessageSettingsDictionary;
import org.ogema.messaging.basic.services.config.localisation.MessageSettingsDictionary_de;
import org.ogema.messaging.basic.services.config.localisation.MessageSettingsDictionary_en;
import org.ogema.messaging.basic.services.config.model.EmailConfiguration;
import org.ogema.messaging.basic.services.config.model.ReceiverConfiguration;
import org.ogema.messaging.basic.services.config.model.SmsConfiguration;
import org.ogema.messaging.basic.services.config.model.XmppConfiguration;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;

@Component(specVersion = "1.2")
@Service(Application.class)
public class MessageSettings implements Application {

	private OgemaLogger logger;
	private WidgetApp wApp;
	private ResourceAccess resAcc;
	private SenderPageBuilder senderPageBuilder;
	private ReceiverPageBuilder receiverPageBuilder;

	@Reference
	private OgemaGuiService guiService;

	@Override
	public void start(ApplicationManager appManager) {
		this.resAcc = appManager.getResourceAccess();
		this.logger = appManager.getLogger();
		logger.debug("{} started", getClass().getName());

		wApp = guiService.createWidgetApp("/de/iwes/ogema/apps/messageSettings", appManager);
		WidgetPage<MessageSettingsDictionary> senderPage = wApp.createWidgetPage("sender.html");
		senderPage.registerLocalisation(MessageSettingsDictionary_de.class).registerLocalisation(MessageSettingsDictionary_en.class);
		senderPageBuilder = new SenderPageBuilder(senderPage, appManager);

		WidgetPage<MessageSettingsDictionary> receiverPage = wApp.createStartPage();
		receiverPage.registerLocalisation(MessageSettingsDictionary_de.class).registerLocalisation(MessageSettingsDictionary_en.class);
		receiverPageBuilder = new ReceiverPageBuilder(receiverPage, appManager);

		resAcc.addResourceDemand(EmailConfiguration.class, senderPageBuilder.getEmailListener());
		resAcc.addResourceDemand(SmsConfiguration.class, senderPageBuilder.getSmsListener());
		resAcc.addResourceDemand(XmppConfiguration.class, senderPageBuilder.getXmppListener());
		resAcc.addResourceDemand(ReceiverConfiguration.class, receiverPageBuilder);

		NavigationMenu nm = new NavigationMenu(" Select page");
		nm.addEntry("Edit senders", senderPage);
		nm.addEntry("Edit receivers", receiverPage);

		MenuConfiguration mc = receiverPage.getMenuConfiguration();
		mc.setCustomNavigation(nm);
		mc = senderPage.getMenuConfiguration();
		mc.setCustomNavigation(nm);
	}

	@Override
	public void stop(AppStopReason reason) {
		if (wApp != null) {
			wApp.close();
		}
		if (resAcc != null && senderPageBuilder != null) {
			resAcc.removeResourceDemand(EmailConfiguration.class, senderPageBuilder.getEmailListener());
			resAcc.removeResourceDemand(SmsConfiguration.class, senderPageBuilder.getSmsListener());
			resAcc.removeResourceDemand(XmppConfiguration.class, senderPageBuilder.getXmppListener());
		}
		if (resAcc != null && receiverPageBuilder != null) {
			resAcc.removeResourceDemand(ReceiverConfiguration.class, receiverPageBuilder);
		}
		logger = null;
		wApp = null;
		resAcc = null;
		receiverPageBuilder = null;
		senderPageBuilder = null;
	}

}
