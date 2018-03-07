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
package de.iwes.apps.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;

import de.iwes.apps.collection.pages.AdminApps;
import de.iwes.apps.collection.pages.DeviceMgmtApps;
import de.iwes.apps.collection.pages.DriverApps;
import de.iwes.apps.collection.pages.MessagingApps;
import de.iwes.apps.collection.pages.ScheduleApps;
import de.iwes.tools.apps.collections.api.AdminApp;
import de.iwes.tools.apps.collections.api.DeviceManagementApp;
import de.iwes.tools.apps.collections.api.DisplayableApp;
import de.iwes.tools.apps.collections.api.DriverApp;
import de.iwes.tools.apps.collections.api.MessagingApp;
import de.iwes.tools.apps.collections.api.ScheduleApp;
import de.iwes.tools.apps.collections.base.AppCollection;
import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.navigation.MenuConfiguration;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;

@Component(specVersion = "1.2")
@Service(Application.class)
@References({
	@Reference(referenceInterface=AdminApp.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC, 
		bind="addAdminApp",
		unbind="removeAdminApp"),
	@Reference(referenceInterface=ScheduleApp.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC, 
		bind="addScheduleApp",
		unbind="removeScheduleApp"),
	@Reference(referenceInterface=DriverApp.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC, 
		bind="addDriverApp",
		unbind="removeDriverApp"),
	@Reference(referenceInterface=DeviceManagementApp.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC, 
		bind="addDeviceMgmtApp",
		unbind="removeDeviceMgmtApp"),
	@Reference(referenceInterface=MessagingApp.class,
		cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE,
		policy=ReferencePolicy.DYNAMIC, 
		bind="addMessagingApp",
		unbind="removeMessagingApp")
})
public class AdminAppsApp implements Application {

	private final List<DisplayableApp> serviceApps = new ArrayList<>();
	private final Queue<DisplayableApp> unprocessedApps = new LinkedTransferQueue<>();
	private WidgetApp wapp;
	private volatile AdminApps adminApps;
	private volatile ScheduleApps scheduleApps;
	private volatile DriverApps driverApps;
	private volatile DeviceMgmtApps deviceMgmtApps;
	private volatile MessagingApps messagingApps;
	
	@Reference
	private OgemaGuiService widgetService;
	
	@Override
	public void start(ApplicationManager appManager) {
		wapp = widgetService.createWidgetApp("/de/iwes/tools/app-collections", appManager);
		adminApps = new AdminApps(appManager, wapp, "admin-apps.html", true);
		scheduleApps = new ScheduleApps(appManager, wapp, "schedule-apps.html", false);
		driverApps = new DriverApps(appManager, wapp, "driver-configurations.html", false);
		deviceMgmtApps = new DeviceMgmtApps(appManager, wapp, "device-apps.html", false);
		messagingApps = new MessagingApps(appManager, wapp, "messaging-apps.html", false);
		processApps();
		
		final NavigationMenu menu = new NavigationMenu(" Select page");
		menu.addEntry("Admin apps", adminApps.getPage());
		menu.addEntry("Time series app", scheduleApps.getPage());
		menu.addEntry("Driver configurations", driverApps.getPage());
		menu.addEntry("Device/Room management", deviceMgmtApps.getPage());
		menu.addEntry("Messaging apps", messagingApps.getPage());
		configureMenu(adminApps, menu);
		configureMenu(scheduleApps, menu);
		configureMenu(driverApps, menu);
		configureMenu(deviceMgmtApps, menu);
		configureMenu(messagingApps, menu);
	}
	
	private static void configureMenu(AppCollection<?> app, NavigationMenu menu) {
		final MenuConfiguration config = app.getPage().getMenuConfiguration();
		config.setCustomNavigation(menu);
		config.setNavigationVisible(false);
	}

	@Override
	public void stop(AppStopReason reason) {
		if (wapp != null)
			wapp.close();
		wapp = null;
		adminApps = null;
		scheduleApps = null;
		driverApps = null;
		deviceMgmtApps = null;
		messagingApps = null;
		synchronized (this) {
			unprocessedApps.clear();
			unprocessedApps.addAll(serviceApps);
		}
	}

	private synchronized void processApps() {
		while (!unprocessedApps.isEmpty()) {
			DisplayableApp app = unprocessedApps.poll();
			if (app instanceof AdminApp) {
				adminApps.addApp((AdminApp) app);
			} else if (app instanceof ScheduleApp) {
				scheduleApps.addApp((ScheduleApp) app);
			} else if (app instanceof DriverApp) {
				driverApps.addApp((DriverApp) app);
			} else if (app instanceof DeviceManagementApp) {
				deviceMgmtApps.addApp((DeviceManagementApp) app);
			} else if (app instanceof MessagingApp) {
				messagingApps.addApp((MessagingApp) app);
			}
		}
	}
	
	protected synchronized void addApp(DisplayableApp app) {
		serviceApps.add(app);
		unprocessedApps.add(app);
		if (adminApps != null)
			processApps();
	}

	protected void removeApp(final DisplayableApp app) {
		synchronized (this) {
			serviceApps.remove(app);
		}
		if (app instanceof AdminApp) {
			final AdminApps adminApps = this.adminApps;
			if (adminApps != null)
				adminApps.removeApp((AdminApp) app);
		} else if (app instanceof ScheduleApp) {
			final ScheduleApps scheduleApps = this.scheduleApps;
			if (scheduleApps != null)
				scheduleApps.removeApp((ScheduleApp) app);
		} else if (app instanceof DriverApp) {
			final DriverApps driverApps = this.driverApps;
			if (driverApps != null)
				driverApps.removeApp((DriverApp) app);
		} else if (app instanceof DeviceManagementApp) {
			final DeviceMgmtApps deviceApps = this.deviceMgmtApps;
			if (deviceApps != null)
				deviceApps.removeApp((DeviceManagementApp) app);
		} else if (app instanceof MessagingApp) {
			final MessagingApps messagingApps = this.messagingApps;
			if (messagingApps != null)
				messagingApps.removeApp((MessagingApp) app);
		}
		
	}
	
	protected void addScheduleApp(ScheduleApp a) {
		addApp(a);
	}
	
	protected void addAdminApp(AdminApp a) {
		addApp(a);
	}
	
	protected void addDriverApp(DriverApp a) {
		addApp(a);
	}
	
	protected void addDeviceMgmtApp(DeviceManagementApp a) {
		addApp(a);
	}
	
	protected void addMessagingApp(MessagingApp a) {
		addApp(a);
	}
	
	protected void removeScheduleApp(ScheduleApp a) {
		removeApp(a);
	}
	
	protected void removeAdminApp(AdminApp a) {
		removeApp(a);
	}
	
	protected void removeDriverApp(DriverApp a) {
		removeApp(a);
	}
	
	protected void removeDeviceMgmtApp(DeviceManagementApp a) {
		removeApp(a);
	}
	
	protected void removeMessagingApp(MessagingApp a) {
		removeApp(a);
	}
}
