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

package org.ogema.widgets.trigger.level.test;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.widgets.trigger.level.test.gui.DownloadPage;
import org.ogema.widgets.trigger.level.test.gui.FileUploadPage;
import org.ogema.widgets.trigger.level.test.gui.FlexboxPage;
import org.ogema.widgets.trigger.level.test.gui.IconWidthPage;
import org.ogema.widgets.trigger.level.test.gui.LazyLibLoadingPage;
import org.ogema.widgets.trigger.level.test.gui.MultiselectPage;
import org.ogema.widgets.trigger.level.test.gui.PlotPage;
import org.ogema.widgets.trigger.level.test.gui.SelectionTreePage;
import org.ogema.widgets.trigger.level.test.gui.StartPageBuilder;
import org.ogema.widgets.trigger.level.test.gui.TriggeredByPage;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.navigation.NavigationMenu;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;

@Component(specVersion = "1.2")
@Service(Application.class)
public class WidgetTest implements Application {

	protected OgemaLogger logger;
	protected ApplicationManager appMan;
	private WidgetApp wapp;

    @Reference
    private OgemaGuiService widgetService;
    
    @Reference
    private OnlineTimeSeriesCache onlineTimeSeriesCache;

	@Override
	public void start(ApplicationManager appManager) {
		this.appMan = appManager;
		this.logger = appManager.getLogger();
		this.wapp = widgetService.createWidgetApp("/de/iwes/widgets/tests/trigger/level", appManager);
		final WidgetPage<?> page = wapp.createStartPage();
		new StartPageBuilder(page);
		final WidgetPage<?> page2 = wapp.createWidgetPage("selection.html");
		new SelectionTreePage(page2, appManager, widgetService.getNameService(), onlineTimeSeriesCache);
		final WidgetPage<?> page3 = wapp.createWidgetPage("libloading.html");
		new LazyLibLoadingPage(page3);
		final WidgetPage<?> page4 = wapp.createWidgetPage("iconwidth.html");
		new IconWidthPage(page4);
		final WidgetPage<?> flexboxPage = wapp.createWidgetPage("flexbox.html");
		new FlexboxPage(flexboxPage);
		final WidgetPage<?> multiselectPage = wapp.createWidgetPage("multiselect.html");
		new MultiselectPage(multiselectPage);
		final WidgetPage<?> plotPage = wapp.createWidgetPage("plots.html");
		new PlotPage(plotPage, appManager);
		final WidgetPage<?> fileUploadPage = wapp.createWidgetPage("fileupload.html");
		new FileUploadPage(fileUploadPage, appManager);
		final WidgetPage<?> downloadPage = wapp.createWidgetPage("download.html");
		new DownloadPage(downloadPage, appManager);
		final WidgetPage<?> triggeredByPage = wapp.createWidgetPage("triggered.html");
		new TriggeredByPage(triggeredByPage);
		
		logger.info("{} started",getClass().getName());
		
		// menu
		final NavigationMenu menu = new NavigationMenu(" Browse pages");
		menu.addEntry("Start page", page);
		menu.addEntry("Selection page", page2);
		menu.addEntry("Lib loading page", page3);
		menu.addEntry("Icon width page", page4);
		menu.addEntry("Flexbox page", flexboxPage);
		menu.addEntry("Triggering page", triggeredByPage);
		menu.addEntry("Multiselect page", multiselectPage);
		menu.addEntry("Schedule plots page", plotPage);
		menu.addEntry("File upload page", fileUploadPage);
		menu.addEntry("Download page", downloadPage);
		page.getMenuConfiguration().setCustomNavigation(menu);
		page2.getMenuConfiguration().setCustomNavigation(menu);
		page3.getMenuConfiguration().setCustomNavigation(menu);
		page4.getMenuConfiguration().setCustomNavigation(menu);
		flexboxPage.getMenuConfiguration().setCustomNavigation(menu);
		plotPage.getMenuConfiguration().setCustomNavigation(menu);
		fileUploadPage.getMenuConfiguration().setCustomNavigation(menu);
		downloadPage.getMenuConfiguration().setCustomNavigation(menu);
		multiselectPage.getMenuConfiguration().setCustomNavigation(menu);
		triggeredByPage.getMenuConfiguration().setCustomNavigation(menu);
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