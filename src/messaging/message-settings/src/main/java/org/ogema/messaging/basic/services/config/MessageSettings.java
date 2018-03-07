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
		if (wApp != null){
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
