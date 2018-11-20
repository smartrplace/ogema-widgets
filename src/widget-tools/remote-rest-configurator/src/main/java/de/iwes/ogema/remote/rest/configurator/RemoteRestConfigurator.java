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
package de.iwes.ogema.remote.rest.configurator;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.Resource;

import de.iwes.ogema.remote.rest.connector.model.RestConnection;
import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;
import de.iwes.widgets.pattern.widget.patternedit.PatternCreatorConfiguration;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageUtil;

@Component(specVersion = "1.2")
@Service(Application.class)
public class RemoteRestConfigurator implements Application {

    private OgemaLogger logger;
    private ApplicationManager appMan;
    private WidgetApp wApp;

    @Reference
    private OgemaGuiService guiService;
    
    @Override
    public void start(ApplicationManager appManager) {
        this.appMan = appManager;
        this.logger = appManager.getLogger();
        logger.info("{} started", getClass().getName());
        wApp = guiService.createWidgetApp("/de/iwes/ogema/remote/rest/configurator", appManager);
        PatternCreatorConfiguration<RestConnectorPattern, RestConnection> config = 
        		new PatternCreatorConfiguration<RestConnectorPattern, RestConnection>(Resource.class, true, null);
        
        PatternPageUtil util = PatternPageUtil.getInstance(appManager, wApp);
        WidgetPage<?> createPage = util.newPatternCreatorPage(RestConnectorPattern.class, "createConnection.html", true, config, null).getPage();       
        WidgetPage<?> editPage = util.newPatternEditorPage(RestConnectorPattern.class, "editConnection.html").getPage();
        
		/* Set navigation menu */

		NavigationMenu customMenu = new NavigationMenu(" Select page");
		customMenu.addEntry("Create connection", createPage);
		customMenu.addEntry("Edit connections", editPage);
		
		createPage.getMenuConfiguration().setCustomNavigation(customMenu);
		editPage.getMenuConfiguration().setCustomNavigation(customMenu);
        
    }

    @Override
    public void stop(AppStopReason reason) {
    	if (wApp != null)
    		wApp.close();
    	wApp = null;
    	if (logger != null)
    		logger.info("{} stopped", getClass().getName());
        appMan = null;
        logger = null;
    }


}
