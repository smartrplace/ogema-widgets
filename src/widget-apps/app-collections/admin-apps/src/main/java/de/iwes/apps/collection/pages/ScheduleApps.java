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

import de.iwes.tools.apps.collections.api.ScheduleApp;
import de.iwes.tools.apps.collections.base.AppCollection;
import de.iwes.widgets.api.widgets.WidgetApp;

public class ScheduleApps extends AppCollection<ScheduleApp> {
	
	private final static Map<String,String> STATIC_APPS;
	
	static {
		Map<String,String> apps = new LinkedHashMap<>();
		apps.put("de.iwes.tools.schedule-viewer-basic", 
				"Time series visualization");	
		apps.put("de.iwes.tools.schedule-viewer", 
				"Time series visualization, with configuration resources");
		apps.put("org.ogema.apps.grafana-schedule-viewer", 
				"Schedule visualization based on the Grafana library");
		apps.put("org.ogema.apps.grafana-logging", 
				"Log data visualization based on the Grafana library");
		STATIC_APPS = Collections.unmodifiableMap(apps);
	}
	
	public ScheduleApps(ApplicationManager am, WidgetApp app, String pageUrl, boolean setAsStartPage) {
		super(am, app, pageUrl, setAsStartPage);
	}

	@Override
	protected String pageTitle() {
		return "Time series apps";
	}

	@Override
	protected Map<String, String> staticApps() {
		return STATIC_APPS;
	}
	
	
}
