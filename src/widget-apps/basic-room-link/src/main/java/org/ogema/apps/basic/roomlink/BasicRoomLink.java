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
package org.ogema.apps.basic.roomlink;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.osgi.framework.Constants;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.OgemaGuiService;
import de.iwes.widgets.api.widgets.WidgetApp;
import de.iwes.widgets.pattern.widget.patternedit.PatternPageUtil;

@Component(specVersion = "1.2")
@Service(Application.class)
@Properties({
	@Property(name="org.ogema.apps.device.mgmt", value="org.ogema.model.locations.Room"), // announce itself as a rooms managing app, but with low ranking
	@Property(name=Constants.SERVICE_RANKING, intValue=-1)
})
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
