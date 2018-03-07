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
package com.example.driver.template;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import com.example.driver.template.pattern.TemplateDriverPattern;
import com.example.driver.template.patternlistener.TemplateDriverListener;

@Component(specVersion = "1.2")
@Service(Application.class)
public class TemplateDriver implements Application {
	public static final String urlPath = "/com/example/app/urlPath";

    private OgemaLogger log;
    private ApplicationManager appManager;
    private ResourcePatternAccess patternAccess;
	private TemplateDriverListener connectionListener;
	private TemplateConnectionManager connectionManager;

    /*
     * This is the entry point to the application.
     */
 	@Override
    public void start(ApplicationManager appMan) {

        // Remember framework references for later.
        appManager = appMan;
        patternAccess = appManager.getResourcePatternAccess();
        log = appManager.getLogger();
        connectionManager = new TemplateConnectionManager(appMan);
        connectionListener = new TemplateDriverListener(appManager);
        patternAccess.addPatternDemand(TemplateDriverPattern.class, connectionListener, AccessPriority.PRIO_LOWEST);    
        log.info("{} started", getClass().getName());
   }

     /*
     * Callback called when the application is going to be stopped.
     */
    @Override
    public void stop(AppStopReason reason) {
        log.info("{} being stopped", getClass().getName());
        patternAccess.removePatternDemand(TemplateDriverPattern.class, connectionListener);
        if(connectionManager != null) {
        	connectionManager.close();
        	connectionManager = null;
        }
        connectionListener = null;
        appManager = null;
        patternAccess = null;
        log = null;
    }
}
