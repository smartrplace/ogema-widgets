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

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import com.example.app.template.config.TemplateConfig;
import com.example.app.template.pattern.TemplateContextPattern;
import com.example.app.template.pattern.TemplatePattern;
import com.example.app.template.patternlistener.TemplateSensorContextListener;
import com.example.app.template.patternlistener.TemplateSensorListener;

// here the controller logic is implemented
public class TemplateController {

	public OgemaLogger log;
    public ApplicationManager appMan;
    private ResourcePatternAccess advAcc;

	public TemplateConfig appConfigData;
	
	//add more listeners here
	public TemplateSensorListener templateSensorListener;
	public TemplateSensorContextListener templateSensorContextListener;
	
    public TemplateController(ApplicationManager appMan) {
		this.appMan = appMan;
		this.log = appMan.getLogger();
		this.advAcc = appMan.getResourcePatternAccess();
		
        initConfigurationResource();
        initDemands();
	}

    private void initConfigurationResource() {
		String configResourceDefaultName = TemplateConfig.class.getSimpleName().substring(0, 1).toLowerCase()+TemplateConfig.class.getSimpleName().substring(1);
		appConfigData = appMan.getResourceAccess().getResource(configResourceDefaultName);
		if (appConfigData != null) { // resource already exists (appears in case of non-clean start)
			appMan.getLogger().debug("{} started with previously-existing config resource", getClass().getName());
		}
		else {
			appConfigData = (TemplateConfig) appMan.getResourceManagement().createResource(configResourceDefaultName, TemplateConfig.class);
			appConfigData.sampleElement().create();
			appConfigData.sampleElement().setValue("Example");
			appConfigData.activate(true);
			appMan.getLogger().debug("{} started with new config resource", getClass().getName());
		}
    }
    
    /*
     * register ResourcePatternDemands. The listeners will be informed about new and disappearing
     * patterns in the OGEMA resource tree
     */
    public void initDemands() {
        templateSensorListener = new TemplateSensorListener(this);
        templateSensorContextListener = new TemplateSensorContextListener(this);
        advAcc.addPatternDemand(TemplatePattern.class, templateSensorListener, AccessPriority.PRIO_LOWEST);    	
        advAcc.addPatternDemand(TemplateContextPattern.class, templateSensorContextListener, AccessPriority.PRIO_LOWEST, this);    	
    }

	public void close() {
    	advAcc.removePatternDemand(TemplatePattern.class, templateSensorListener);
    	advAcc.removePatternDemand(TemplateContextPattern.class, templateSensorContextListener);
   }

	/*
	 * if the app needs to consider dependencies between different pattern types,
	 * they can be processed here.
	 */
	public void processInterdependies() {
		// TODO Auto-generated method stub
		
	}
}
