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
