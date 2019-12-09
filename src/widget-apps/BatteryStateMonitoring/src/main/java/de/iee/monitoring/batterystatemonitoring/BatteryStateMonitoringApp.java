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

package de.iee.monitoring.batterystatemonitoring;


import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;

import de.iee.monitoring.batterystatemonitoring.gui.MainPageCopy;
import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;

/**
 * Battery state monitoring.
 * Checks every ten minutes whether a low battery has been detected.
 * Once this has happened, checks only once a day. 
 * Configuration via a simple gui.
 * 
 * TODO tests
 */
@Component(specVersion = "1.2")
@Service(Application.class)
public class BatteryStateMonitoringApp implements Application {
	
	private static final String PROPERTY_WARN_ALL_BATTERIES = "de.iee.ogema.batterymonitoring.enableall";
	private static final String urlPath = "/de/iee/ogema/batterystatemonitoring"; // FIXME com/example/app?
	public static final String BASE_PATH = "batteryStateMonitoringConfig";
	
    private OgemaLogger log;
    private ApplicationManager appMan;
    private BatteryStateMonitoringController controller;
	private WidgetApp widgetApp;

	@Reference
	private OgemaGuiService guiService;
	
    /*
     * This is the entry point to the application.
     */
 	@Override
    public void start(ApplicationManager appManager) {

        // Remember framework references for later.
        appMan = appManager;
        log = appManager.getLogger();
		guiService.getMessagingService().registerMessagingApp(appMan.getAppID(), "Battery_State_Monitoring_App");
        this.controller = new BatteryStateMonitoringController(appMan, guiService.getMessagingService(), guiService.getNameService());
        try {
			if (Boolean.getBoolean(PROPERTY_WARN_ALL_BATTERIES)) {
				controller.enableMonitoringForAllBatteries();
			}
        } catch (SecurityException ok) {}
		//register a web page with dynamically generated HTML
		widgetApp = guiService.createWidgetApp(urlPath, appManager);
		widgetApp.createLazyStartPage(page -> new MainPageCopy(page, appManager, controller.getConfigs()));
		/*
		final WidgetPage<?> page = widgetApp.createStartPage();
		new MainPage(page, appMan, HmMaintenance.class);
		*/
 	}

     /*
     * Callback called when the application is going to be stopped.
     */
    @Override
    public void stop(AppStopReason reason) {
    	if (widgetApp != null) 
    		widgetApp.close();
		if(controller != null)
			controller.close();
		log.info("{} stopped", getClass().getName());
		widgetApp = null;
		appMan = null;
		controller = null;
    }
}
