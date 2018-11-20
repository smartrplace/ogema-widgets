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
package de.iwes.apps.collection.pages;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.ogema.core.application.ApplicationManager;

import de.iwes.tools.apps.collections.api.MessagingApp;
import de.iwes.tools.apps.collections.base.AppCollection;
import de.iwes.widgets.api.widgets.WidgetApp;

public class MessagingApps extends AppCollection<MessagingApp> {
	
	private final static Map<String,String> STATIC_APPS;
	
	static {
		Map<String,String> apps = new LinkedHashMap<>();
		apps.put("org.ogema.messaging.message-forwarding", 
				"Configure which messages from the OGEMA system are forwarded to your email, SMS or messenger accounts");
		apps.put("org.ogema.messaging.message-settings", 
				"Configuration of Email, SMS and XMPP accounts");
		apps.put("org.ogema.messaging.message-reader", // in ogema-apps repository
				"Display messages from the installed apps");
		apps.put("org.ogema.messaging.test", 
				"Developer tool for testing the message forwarding configuration");
		STATIC_APPS = Collections.unmodifiableMap(apps);
	}
	
	public MessagingApps(ApplicationManager am, WidgetApp app, String url, boolean startPage) {
		super(am, app, url, startPage);
	}

	@Override
	protected String pageTitle() {
		return "Messaging apps";
	}

	@Override
	protected Map<String, String> staticApps() {
		return STATIC_APPS;
	}
	
}
