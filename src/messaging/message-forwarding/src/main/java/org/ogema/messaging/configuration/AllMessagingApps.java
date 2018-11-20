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
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public String getMessagingId() {
		return ALL_APPS_IDENTIFIER;
	}

}