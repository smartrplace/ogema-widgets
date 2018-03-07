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

package de.iwes.widgets.test3.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;
import de.iwes.widgets.test3.template.impl.TemplatePageBuilder;

@Component(specVersion = "1.2", immediate=true)
@Service(Application.class)
public class WidgetTest implements Application {

	public final static String GOOGLE_API_PROP = "de.iwes.widgets.testapp3.googlekey";
	public final static String BING_API_PROP = "de.iwes.widgets.testapp3.bingkey";
	public final static String ICONS_BASE = "/de/iwes/widgets/test3resources";
	protected OgemaLogger logger;
	protected ApplicationManager appMan;
	private WidgetApp wapp;

    @Reference
    private OgemaGuiService widgetService;

	@Override
	public void start(ApplicationManager appManager) {
		this.appMan = appManager;
		this.logger = appManager.getLogger();
		this.wapp = widgetService.createWidgetApp("/de/iwes/widgets/test3", appManager);
		WidgetPage<?> page = wapp.createStartPage();
		final String googleApiKey = System.getProperty(GOOGLE_API_PROP);
		final String bingApiKey = System.getProperty(BING_API_PROP);

//		if (apiKey == null) {
//			throw new RuntimeException("Googlemaps API key not set, cannot start test app: " + API_PROP);
//		}
		StartPageBuilder pagebuilder = new StartPageBuilder(page, googleApiKey, bingApiKey, logger);
		appManager.getWebAccessManager().registerWebResource(ICONS_BASE, "icons");
		
		final WidgetPage<?> templatePage = wapp.createWidgetPage("templatePage.html");
		new TemplatePageBuilder(templatePage, googleApiKey, bingApiKey, logger);
		
		final NavigationMenu menu = new NavigationMenu(" Select page");
		menu.addEntry("Basic map", page);
		menu.addEntry("Template map", templatePage);
		
		templatePage.getMenuConfiguration().setCustomNavigation(menu);
		page.getMenuConfiguration().setCustomNavigation(menu);
		
		logger.info("{} started",getClass().getName());
	}

    @Override
	public void stop(AppStopReason reason) {
    	if (wapp != null)
    		wapp.close();
    	wapp = null;
    	if (appMan != null) {
    		appMan.getWebAccessManager().unregisterWebResource(ICONS_BASE);
    	}
    	appMan = null;
    	if (logger != null)
    		logger.info("{} closing down",getClass().getName());
    	logger = null;
	}

}