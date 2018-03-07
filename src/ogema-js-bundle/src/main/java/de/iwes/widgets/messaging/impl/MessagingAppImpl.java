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
	
	MessagingAppImpl(final AppID appId, final String id) {
//		this.appId = appId;
		this.headers = AccessController.doPrivileged(new PrivilegedAction<Dictionary<String, String>>() {

			@Override
			public Dictionary<String, String> run() {
				return appId.getBundle().getHeaders();
			}
		});
		this.messagingId = id;
	}

	@Override
	public String getName() {
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
