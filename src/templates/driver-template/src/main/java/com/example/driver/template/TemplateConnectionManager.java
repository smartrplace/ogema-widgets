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

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.simple.FloatResource;

import com.example.driver.template.drivermodel.TemplateConfig;
import com.example.driver.template.drivermodel.TemplateDriverModel;

public class TemplateConnectionManager {
	public OgemaLogger log;
    public ApplicationManager appMan;

	public TemplateConfig appConfigData;

    public TemplateConnectionManager(ApplicationManager appMan) {
		this.appMan = appMan;
		this.log = appMan.getLogger();
		
        initConfigurationResource();
 	}

    public void createNewConnection(FloatResource value, Object configurationData ) {
		TemplateDriverModel newConnection = appConfigData.connections().add();
		newConnection.value().setAsReference(value);
		//TODO set other relevant data of the connection obtained from configurationData
		newConnection.activate(true);
	}

    /*
     * This app uses a central configuration resource, which is accessed here
     */
    private void initConfigurationResource() {
		String configResourceDefaultName = TemplateConfig.class.getSimpleName().substring(0, 1).toLowerCase()+TemplateConfig.class.getSimpleName().substring(1);
		appConfigData = appMan.getResourceAccess().getResource(configResourceDefaultName);
		if (appConfigData != null) { // resource already exists (appears in case of non-clean start)
			appMan.getLogger().debug("{} started with previously-existing config resource", getClass().getName());
		}
		else {
			appConfigData = (TemplateConfig) appMan.getResourceManagement().createResource(configResourceDefaultName, TemplateConfig.class);
			appConfigData.activate(true);
			appMan.getLogger().debug("{} started with new config resource", getClass().getName());
		}
    }
    
    public void close() {
    	
    }
}
