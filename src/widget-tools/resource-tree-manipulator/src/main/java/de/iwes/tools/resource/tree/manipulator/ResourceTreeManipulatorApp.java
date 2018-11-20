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
package de.iwes.tools.resource.tree.manipulator;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;

@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class ResourceTreeManipulatorApp implements Application {

	private OgemaLogger logger;
	private WidgetApp wApp;
	
	@Reference
	private OgemaGuiService guiService;
	
	@Override
	public void start(ApplicationManager appManager) {
		this.logger = appManager.getLogger();
		logger.info("{} started", getClass().getName());
		
		wApp = guiService.createWidgetApp("/de/iwes/tools/resource/tree/manipulator", appManager);
		WidgetPage<?> page = wApp.createStartPage();
		page.showOverlay(true);
		new ManipulatorPageBuilder(page, appManager);
		
	}

	@Override
	public void stop(AppStopReason reason) {
		if (logger != null)
			logger.info("{} stopped", getClass().getName());
		this.logger = null;
		if (wApp != null)
			wApp.close();
		wApp = null;
	}

}