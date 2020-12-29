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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;

import org.ogema.core.application.AppID;

import de.iwes.widgets.messaging.MessagingApp;

class MessagingAppImpl implements MessagingApp {

//	private final AppID appId;
	private final Dictionary<String, String> headers;
	private final String messagingId;
	private final String name;
	
	MessagingAppImpl(final AppID appId, final String id) {
		this(appId, id, null);
	}
	MessagingAppImpl(final AppID appId, final String id, String name) {
//		this.appId = appId;
		this.headers = AccessController.doPrivileged(new PrivilegedAction<Dictionary<String, String>>() {

			@Override
			public Dictionary<String, String> run() {
				return appId.getBundle().getHeaders();
			}
		});
		this.messagingId = id;
		this.name = name;
	}

	@Override
	public String getName() {
		if(this.name != null)
			return this.name;
		return headers.get("Bundle-Name");
	}

	@Override
	public String getDescription() {
		return headers.get("Bundle-Description");
	}

	@Override
	public String getBundleSymbolicName() {
		return headers.get("Bundle-SymbolicName");
	}

	@Override
	public String getVersion() {
		return headers.get("Bundle-Version");
	}

	@Override
	public String getMessagingId() {
		return messagingId;
	}
	
}
