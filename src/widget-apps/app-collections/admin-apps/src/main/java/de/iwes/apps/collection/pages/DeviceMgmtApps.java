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

import de.iwes.tools.apps.collections.api.DeviceManagementApp;
import de.iwes.tools.apps.collections.base.AppCollection;
import de.iwes.widgets.api.widgets.WidgetApp;

public class DeviceMgmtApps extends AppCollection<DeviceManagementApp> {
	
	private final static Map<String,String> STATIC_APPS;
	
	static {
		Map<String,String> apps = new LinkedHashMap<>();
		apps.put("de.iwes.apps.room-link", 
				"Rooms management and device location");
		apps.put("de.iwes.apps.device-configuration", 
				"Device naming and rooms association");
		apps.put("de.iwes.widgets.datalog-sensact", 
				"Basic overview of sensors and actors in the system");
		apps.put("org.ogema.apps.basic-room-link", 
				"Simplified version of the room link app");
		apps.put("org.ogema.apps.basic-switch-gui", 
				"Displays all actors with a simple switch, or dimmable devices, and allows to change their settings");	
		apps.put("org.ogema.apps.simulation-gui", 
				"Start and stop simulations, view the different simulation providers");
		STATIC_APPS = Collections.unmodifiableMap(apps);
	}
	
	public DeviceMgmtApps(ApplicationManager am, WidgetApp app, String url, boolean startPage) {
		super(am, app, url, startPage);
	}

	@Override
	protected String pageTitle() {
		return "Device and room management";
	}

	@Override
	protected Map<String, String> staticApps() {
		return STATIC_APPS;
	}
	
}
