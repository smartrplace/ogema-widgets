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

package org.ogema.messaging.configuration;

import de.iwes.widgets.messaging.MessagingApp;

public class AllMessagingApps implements MessagingApp {
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getDescription() {
		return "A proxy for all applications. Forwarding configurations for this app "
				+ "mean that messages from all apps will be forwarded to the configured address";
	}

	@Override
	public String getBundleSymbolicName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessagingId() {
		return ALL_APPS_IDENTIFIER;
	}

}