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
 * Copyright 2014 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package com.example.app.template;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;

import com.example.app.template.gui.MainPage;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

@Component(specVersion = "1.2")
@Service(Application.class)
public class TemplateApp implements Application {
	public static final String urlPath = "/com/example/app/urlPath";
    private OgemaLogger log;
    private ApplicationManager appMan;
    private WidgetApp widgetApp;
    private TemplateController controller;

	//TODO: optional
    MainPage mainPage;
    
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

        // 
        controller = new TemplateController(appMan);
        
        // register static web resources
        //String path1 = appManager.getWebAccessManager().registerWebResource("com/example/app/template","com/example/app/template/gui");
        //log.info("Registered template gui on " + path1);

        // register a web page with dynamically generated HTML
        //String path2 = "/com/example/app/template2";
        widgetApp = guiService.createWidgetApp(urlPath, appManager);
        WidgetPage<?> page = widgetApp.createStartPage();
        
        mainPage = new MainPage(page, appMan);
        
    }

     /*
     * Callback called when the application is going to be stopped.
     */
    @Override
    public void stop(AppStopReason reason) {
    	if (controller != null)
    		controller.close();
        if (widgetApp != null)
        	widgetApp.close();
        log.info("{} stopped", getClass().getName());
        controller = null;
        widgetApp = null;
        appMan = null;
        log = null;
        mainPage = null;
    }
}
