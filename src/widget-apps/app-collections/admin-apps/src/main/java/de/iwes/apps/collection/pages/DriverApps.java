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

import de.iwes.tools.apps.collections.api.DriverApp;
import de.iwes.tools.apps.collections.base.AppCollection;
import de.iwes.widgets.api.widgets.WidgetApp;

public class DriverApps extends AppCollection<DriverApp> {
	
	private final static Map<String,String> STATIC_APPS;
	
	static {
		Map<String,String> apps = new LinkedHashMap<>();
		apps.put("org.ogema.apps.device-configurator", 
				"Configure drivers available via the device configurator");
		apps.put("de.iwes.ogema.remote-rest-configurator", 
				"Configuration page for the RemoteRESTConnector - driver that connects to other OGEMA gateways via their REST interface");
		apps.put("org.ogema.drivers.modbus-debugger", // in ogema-apps repository
				"Configurations for the Modbus TCP driver (resource-based)");
		apps.put("org.ogema.drivers.knx-js-gui", 
				"Experimental GUI for the KNX driver");
		apps.put("org.ogema.apps.simulation-gui", 
				"Start and stop simulations, view the different simulation providers");
		STATIC_APPS = Collections.unmodifiableMap(apps);
	}
	
	public DriverApps(ApplicationManager am, WidgetApp app, String url, boolean startPage) {
		super(am, app, url, startPage);
	}

	@Override
	protected String pageTitle() {
		return "Driver configuration pages";
	}

	@Override
	protected Map<String, String> staticApps() {
		return STATIC_APPS;
	}
	
}
