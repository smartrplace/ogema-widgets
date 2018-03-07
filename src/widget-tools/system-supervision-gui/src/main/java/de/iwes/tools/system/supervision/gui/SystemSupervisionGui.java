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
@Component(immediate = true, specVersion = "1.2")
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
