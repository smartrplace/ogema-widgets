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
