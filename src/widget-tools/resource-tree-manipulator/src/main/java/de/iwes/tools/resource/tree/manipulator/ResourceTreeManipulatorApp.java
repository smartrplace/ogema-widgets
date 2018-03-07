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