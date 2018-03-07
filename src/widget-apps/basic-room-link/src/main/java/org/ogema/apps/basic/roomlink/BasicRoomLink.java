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

package org.ogema.apps.basic.roomlink;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageUtil;

@Component(specVersion = "1.2")
@Service(Application.class)
public class BasicRoomLink implements Application {

    private WidgetApp widgetApp;

    @Reference
    private OgemaGuiService widgetService;

    @Override
    public void start(ApplicationManager appManager) {

        appManager.getLogger().debug("{} started", getClass().getName());
        widgetApp = widgetService.createWidgetApp("/de/iwes/apps/basic-room-link", appManager);
//        roomAssignPage = (WidgetPageSimple) patternPageUtil.editPatternPage(DevicePattern.class, widgetApp, "roomAssign.html", appManager, true,null);
        PatternPageUtil.getInstance(appManager, widgetApp).newPatternEditorPage(DevicePattern.class, "roomAssign.html", true, null);
   
    }

    @Override
    public void stop(AppStopReason reason) {
    	if (widgetApp != null)
    		widgetApp.close();
    	widgetApp = null;
    	LoggerFactory.getLogger(getClass()).debug("{} stopping",getClass().getName());
    }


}
