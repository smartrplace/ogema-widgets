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
package org.ogema.apps.message.reader;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.apps.message.reader.dictionary.MessagesDictionary;
import org.ogema.apps.message.reader.dictionary.MessagesDictionary_de;
import org.ogema.apps.message.reader.dictionary.MessagesDictionary_en;
import org.ogema.apps.message.reader.dictionary.MessagesDictionary_fr;
import org.ogema.apps.message.reader.gui.PageBuilder;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.extended.WidgetAppImpl;
import de.iwes.widgets.api.extended.WidgetPageImpl;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.messaging.MessageReader;

@Component(specVersion = "1.2", immediate=true)
@Service(Application.class)
public class MessageReaderApp implements Application{

	private WidgetApp wapp;
	private WidgetPage<MessagesDictionary> page;
	private final String htmlPath = "/de/iwes/ogema/apps/message/reader";
	
	@Reference
	OgemaGuiService widgetService;
	
//	@Reference
//	MessagingService ms;
	
	@Reference
	MessageReader mr;
	
	@Override
	public void start(ApplicationManager am) {
		this.wapp = widgetService.createWidgetApp(htmlPath, am);
		this.page = wapp.createStartPage();
		page.setTitle("OGEMA message reader");
		page.registerLocalisation(MessagesDictionary_en.class).registerLocalisation(MessagesDictionary_de.class).registerLocalisation(MessagesDictionary_fr.class);
		page.getMenuConfiguration().setShowMessages(false);
		PageBuilder pb = new PageBuilder(page, mr);
	}

	@Override
	public void stop(AppStopReason reason) {
		if (wapp != null)
			wapp.close();
		wapp = null;
		page = null;
	}
	

}