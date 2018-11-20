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
