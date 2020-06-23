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
package org.ogema.messaging.configuration;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.messaging.MessageReader;
import de.iwes.widgets.messaging.model.MessagingApp;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=/de/iwes/ogema/apps/select-connector", 
				LazyWidgetPage.RELATIVE_URL + "=index.html",
				LazyWidgetPage.START_PAGE + "=true",
				LazyWidgetPage.MENU_ENTRY + "=Message forwarding"
		}
)
public class SelectConnector implements LazyWidgetPage {

	@Reference
	private MessageReader reader;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		final ResourceList<MessagingApp> appList = appMan.getResourceManagement().createResource("messagingApps", ResourceList.class);
		appList.setElementType(MessagingApp.class);
		new PageInit((WidgetPage) page, appMan, appList, reader);

		if(Boolean.getBoolean("org.ogema.messaging.basic.services.config.fixconfigenglish")) {
			MenuConfiguration mc = page.getMenuConfiguration();
			mc.setLanguageSelectionVisible(false);
			mc.setNavigationVisible(false); 					
		}
	}
	
}
