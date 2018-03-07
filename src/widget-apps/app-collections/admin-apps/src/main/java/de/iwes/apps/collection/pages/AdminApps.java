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

import de.iwes.tools.apps.collections.api.AdminApp;
import de.iwes.tools.apps.collections.base.AppCollection;
import de.iwes.widgets.api.widgets.WidgetApp;

public class AdminApps extends AppCollection<AdminApp> {
	
	private final static Map<String,String> STATIC_APPS;
	
	static {
		Map<String,String> apps = new LinkedHashMap<>();
		apps.put("org.ogema.ref-impl.framework-administration", 
				"Basic framework administration, incl. Resource view and user administration");
		apps.put("org.ogema.ref-impl.security-gui", 
				"Install apps and edit permissions");
		apps.put("de.iwes.tools.resource-tree-manipulator", 
				"Manipulate the Resource database");
		apps.put("org.ogema.apps.logging-app", 
				"Configure resource value logging");
		apps.put("de.iwes.tools.schedule-viewer-basic", 
				"Time series visualization");	
		apps.put("org.ogema.ref-impl.rest", 
				"REST interface debugging tool");
		apps.put("org.ogema.tools.pattern-debugger", 
				"View all pattern listener requests and the complete and incomplete matches in the resource graph");
		apps.put("de.iwes.widgets.datalog-resadmin-v2", 
				"Download and configure backups, replay resources from xml or json files, etc.");	
		apps.put("org.ogema.apps.graph-generator", 
				"Resource graph visualization; does not scale very well");	
		STATIC_APPS = Collections.unmodifiableMap(apps);
	}
	
	public AdminApps(ApplicationManager am, WidgetApp app, String url, boolean startPage) {
		super(am, app, url, startPage);
	}

	@Override
	protected String pageTitle() {
		return "Framework administration apps";
	}

	@Override
	protected Map<String, String> staticApps() {
		return STATIC_APPS;
	}
	
}
