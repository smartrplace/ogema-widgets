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
package org.ogema.widgets.update.test;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.widgets.update.test.gui.StartPageBuilder;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;

@Component(specVersion = "1.2")
@Service(Application.class)
public class WidgetTest implements Application {

	protected OgemaLogger logger;
	protected ApplicationManager appMan;
	private WidgetApp wapp;

    @Reference
    private OgemaGuiService widgetService;

	@Override
	public void start(ApplicationManager appManager) {
		this.appMan = appManager;
		this.logger = appManager.getLogger();
		this.wapp = widgetService.createWidgetApp("/widgets/update/test", appManager);
		wapp.createLazyStartPage(new StartPageBuilder());
//		WidgetPage<?> page = wapp.createStartPage();
//		StartPageBuilder pagebuilder = new StartPageBuilder(page);
		logger.info("{} started",getClass().getName());
	}

    @Override
	public void stop(AppStopReason reason) {
    	if (wapp != null)
    		wapp.close();
    	wapp = null;
    	appMan = null;
    	if (logger != null)
    		logger.info("{} closing down",getClass().getName());
    	logger = null;
	}

}