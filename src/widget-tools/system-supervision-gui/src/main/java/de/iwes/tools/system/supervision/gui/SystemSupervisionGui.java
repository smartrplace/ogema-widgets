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
package de.iwes.tools.system.supervision.gui;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.ResourceDemandListener;

import de.iwes.tools.system.supervision.gui.model.SupervisionMessageSettings;
import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;

// TODO send message when ram or disk usage exceeds allowed level? (append own config to supervision config)
@Component(specVersion = "1.2")
@Service(Application.class)
public class SystemSupervisionGui implements Application, ResourceDemandListener<SupervisionMessageSettings> {

	final static String URL_BASE = "/de/iwes/tools/system/supervision";
	private WidgetApp wapp;
	private ApplicationManager am;
	private final Map<String, Messenger> messageSupervisions = new HashMap<>();
	
	@Reference
	private OgemaGuiService widgetService;
	
	@Override
	public void start(ApplicationManager appManager) {
		this.am = appManager;
		this.wapp = widgetService.createWidgetApp(URL_BASE, appManager);
		appManager.getResourceAccess().addResourceDemand(SupervisionMessageSettings.class, this);
		new SystemSupervisionPage(wapp.createStartPage(), appManager);
		widgetService.getMessagingService().registerMessagingApp(am.getAppID(), "System supervision");
	}

	@Override
	public void stop(AppStopReason reason) {
		if (am != null)
			am.getResourceAccess().removeResourceDemand(SupervisionMessageSettings.class, this);
		if (wapp != null) 
			wapp.close();
		for (Messenger m : messageSupervisions.values()) {
			m.close();
		}
		try {
			widgetService.getMessagingService().unregisterMessagingApp(am.getAppID());
		} catch (Exception e) {}
		messageSupervisions.clear();
		am = null;
		wapp = null;
	}

	@Override
	public void resourceAvailable(SupervisionMessageSettings resource) {
		resourceUnavailable(resource);
		final Messenger m = new Messenger(resource, am, widgetService.getMessagingService());
		messageSupervisions.put(resource.getLocation(), m);
	}

	@Override
	public void resourceUnavailable(SupervisionMessageSettings resource) {
		final Messenger m = messageSupervisions.remove(resource.getLocation());
		if (m != null)
			m.close();
	}

	
	
	
}
