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
import de.iwes.widgets.messaging.MessageReader;

@Component(specVersion = "1.2", immediate=true)
@Service(Application.class)
public class MessageReaderApp implements Application{

	private WidgetAppImpl wapp;
	private WidgetPageImpl<MessagesDictionary> page;
	private final String htmlPath = "/de/iwes/ogema/apps/message/reader";
	
	@Reference
	OgemaGuiService widgetService;
	
//	@Reference
//	MessagingService ms;
	
	@Reference
	MessageReader mr;
	
	@Override
	public void start(ApplicationManager am) {
		this.wapp = new WidgetAppImpl(htmlPath, widgetService, am);
		this.page = new WidgetPageImpl<MessagesDictionary>(wapp,true);
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